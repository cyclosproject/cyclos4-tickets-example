package org.socialtrade.cyclostickets.controllers;

import java.net.URI;

import org.socialtrade.cyclostickets.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the Cyclos OpenID Connect / OAuth 2 connection page
 */
@Controller
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @GetMapping("/connection")
    public ModelAndView getConnection() {
        return new ModelAndView("connection")
                .addObject("title", "Cyclos connection")
                .addObject("page", "connection")
                .addObject("connection", connectionService.getConnection());
    }

    @PostMapping("/connection")
    public ResponseEntity<Void> postConnection() {
        var url = connectionService.register();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    @PostMapping("/start-over")
    public String startOver() {
        connectionService.startOver();
        return "redirect:/connection";
    }

    @GetMapping("/redirect")
    public ModelAndView redirect(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "id_token", required = false) String idToken,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription) {

        if (error == null && code == null) {
            error = "bad_request";
            errorDescription = "Missing parameters";
        }

        if (error != null) {
            var message = "<tt>" + error + "</tt>";
            if (errorDescription != null) {
                message += "<br><br>" + errorDescription;
            }
            return new ModelAndView("error")
                .addObject("title", "Error getting the user consent")
                .addObject("error", message);
        }

        // No error - Handle the redirect request
        connectionService.redirect(code, idToken);
        return new ModelAndView("redirect:/connection");
    }

}
