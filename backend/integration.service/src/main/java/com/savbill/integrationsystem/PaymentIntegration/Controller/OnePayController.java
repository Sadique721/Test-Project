package com.savbill.integrationsystem.PaymentIntegration.Controller;

import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Service.OnePayIntegrationService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class OnePayController {

    @Autowired
    OnePayIntegrationService onePayIntegrationService;


    @PostMapping( value = "/onePayPayment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO onePayPayment(@RequestBody CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String authToken = request.getHeader("Authorization");
            genericDataDTO  = onePayIntegrationService.OnePayPaymentInitiateService(customerPaymentDTO,authToken);
            return genericDataDTO;
        } catch (Exception e) {
            ApplicationLogger.logger.error("MomoPe Payment initiation failed " + e.getMessage());
        }
        return null;
    }

}
