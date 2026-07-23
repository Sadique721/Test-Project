package com.savbill.ticketmanagement.core.controller;

import com.savbill.ticketmanagement.core.modules.constants.UrlConstants;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class APITicketController {

    Logger log = LoggerFactory.getLogger(APIResponseController.class);

    @ApiOperation(value = "Used to check whether radius service is up or not.")
    @GetMapping("/serviceStatus")
    public String checkServiceStatus() {
        try {
            log.debug("Ticket Service is Up");
            return "{\"success\": true,\"message\": \"Ticket Service is Up.\"}";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
