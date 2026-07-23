package com.savbill.integrationsystem.PaymentIntegration.Controller;

import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentIntegration.DTO.TigoAppToCRMDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.TigoPesaResponseDTO;
import com.savbill.integrationsystem.PaymentIntegration.Service.TigoPesaAppToCRMService;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.CommonConstant;
import com.savbill.integrationsystem.core.exceptions.PaymentValidationException;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
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

@RestController()
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class TigoPesaController {

    @Autowired
    private TigoPesaAppToCRMService tigoPesaAppToCRMService;
    @Autowired
    private ApiAuditsService apiAuditsService;

    private static final Logger logger = LoggerFactory.getLogger(TigoPesaController.class);

    @PostMapping(value = "/SYNC_BILLPAY_API", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public TigoPesaResponseDTO processTransaction(@RequestBody TigoAppToCRMDTO request, @RequestHeader("Authorization") String token, HttpServletRequest req) throws JAXBException {
        logger.info("********** Inside processTransaction Method **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        TigoPesaResponseDTO response = new TigoPesaResponseDTO();
        try {
            tigoPesaAppToCRMService.validateProcessTxRequestData(request);
            response = tigoPesaAppToCRMService.processC2BRequest(request, token);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request.toString(), ResponseEntity.status(HttpStatus.OK).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId(), "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.TIGO_PESA,request.getCustomerReference());
            return response;
        } catch (PaymentValidationException ex) {
            Long orderId =  tigoPesaAppToCRMService.fetchOrderId(request);
            response.setType(CommonConstant.TIGOPESA_CONSTANTS.SYNC_BILLPAY_RESPONSE);
            response.setTransId(request.getTransId());
            response.setRefId(orderId);
            response.setResult(CommonConstant.TIGOPESA_CONSTANTS.TRANSACTION_FAILURE);
            response.setErrorCode(CommonConstant.TIGOPESA_STATUS_CODES.INVALID_PAYMENT);
            response.setErrorDesc("Transaction Failure");
            response.setMsisdn(request.getMsisdn());
            response.setFlag("N");
            response.setContent(ex.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            if(request.getCustomerReference()!=null) {
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), request, ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response), headers, responseTime, requestInitiationTime, getLoggedInUser().getUsername(), getLoggedInUser().getMvnoId(), "POST", ex.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.TIGO_PESA, request.getCustomerReference());
            }
        } catch (Exception e) {
            Long orderId =  tigoPesaAppToCRMService.fetchOrderId(request);
            response.setType(CommonConstant.TIGOPESA_CONSTANTS.SYNC_BILLPAY_RESPONSE);
            response.setTransId(request.getTransId());
            response.setRefId(orderId);
            response.setResult(CommonConstant.TIGOPESA_CONSTANTS.TRANSACTION_FAILURE);
            response.setErrorCode(CommonConstant.TIGOPESA_STATUS_CODES.INVALID_PAYMENT);
            response.setErrorDesc("Transaction Failure");
            response.setMsisdn(request.getMsisdn());
            response.setFlag("N");
            response.setContent(e.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            if(request.getCustomerReference()!=null) {
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), request, ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response), headers, responseTime, requestInitiationTime, getLoggedInUser().getUsername(), getLoggedInUser().getMvnoId(), "POST", e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.TIGO_PESA, request.getCustomerReference());
            }
        }
       return tigoPesaAppToCRMService.generateRespone(response);
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
}
