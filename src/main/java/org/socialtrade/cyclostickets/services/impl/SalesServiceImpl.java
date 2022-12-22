package org.socialtrade.cyclostickets.services.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.socialtrade.cyclostickets.apimodels.TicketNew;
import org.socialtrade.cyclostickets.apimodels.TicketProcessResponse;
import org.socialtrade.cyclostickets.apimodels.TicketResult;
import org.socialtrade.cyclostickets.models.Sale;
import org.socialtrade.cyclostickets.services.ConnectionService;
import org.socialtrade.cyclostickets.services.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

/**
 * Implementation for {@link SalesService}
 */
@Service
public class SalesServiceImpl extends BaseService implements SalesService {

    private File salesDir;

    @Autowired
    private ConnectionService connectionService;

    @PostConstruct
    public void initialize() {
        var dir = cyclosConfig.getDataDir();
        salesDir = new File(dir, "sales");
        salesDir.mkdirs();
    }

    @Override
    public Sale create(BigDecimal amount, String description) {
        var sale = new Sale();
        sale.setId(UUID.randomUUID());
        sale.setCreationDate(OffsetDateTime.now());
        sale.setAmount(amount);
        sale.setDescription(description);
        save(sale);
        return sale;
    }

    @Override
    public Sale find(UUID id) {
        var file = new File(salesDir, id + ".json");
        try {
            return objectMapper.readValue(file, Sale.class);
        } catch (IOException e) {
        }
        throw new IllegalArgumentException("Sale not found: " + id);
    }

    @Override
    public List<Sale> list() {
        return Stream.of(salesDir.listFiles())
                .map(f -> {
                    try {
                        return objectMapper.readValue(f, Sale.class);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Sale::getCreationDate))
                .collect(Collectors.toList());
    }

    @Override
    public String prepareTicket(UUID id) {
        var sale = find(id);

        // Prepare the ticket parameters
        var ticket = new TicketNew();
        ticket.setAmount(sale.getAmount());
        ticket.setDescription(sale.getDescription());
        ticket.setOrderId(id.toString());
        ticket.setCancelUrl(cyclosConfig.appUri("/sales/cancel").toASCIIString());
        ticket.setSuccessUrl(cyclosConfig.appUri("/sales/callback").toASCIIString());

        // Create the new ticket
        HttpResponse<TicketResult> response;
        try {
            response = http.send(HttpRequest.newBuilder()
                    .uri(cyclosConfig.serverUriBuilder()
                            .path("/api/self/tickets")
                            .queryParam("fields", "ticketNumber")
                            .build()
                            .toUri())
                    .POST(jsonBodyHelper.publisher(ticket))
                    .header("Authorization", "Bearer " + connectionService.getAccessToken())
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .build(),
                    jsonBodyHelper.handler(TicketResult.class));
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        // Return the URL to pay the ticket
        var ticketNumber = response.body().getTicketNumber();
        return cyclosConfig.serverUri("/pay/" + ticketNumber).toASCIIString();
    }

    @Override
    public void processTicket(UUID id, String ticketNumber) {
        var sale = find(id);

        // Create the new ticket
        HttpResponse<TicketProcessResponse> response;
        try {
            response = http.send(HttpRequest.newBuilder()
                    .uri(cyclosConfig.serverUriBuilder()
                            .pathSegment("api", "tickets", ticketNumber, "process")
                            .queryParam("orderId", id)
                            .queryParam("fields", "actuallyProcessed", "transaction.transactionNumber")
                            .build()
                            .toUri())
                    .POST(BodyPublishers.noBody())
                    .header("Authorization", "Bearer " + connectionService.getAccessToken())
                    .build(),
                    jsonBodyHelper.handler(TicketProcessResponse.class));
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        // Update the sale with the payment details
        var result = response.body();
        if (result.isActuallyProcessed()) {
            sale.setTransactionNumber(result.getTransaction().getTransactionNumber());
            sale.setProcessDate(OffsetDateTime.now());
            save(sale);
        }
    }

    private void save(Sale sale) {
        // Persist to disk
        var file = new File(salesDir, sale.getId() + ".json");
        try {
            objectMapper.writeValue(file, sale);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
