package com.savbill.integrationsystem.PaymentIntegration.Controller;

import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentIntegration.DTO.SelcomAppToCRMDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.SelcomResponseDTO;
import com.savbill.integrationsystem.PaymentIntegration.Service.SelcomAppToCRMService;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.CommonConstant;
import com.savbill.integrationsystem.core.exceptions.PaymentValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@RestController()
public class SelcomAppToCRMController {


    private static final Logger logger = LoggerFactory.getLogger(SelcomAppToCRMController.class);

    @Autowired
    private SelcomAppToCRMService selcomAppToCRMService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    @PostMapping(value = "/selcomAppToCRM/validation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SelcomResponseDTO processTransaction(@RequestBody SelcomAppToCRMDTO request, HttpServletRequest req)  {
        logger.info("********** Inside Validation Method **********");
        SelcomResponseDTO response = new SelcomResponseDTO();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        try {
            selcomAppToCRMService.validateProcessTxRequestData(request);
            response = selcomAppToCRMService.processC2BRequest(request);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.OK).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.SELCOM_VALIDATION,request.getReference().toString());
            return response;
        } catch (PaymentValidationException ex) {
            response.setReference(request.getReference());
            response.setResultcode(ex.getResultCode());
            response.setResult(CommonConstant.TRANSACTION_FAILURE);
            response.setMessage(ex.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            if(request.getReference()!=null) {
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), request, ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response), headers, responseTime, requestInitiationTime, null, 1, "POST", ex.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.SELCOM_VALIDATION, request.getReference());
            }
        } catch (Exception e) {
            response.setReference(request.getReference());
            response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.FAILURE);
            response.setResult(CommonConstant.TRANSACTION_FAILURE);
            response.setMessage(e.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            if(request.getReference()!=null) {
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), request, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response), headers, responseTime, requestInitiationTime, null, 1, "POST", e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.SELCOM_VALIDATION, request.getReference());
            }
        }
        return response;

    }
    @PostMapping(value = "/selcomAppToCRM/notification", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SelcomResponseDTO transactionStatus(@RequestBody SelcomAppToCRMDTO request,HttpServletRequest req)  {
        logger.info("********** Inside transactionStatus method **********");
        SelcomResponseDTO response = new SelcomResponseDTO();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        try {
            selcomAppToCRMService.validateProcessTxRequestData(request);
            response = selcomAppToCRMService.transactionrespons(request);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.OK).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.SELCOM_NOTIFICATION,request.getReference().toString());
            return response;
        } catch (PaymentValidationException ex){
            response.setReference(request.getReference());
            response.setResultcode(ex.getResultCode());
            response.setResult(CommonConstant.TRANSACTION_FAILURE);
            response.setMessage(ex.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            if(request.getReference()!=null) {
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), request, ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response), headers, responseTime, requestInitiationTime, null, 1, "POST", ex.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.SELCOM_NOTIFICATION, request.getReference());
            }
        } catch (Exception e){
            response.setReference(request.getReference());
            response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.FAILURE);
            response.setResult(CommonConstant.TRANSACTION_FAILURE);
            response.setMessage(e.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            if(request.getReference()!=null) {
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), request, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response), headers, responseTime, requestInitiationTime, null, 1, "POST", e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.SELCOM_NOTIFICATION, request.getReference());
            }
        }
        return response;
    }

}
