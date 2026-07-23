package com.savbill.integrationsystem.PaymentIntegration.Controller;

import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Service.MoMoPePaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class MoMoPaymentIntegrationController{

    @Autowired
    private MoMoPePaymentService moMoPePaymentService;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    @PostMapping("/requestToPay")
    public GenericDataDTO InitiateMomoPeRequest(@RequestBody CustomerPaymentDTO customerPaymentDTO , HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String authToken = request.getHeader("Authorization");
//            if(customerPaymentDTO.getHash() != null && !customerPaymentDTO.getHash().equalsIgnoreCase("")) {
//                ResponseEntity<Map<String, Object>> response = moMoPePaymentService.getPaymentDetailsByHash(customerPaymentDTO , authToken);
//                if (response.getStatusCode().value() == 200) {
//                    genericDataDTO = moMoPePaymentService.momoPePaymentInitiateService(customerPaymentDTO, authToken);
//                }
//            }
//            else{
                genericDataDTO = moMoPePaymentService.momoPePaymentInitiateService(customerPaymentDTO, authToken);
//            }
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_EXPECTATION_FAILED);
            genericDataDTO.setResponseMessage("Something Went Wrong Regarding MoMo Pay Initiation...");
            ApplicationLogger.logger.error("MomoPe Payment initiation failed " + e.getMessage());
            return genericDataDTO;
        }
    }

    @PostMapping("/getpaymentstatus")
    public GenericDataDTO getPaymentStatus(@RequestBody CustomerPaymentDTO customerPaymentDTO) throws Exception {
        MDC.put("type", "get");
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (customerPaymentDTO != null) {
                HashMap<String , String> hashMap = new HashMap<>();
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                hashMap.put("istransactionsuccess",paymentIntegrationService.IsTransactionStatusSuccess(customerPaymentDTO.getOrderId() , customerPaymentDTO.getStatus()).toString());
                genericDataDTO.setData(hashMap);
                genericDataDTO.setResponseMessage("transaction status found");
                RESP_CODE = APIConstants.SUCCESS;
                ApplicationLogger.logger.info("get transaction status for " + customerPaymentDTO.getOrderId() + "  :  request: { From : {}}; Response : {{}}", "TicketRemark", RESP_CODE, null);
                return  genericDataDTO;
            }
        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
           ApplicationLogger.logger.error("Transaction status" + ce.getStackTrace(), ce);
            ce.printStackTrace();
            ApplicationLogger.logger.error("Unable to get transaction status for " + customerPaymentDTO.getOrderId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "TicketRemark", RESP_CODE, null, ce.getStackTrace());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage("Error processing transaction status");
            ApplicationLogger.logger.error("Transaction status" + ex.getStackTrace(), ex);
            ex.printStackTrace();
            ApplicationLogger.logger.error("Unable to get transaction status for " + customerPaymentDTO.getOrderId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "TicketRemark", RESP_CODE, null, ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
}
