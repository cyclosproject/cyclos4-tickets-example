package org.socialtrade.cyclostickets.controllers;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the error page
 */
@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView error(HttpServletRequest request) {
        var statusCode = 500;
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        String description;
        switch (statusCode) {
            case 404:
                description = "The requested page was not found";
                break;
            default:
                description = "There was an error while processing your request";
                break;
        }

        return new ModelAndView("error")
            .addObject("title", "Error")
            .addObject("error", description);
    }
}
