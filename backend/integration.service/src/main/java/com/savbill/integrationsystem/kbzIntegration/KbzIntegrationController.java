package com.savbill.integrationsystem.kbzIntegration;

import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class KbzIntegrationController {

    @Autowired
    KbzIntegrationService kbzIntegrationService;


    @PostMapping("/kbzPay")
    public GenericDataDTO InitiateKbzPayRequest(@RequestBody CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String authToken = request.getHeader("Authorization");
            genericDataDTO  = kbzIntegrationService.kbzPaymentInitiateService(customerPaymentDTO, authToken);
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_EXPECTATION_FAILED);
            genericDataDTO.setResponseMessage("Something Went Wrong Regarding KBZ Pay Initiation...");
            ApplicationLogger.logger.error("KBZ Payment initiation failed " + e.getMessage());
            return genericDataDTO;
        }
    }


}
