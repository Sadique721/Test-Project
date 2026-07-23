package com.savbill.integrationsystem.AirtelAppToCRM;

import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelValidateTxRequest;
import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.TransactionEnquiryRequest;
import com.savbill.integrationsystem.AirtelAppToCRM.ResponseDTO.AirtelValidateTxResponse;
import com.savbill.integrationsystem.AirtelAppToCRM.ResponseDTO.TransactionEnquiryResponse;
import com.savbill.integrationsystem.AirtelAppToCRM.service.AirtelValidateTxService;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class AirtelAppToCRMController {

    private static final Logger logger = LoggerFactory.getLogger(AirtelAppToCRMController.class);

    @Autowired
    private AirtelValidateTxService validateTxService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    @PostMapping(value = "/validTransaction", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public AirtelValidateTxResponse validTransaction(@RequestBody AirtelValidateTxRequest request, HttpServletRequest req, @RequestHeader("Authorization") String token) throws JAXBException {
        logger.info("********** Inside validTransaction method **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        try {
            validateTxService.validateC2BRequestData(request);
            AirtelValidateTxResponse airtelValidateTxResponse = validateTxService.validateC2BRequest(request, token);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.OK).body(airtelValidateTxResponse) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_VALID_TRANSACTION,request.getReference().toString());
            return airtelValidateTxResponse;
        } catch (AirtelValidateTxValidator ex) {
            AirtelValidateTxResponse response = new AirtelValidateTxResponse();
            response.setStatus(400);
            response.setMessage(ex.getLocalizedMessage());
            response = validateTxService.generateRespons(response);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_VALID_TRANSACTION,request.getReference().toString());
            return response;
        } catch (Exception e) {
            AirtelValidateTxResponse response = new AirtelValidateTxResponse();
            response.setStatus(400);
            response.setMessage("Transaction can not processed.");
            response = validateTxService.generateRespons(response);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Transaction can not processed.", PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_VALID_TRANSACTION,request.getReference().toString());
            return response;
        }

    }

    @PostMapping(value = "/processTransaction", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public AirtelValidateTxResponse processTransaction(@RequestBody AirtelValidateTxRequest request, @RequestHeader("Authorization") String token,HttpServletRequest req) throws JAXBException {
        logger.info("********** Inside processTransaction method **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        try {
            validateTxService.validateProcessTxRequestData(request);
            AirtelValidateTxResponse airtelValidateTxResponse = validateTxService.processB2CRequest(request, token);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.OK).body(airtelValidateTxResponse) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_PROCESS_TRANSACTION,request.getReference().toString());
            return airtelValidateTxResponse;
        } catch (AirtelValidateTxValidator ex) {
            AirtelValidateTxResponse response = new AirtelValidateTxResponse();
            response.setStatus(400);
            response.setTranscationId("");
            response.setMessage(ex.getLocalizedMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_PROCESS_TRANSACTION,request.getReference().toString());
            return validateTxService.generateRespons(response);
        } catch (Exception e) {
            AirtelValidateTxResponse response = new AirtelValidateTxResponse();
            response.setStatus(400);
            response.setTranscationId("");
            response.setMessage("TRANSACTION CAN NOT PROCEED.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" ,"TRANSACTION CAN NOT PROCEED.", PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_PROCESS_TRANSACTION,request.getReference().toString());
            return validateTxService.generateRespons(response);
        }

    }

    @PostMapping(value = "/transactionEnquiry", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public TransactionEnquiryResponse transactionEnquiry(@RequestBody TransactionEnquiryRequest request,HttpServletRequest req) throws JAXBException {
        logger.info("********** Inside transactionEnquiry method **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        try {
            validateTxService.validateTransactionRequestData(request);
            TransactionEnquiryResponse response = validateTxService.transactionrespons(request);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.OK).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_TRANSACTION_ENQUIRY,request.getTXNID().toString());
            return response;
        } catch (AirtelValidateTxValidator ex){
            TransactionEnquiryResponse response = new TransactionEnquiryResponse();
            response.setStatus(400);
            response.setMessage(ex.getLocalizedMessage());
            response.setReference(ex.reference);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_TRANSACTION_ENQUIRY,request.getTXNID().toString());
            return validateTxService.generatetransactionRespons(response);
        } catch (Exception e){
            TransactionEnquiryResponse response = new TransactionEnquiryResponse();
            response.setStatus(400);
            response.setMessage(" failed transaction or bad request");
            response.setReference("Transaction id of the partner");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_TRANSACTION_ENQUIRY,request.getTXNID().toString());
            return validateTxService.generatetransactionRespons(response);
        }
    }


    @PostMapping(value = "/billFetch", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public AirtelValidateTxResponse billFetch(@RequestBody AirtelValidateTxRequest request,HttpServletRequest req) throws JAXBException {
        logger.info("********** Inside billFetch method **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        try {
            validateTxService.validateBillFetchRequestData(request);
            AirtelValidateTxResponse response = validateTxService.processBILLFETCHRequest(request);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.OK).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_BILL_FETCH,request.getReference().toString());
            return response;
        } catch (AirtelValidateTxValidator ex) {
            AirtelValidateTxResponse response = new AirtelValidateTxResponse();
            response.setStatus(404);
            response.setFirstName("");
            response.setLastName("");
            response.setDueDate("");
            response.setAmmount("");
            response.setCurrency("");
            response.setMessage(ex.getLocalizedMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_BILL_FETCH,request.getReference().toString());
            return validateTxService.generateRespons(response);
        } catch (Exception e) {
            AirtelValidateTxResponse response = new AirtelValidateTxResponse();
            response.setStatus(404);
            response.setFirstName("");
            response.setLastName("");
            response.setDueDate("");
            response.setAmmount("");
            response.setCurrency("");
            response.setMessage("Transaction can not processed.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_BILL_FETCH,request.getReference().toString());
            return validateTxService.generateRespons(response);
        }

    }

    @PostMapping(value = "/lookupDetails", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public AirtelValidateTxResponse lookupDetails(@RequestBody AirtelValidateTxRequest request,HttpServletRequest req, @RequestHeader("Authorization") String token) throws JAXBException {
        logger.info("********** Inside lookupDetails method **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        try {
            validateTxService.validateLookUpRequestData(request);
            AirtelValidateTxResponse airtelValidateTxResponse = validateTxService.processLOOKUPRequest(request, token);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.OK).body(airtelValidateTxResponse) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_LOOKUP_DETAILS,request.getReference().toString());
            return airtelValidateTxResponse;
        } catch (AirtelValidateTxValidator ex) {
            AirtelValidateTxResponse response = new AirtelValidateTxResponse();
            response.setStatus(404);
            response.setFirstName("");
            response.setLastName("");
            response.setMessage(ex.getLocalizedMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_LOOKUP_DETAILS,request.getReference().toString());
            return validateTxService.generateRespons(response);
        } catch (Exception e) {
            AirtelValidateTxResponse response = new AirtelValidateTxResponse();
            response.setStatus(404);
            response.setFirstName("");
            response.setLastName("");
            response.setMessage("Transaction can not processed.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(response) , headers , responseTime , requestInitiationTime , null , 1, "POST" , e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.AIRTEL_REVERSE_LOOKUP_DETAILS,request.getReference().toString());
            return validateTxService.generateRespons(response);
        }

    }
}
