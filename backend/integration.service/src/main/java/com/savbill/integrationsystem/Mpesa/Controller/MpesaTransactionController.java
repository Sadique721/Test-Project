package com.savbill.integrationsystem.Mpesa.Controller;

import com.savbill.integrationsystem.AirtelAppToCRM.AirtelValidateTxValidator;
import com.savbill.integrationsystem.Mpesa.RequestDTO.MpesaBrokerRequestDTO;
import com.savbill.integrationsystem.Mpesa.RequestDTO.TransactionStatusRequestDTO;
import com.savbill.integrationsystem.Mpesa.ResponseDTO.MpesaBrokerResponseDTO;
import com.savbill.integrationsystem.Mpesa.ResponseDTO.MpesaQrResponseDTO;
import com.savbill.integrationsystem.Mpesa.Service.MpesaBrokerService;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class MpesaTransactionController {

    private static final Logger logger = LoggerFactory.getLogger(MpesaTransactionController.class);
    @Autowired
    private MpesaBrokerService mpesaBrokerService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    @PostMapping(value = "/C2B-request-url", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public MpesaBrokerResponseDTO mpesaProcessTransaction(@RequestBody MpesaBrokerRequestDTO request, @RequestHeader("Authorization") String token ,HttpServletRequest req) throws JAXBException {
        logger.info("********** Inside mpesaProcessTransaction method **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());

        try {
            mpesaBrokerService.validateProcessTxRequestData(request);
            MpesaBrokerResponseDTO mpesaBrokerResponseDTO = mpesaBrokerService.processB2CRequest(request, token);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request.toString(), ResponseEntity.status(HttpStatus.OK).body(mpesaBrokerResponseDTO) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId(), "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.MPESA_TRANSACTION,request.getRequest().getTransaction().getAccountReference() != null ? request.getRequest().getTransaction().getAccountReference() : "");
            return mpesaBrokerResponseDTO;
        } catch (AirtelValidateTxValidator ex) {
            MpesaBrokerResponseDTO response = new MpesaBrokerResponseDTO();
            response.setResponseCode(String.valueOf(HttpStatus.BAD_REQUEST));
            response.setTransactionID("");
            response.setResponseDesc(ex.getLocalizedMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request.toString(), ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId(), "POST" , ex.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.MPESA_TRANSACTION,request.getRequest().getTransaction().getAccountReference() != null ? request.getRequest().getTransaction().getAccountReference() : "");
            return mpesaBrokerService.generateResponse(response);
        } catch (Exception e) {
            MpesaBrokerResponseDTO response = new MpesaBrokerResponseDTO();
            response.setResponseCode(String.valueOf(HttpStatus.BAD_REQUEST));
            response.setTransactionID("");
            response.setResponseDesc("TRANSACTION CAN NOT PROCEED.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request.toString(), ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId(), "POST" , e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.MPESA_TRANSACTION,request.getRequest().getTransaction().getAccountReference() != null ? request.getRequest().getTransaction().getAccountReference() : "");
            return mpesaBrokerService.generateResponse(response);
        }
    }
    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {

        }
        return loggedInUser;
    }
    @PostMapping(value = "/mpesaB2C/initiateB2CPayment")
    public GenericDataDTO mpesaB2CProcessTransaction(@RequestBody CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request) throws JAXBException {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        logger.info("********** Inside mpesaB2CProcessTransaction method **********");
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try{
            mpesaBrokerService.validateRequestForInitiatePayment(customerPaymentDTO);
            genericDataDTO=mpesaBrokerService.initiateB2CMpesaPayment(customerPaymentDTO,request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/mpesaB2C/checkTransactionStatus")
    public GenericDataDTO mpesaB2CProcessTransactionStatusChecker(@RequestBody TransactionStatusRequestDTO transactionStatusRequestDTO, HttpServletRequest request) throws JAXBException{
        GenericDataDTO genericDataDTO= new GenericDataDTO();
        logger.info("********** Inside mpesaB2CProcessTransactionStatusChecker method **********");
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try{
            genericDataDTO=mpesaBrokerService.checkTransactionStatusResponse(transactionStatusRequestDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return genericDataDTO;
    }
    @PostMapping(value = URLConstants.MpesaUrlConstants.C2B_INITIATE_PAYMENT)
    public GenericDataDTO expressSimulateInitiatePayment(@RequestBody CustomerPaymentDTO customerPaymentDTO,HttpServletRequest request) {
        GenericDataDTO dataDTO = new GenericDataDTO();
        dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
        try{
            if(Objects.isNull(customerPaymentDTO)){
                throw new Exception("customerPaymentDTO is null");
            }
            dataDTO =  mpesaBrokerService.initiateC2BMpesaExpressSimulate(customerPaymentDTO,request);
        } catch (CustomValidationException e){
            dataDTO.setResponseCode(e.getErrCode());
            dataDTO.setResponseMessage(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            dataDTO.setResponseMessage(e.getMessage());
        }
        return dataDTO;
    }

    @PostMapping(value = URLConstants.MpesaUrlConstants.QR_PAYMENT)
    public MpesaQrResponseDTO dynamicQrInitiatePayment(@RequestBody CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request) {
        logger.info("********** Inside dynamicQrInitiatePayment method **********");
        MpesaQrResponseDTO mpesaQrResponseDTO = new MpesaQrResponseDTO();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",request.getHeaderNames().toString());
        try{
            if(Objects.isNull(customerPaymentDTO)){
                throw new Exception("customerPaymentDTO is null");
            }
            mpesaQrResponseDTO =  mpesaBrokerService.initiateQRCodePayment(customerPaymentDTO,request);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return mpesaQrResponseDTO;
    }
}
