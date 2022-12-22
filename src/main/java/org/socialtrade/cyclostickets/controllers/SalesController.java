package org.socialtrade.cyclostickets.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.UUID;

import org.socialtrade.cyclostickets.services.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the simulated sales page
 */
@Controller
public class SalesController {

    @Autowired
    private SalesService salesService;

    @GetMapping("/sales")
    public ModelAndView listSales() {
        return new ModelAndView("sales")
                .addObject("title", "Simulated sales")
                .addObject("page", "sales")
                .addObject("sales", salesService.list());
    }

    @GetMapping("/sales/new")
    public ModelAndView newSale() {
        return new ModelAndView("new-sale")
                .addObject("title", "Create sale")
                .addObject("page", "sales");
    }

    @PostMapping("/sales/save")
    public String saveSale(
        @RequestParam(name="amount") BigDecimal amount,
        @RequestParam(name="description", required=false) String description) {
            salesService.create(amount, description);
            return "redirect:/sales";
    }

    @GetMapping("/sales/pay/{id}")
    public ResponseEntity<Void> paySale(@PathVariable(name="id") UUID id) {
        var url = salesService.prepareTicket(id);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    @GetMapping("/sales/cancel")
    public ModelAndView cancelTicket(@RequestParam(name="orderId") UUID id) {
        var order = salesService.find(id);
        var message = "You can restart it in the sales page";
        if (order != null) {
            message += "<br><br>Order amount: " + order.getAmount().setScale(2, RoundingMode.HALF_UP)
                + "<br><br>Order description: " + order.getDescription();
        }
        return new ModelAndView("error")
            .addObject("title", "Cyclos ticket payment canceled")
            .addObject("error", message);
    }


    @GetMapping("/sales/callback")
    public String ticketCallback(
        @RequestParam(name="orderId") UUID id,
        @RequestParam(name="ticketNumber") String ticketNumber) {
        salesService.processTicket(id, ticketNumber);
        return "redirect:/sales";
    }

}
