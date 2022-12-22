package org.socialtrade.cyclostickets.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.socialtrade.cyclostickets.models.Sale;

/**
 * Interface for managing the simulated sales
 */
public interface SalesService {

    /**
     * Finds a single sale, returning null when not found
     */
    Sale find(UUID id);

    /**
     * Creates a simulated sale
     */
    Sale create(BigDecimal amount, String description);

    /**
     * Returns all simulated sales
     */
    List<Sale> list();

    /**
     * Prepares a ticket for paying the given sale, returning the URL to pay it
     */
    String prepareTicket(UUID id);

    /**
     * Processes the sale with the given id using the given ticket number
     */
    void processTicket(UUID id, String ticketNumber);
}
