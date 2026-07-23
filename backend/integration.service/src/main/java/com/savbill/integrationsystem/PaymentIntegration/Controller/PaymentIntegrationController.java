package com.savbill.integrationsystem.PaymentIntegration.Controller;

import com.savbill.integrationsystem.PaymentIntegration.DTO.*;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL + URLConstants.PHONPE_MASTER)
public class PaymentIntegrationController {


    @Autowired
    PaymentIntegrationService paymentIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(PaymentIntegrationController.class);

    @Autowired
    private ApiAuditsService apiAuditsService;

    @Autowired
    private CustomerPaymentService customerPaymentService;

//    @PostMapping("/callback")
//    public String handlePaymentCallback(HttpServletRequest request) {
//        // Log incoming parameters for debugging
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        parameterMap.forEach((key, value) -> {
//            System.out.println("Key: " + key + ", Value: " + String.join(", ", value));
//        });
//
//        // Here you can process the parameters as needed
//        // For example, you can check the payment status, order ID, etc.
//        String paymentStatus = request.getParameter("paymentStatus");
//        String orderId = request.getParameter("orderId");
//
////        // Based on the payment status, you can redirect to the appropriate page
////        if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
////            // Redirect to success page
////            return "redirect:/payment/success?orderId=" + orderId;
////        } else {
////            // Redirect to failure page
////            return "redirect:/payment/failure?orderId=" + orderId;
////        }
//        return null;
//    }



    @PostMapping("/PhonePePaymentInitiate")
    GenericDataDTO InitiatePhonePeRequest(@RequestBody CustomerPaymentDTO customerPaymentDTO){
        try{
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO =  paymentIntegrationService.phonePePaymentInitiateService(customerPaymentDTO);
            return genericDataDTO;

        }catch (Exception e){
            ApplicationLogger.logger.error("Phonepe payment initiation failed "+e.getMessage());
        }
        return null;
    }



}
