package com.savbill.integrationsystem.PaymentIntegration.Controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred.");
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
