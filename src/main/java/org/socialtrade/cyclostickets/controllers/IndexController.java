package org.socialtrade.cyclostickets.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the index page
 */
@Controller
public class IndexController {
    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index")
            .addObject("title", "Cyclos tickets plugin using OAuth");
    }
}
