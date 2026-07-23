package com.savbill.integrationsystem.PaymentIntegration.Controller;

import com.savbill.integrationsystem.PaymentIntegration.Model.SelcomGeneralDTO;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
import com.savbill.integrationsystem.PaymentIntegration.Service.SelecomPaymentService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController()
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class SelcomPaymentIntegrationController {

    @Autowired
    SelecomPaymentService selecomPaymentService;

    @Autowired
    PaymentIntegrationService paymentIntegrationService;


    @PostMapping("/selcomPay")
    public GenericDataDTO InitiateSelcomPayRequest(@RequestBody SelcomGeneralDTO selcomGeneralDTO, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String authToken = request.getHeader("Authorization");
            genericDataDTO  = selecomPaymentService.selecomPaymentInitiateService(selcomGeneralDTO, authToken);
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(org.apache.http.HttpStatus.SC_EXPECTATION_FAILED);
            genericDataDTO.setResponseMessage("Something Went Wrong Regarding Selcom Pay Initiation...");
            ApplicationLogger.logger.error("Selecom Payment initiation failed " + e.getMessage());
            return genericDataDTO;
        }
    }

  /*  @PostMapping("/getOrderstatus")
    public GenericDataDTO getOrderStatus(@RequestBody CustomerPaymentDTO customerPaymentDTO) throws Exception {
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
    }*/
}
