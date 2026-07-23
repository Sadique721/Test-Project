package com.savbill.integrationsystem.PaymentIntegration.Controller;

import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Service.PayStackPaymentService;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL + URLConstants.PayStackPay.PAYSTACK_PAY)
public class PayStackIntegrationController {

    @Autowired
    private PayStackPaymentService payStackPaymentService;
    @Autowired
    private ApiAuditsService apiAuditsService;

    @PostMapping(value = URLConstants.PayStackPay.INITIATE_PAYMENT)
    public GenericDataDTO initiatePaystackPeRequest(@RequestBody CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request){
        MDC.put("type","Fetch");
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            String authToken = request.getHeader("Authorization");
            payStackPaymentService.validateRequestForInitiate(customerPaymentDTO);
            if(customerPaymentDTO.getCustomerId() != null && customerPaymentDTO.getAmount() != null){
                 dataDTO = payStackPaymentService.initiatePaymentService(customerPaymentDTO, authToken);
            }

            if (dataDTO.getResponseCode() == APIConstants.SUCCESS) {
                if(dataDTO.getData() == null){
                    dataDTO.setResponseMessage("An error occurred PayStack initiation failed");
                    dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
                }
            }

        } catch (CustomValidationException e) {
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(e.getErrCode());
        }
        catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            dataDTO.setResponseMessage(e.getMessage());
            log.error("error while handling paystack callback; Message : {}", e.getMessage(),e);
            e.printStackTrace();
        }finally {
            MDC.clear();
        }
        return  dataDTO;
    }

//    @GetMapping(value = URLConstants.PayStackPay.CALLBACK)
//    public GenericDataDTO handlePayStackCallBack(@RequestParam("trxref") String trxRef,@RequestParam("reference") String reference,HttpServletRequest request){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        LocalDateTime requestInitiationTime = LocalDateTime.now();
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("headers",request.getHeaderNames().toString());
//        try {
//            genericDataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
//            Map<String, Object> stringObjectMap = payStackPaymentService.handleCallBackFromPayStack(reference,request);
//            HashMap<String, Object> data = (HashMap<String, Object>) stringObjectMap.get("data");
//            LocalDateTime requestCompletionTime = LocalDateTime.now();
//            long measuredResponseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
//
//            if(stringObjectMap.get("status") != null && (boolean)stringObjectMap.get("status") && data.get("gateway_response").toString().equalsIgnoreCase("SUCCESSFUL")){
//                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),reference,ResponseEntity.status(APIConstants.SUCCESS).body(stringObjectMap),headers,measuredResponseTime,requestInitiationTime,payStackPaymentService.getLoggedInUser().getFullName(),payStackPaymentService.getLoggedInUser().getMvnoId(),"GET",null,"PAYSTACK-CALLBACK",reference);
//            }else {
//                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),reference,ResponseEntity.status(APIConstants.EXPECTATION_FAILED).body(stringObjectMap),headers,measuredResponseTime,requestInitiationTime,payStackPaymentService.getLoggedInUser().getFullName(),payStackPaymentService.getLoggedInUser().getMvnoId(),"GET",null,"PAYSTACK-CALLBACK",reference);
//            }
//            genericDataDTO = payStackPaymentService.processPaystackCallback(stringObjectMap);
//            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),reference,ResponseEntity.status(APIConstants.EXPECTATION_FAILED).body(stringObjectMap),headers,measuredResponseTime,requestInitiationTime,payStackPaymentService.getLoggedInUser().getFullName(),payStackPaymentService.getLoggedInUser().getMvnoId(),"GET",null,"PAYSTACK-CALLBACK",reference);
//        } catch (Exception e) {
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(e.getMessage());
//            genericDataDTO.setData(genericDataDTO.getData());
//            LocalDateTime requestCompletionTime = LocalDateTime.now();
//            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
//            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),reference,ResponseEntity.status(APIConstants.INTERNAL_SERVER_ERROR).body(genericDataDTO),headers,responseTime,requestInitiationTime,payStackPaymentService.getLoggedInUser().getFullName(),payStackPaymentService.getLoggedInUser().getMvnoId(),"GET",null,"PAYSTACK-CALLBACK",reference);
//            log.error("error while handling paystack callback; Message : {}", e.getMessage(),e);
//            e.printStackTrace();
//        }
//        return genericDataDTO;
//    }
//

    @GetMapping(value = URLConstants.PayStackPay.VERIFY_TRANSACTION)
    public GenericDataDTO verifyPayStackTransaction(@RequestParam("reference") String reference,HttpServletRequest request){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LocalDateTime requestInitiationTime = LocalDateTime.now();

        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        if(reference == null)
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"reference/OrderId cannot be null",null);
        try {
            genericDataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
            genericDataDTO = payStackPaymentService.verifyTransaction(reference);
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(e.getErrCode());
            genericDataDTO.setResponseMessage(e.getMessage());
        }catch (Exception e) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(e.getMessage());
            e.printStackTrace();
        }
        return genericDataDTO;
    }
}
