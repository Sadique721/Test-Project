package com.savbill.integrationsystem.Mpesa.Controller;

import com.savbill.integrationsystem.Mpesa.RequestDTO.MpesaC2BRequestDTO;
import com.savbill.integrationsystem.Mpesa.ResponseDTO.MpesaC2BValidateResponseDTO;
import com.savbill.integrationsystem.Mpesa.Service.MpesaBrokerService;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.time.LocalDateTime;


@Slf4j
@RestController
public class MpesaC2BController {

    @Autowired
    private MpesaBrokerService mpesaBrokerService;
    @Autowired
    private ApiAuditsService apiAuditsService;


    @PostMapping("/validateC2BRequest")
    public MpesaC2BValidateResponseDTO processC2BValidateRequest(@RequestBody MpesaC2BRequestDTO requestDTO, HttpServletRequest request) throws JAXBException {
        log.info("********** Inside processC2BValidateRequest method **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        MpesaC2BValidateResponseDTO responseDTO = new MpesaC2BValidateResponseDTO();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try{
            responseDTO = mpesaBrokerService.validateC2BRequest(requestDTO, request);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            if(responseDTO == null){
                responseDTO.setResultCode("C2B00012");
                responseDTO.setResultDesc("Rejected");
            }
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),request.toString(), ResponseEntity.status(HttpStatus.OK).body(responseDTO) , headers , responseTime , requestInitiationTime , null , null, "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.MPESA_TRANSACTION,requestDTO.getTransID() != null ? requestDTO.getTransID() : "");
            return responseDTO;
        } catch (Exception e) {
            MpesaC2BValidateResponseDTO response = new MpesaC2BValidateResponseDTO();
            response.setResultCode("C2B00016");
            response.setResultDesc("Other Error");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),request.toString(), ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , null , null, "POST" , e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.MPESA_TRANSACTION,requestDTO.getTransID() != null ? requestDTO.getTransID() : "");
            log.error("error in processC2BValidateRequest", e);
            return response;
        }
    }

    /* C2B Confirmation api */
    @PostMapping("/c2b/confirmation")
    public MpesaC2BValidateResponseDTO handleConfirmationC2B(@RequestBody MpesaC2BRequestDTO requestDTO, String token, HttpServletRequest request) {
        log.info("********** MPESA C2B Confirmation request received **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        MpesaC2BValidateResponseDTO responseDTO = new MpesaC2BValidateResponseDTO();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try {
            responseDTO = mpesaBrokerService.handleC2BConfirmation(requestDTO, token, request);
            if(responseDTO != null){
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),request.toString(), ResponseEntity.status(HttpStatus.OK).body(responseDTO) , headers , responseTime , requestInitiationTime , null , null, "POST" , null, PaymentGatewayConfigurationConstant.AUDITCONSTANT.MPESA_TRANSACTION,requestDTO.getTransID() != null ? requestDTO.getTransID() : "");
                return responseDTO;
            }else {
                responseDTO.setResultCode("C2B00016");
                responseDTO.setResultDesc("Other Error");
            }
        } catch (Exception e) {
            MpesaC2BValidateResponseDTO response = new MpesaC2BValidateResponseDTO();
            response.setResultCode("C2B00016");
            response.setResultDesc("Other Error");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),request.toString(), ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , null , null, "POST" , e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.MPESA_TRANSACTION,requestDTO.getTransID() != null ? requestDTO.getTransID() : "");
            log.error("error in processC2BValidateRequest", e);
        }
        return responseDTO;
    }
}
