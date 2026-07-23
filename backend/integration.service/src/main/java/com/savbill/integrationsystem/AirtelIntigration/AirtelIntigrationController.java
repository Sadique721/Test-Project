package com.savbill.integrationsystem.AirtelIntigration;

import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Service.MoMoPePaymentService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class AirtelIntigrationController {

    @Autowired
    AirtelIntigrationService airtelIntigrationService;
    @Autowired
    private MoMoPePaymentService moMoPePaymentService;

    @PostMapping("/airtel/requestToPay")
    public GenericDataDTO InitiateAirtelRequest(@RequestBody CustomerPaymentDTO customerPaymentDTO , HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String authToken = request.getHeader("Authorization");
//            if(customerPaymentDTO.getHash() != null && !customerPaymentDTO.getHash().equalsIgnoreCase("")) {
//                ResponseEntity<Map<String, Object>> response = moMoPePaymentService.getPaymentDetailsByHash(customerPaymentDTO,authToken);
//                if (response.getStatusCode().value() == 200) {
//                    genericDataDTO = airtelIntigrationService.createAirtelpayment(customerPaymentDTO, authToken);
//                }
//            }
//            else{
                genericDataDTO = airtelIntigrationService.createAirtelpayment(customerPaymentDTO, authToken);
//            }
            return genericDataDTO;
        }catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.SC_EXPECTATION_FAILED);
            genericDataDTO.setData(ex.getMessage());
            ApplicationLogger.logger.error("Airtel Payment initiation failed " + ex.getMessage());
        }
        catch (Exception e) {
            ApplicationLogger.logger.error("Airtel Payment initiation failed " + e.getMessage());
        }
        return null;
    }
}
