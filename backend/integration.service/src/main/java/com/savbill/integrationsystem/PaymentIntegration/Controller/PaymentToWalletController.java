package com.savbill.integrationsystem.PaymentIntegration.Controller;

import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentIntegration.DTO.*;
import com.savbill.integrationsystem.PaymentIntegration.DTO.*;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentToWalletService;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class PaymentToWalletController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentToWalletController.class);

    @Autowired
    PaymentToWalletService paymentToWalletService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    @Autowired
    private CustomerPaymentService customerPaymentService;

    @PostMapping("/addToWalletByOrderId")
    public GenericDataDTO addToWalletByOrderId(@RequestParam Long orderId, @RequestParam String transactionId, HttpServletRequest request) {
        logger.info("********** Inside addToWalletByOrderId method **********");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = paymentToWalletService.addToWalletByOrderId(orderId, transactionId);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setData(e.getMessage());
            ApplicationLogger.logger.error("addToWalletByOrderId initiation failed " + e.getMessage());
        }
        return genericDataDTO;
    }

    @GetMapping("/gateway/getGatewayFromPrefix")
    public GenericDataDTO getGatewayFromPrefix(@RequestParam String mobileNumber, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String gateway = paymentToWalletService.getGatewayfromMobileNumber(mobileNumber);
            genericDataDTO.setData(gateway);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Gateway Fetch SuccessFully");
        }catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(ex.getErrCode());
            genericDataDTO.setResponseMessage(ex.getMessage());
            ApplicationLogger.logger.error("getGatewayFromPrefix initiation failed " + ex.getMessage());
        }
        catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            ApplicationLogger.logger.error("getGatewayFromPrefix initiation failed " + e.getMessage());
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/addToWalletThirdParty")
    public AddToWalletDTOResponse addToWalletThirdParty(@RequestBody AddToWalletDTO request, @RequestHeader("Authorization") String token, HttpServletRequest req) throws JAXBException {
        logger.info("********** Inside  method  add wallet for third party **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        AddToWalletDTOResponse addToWalletDTOResponse = new AddToWalletDTOResponse();
        try {
            customerPaymentService.validateAddToWalletRequest(request);
            customerPaymentService.processAddToWallet(request,token);
            addToWalletDTOResponse.setStatus(HttpStatus.OK.value());
            addToWalletDTOResponse.setMessage("Amount add to wallet Successfully.");
            addToWalletDTOResponse.setAccountNo(request.getAccountNo());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.OK).body(addToWalletDTOResponse) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_ADD_TO_WALLET, addToWalletDTOResponse.getAccountNo());
            return addToWalletDTOResponse;
        } catch (CustomValidationException ex) {
            addToWalletDTOResponse.setStatus(ex.getErrCode());
            addToWalletDTOResponse.setMessage(ex.getMessage());
            if(request.getAccountNo() != null) {
                addToWalletDTOResponse.setAccountNo(request.getAccountNo());
            }
            else{

                addToWalletDTOResponse.setAccountNo("NIL_ACCOUNT");
            }
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            if(request.getAccountNo() != null && !request.getAccountNo().isEmpty() && !request.getAccountNo().equals(" ")) {
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(addToWalletDTOResponse), headers, responseTime, requestInitiationTime, null, 1, "POST", ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_ADD_TO_WALLET, request.getAccountNo());
            }
            else{
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(addToWalletDTOResponse), headers, responseTime, requestInitiationTime, null, 1, "POST", ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_ADD_TO_WALLET, "ACCOUNT_NIL");
            }
            return addToWalletDTOResponse;
        } catch (Exception e) {
            addToWalletDTOResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            addToWalletDTOResponse.setMessage(e.getMessage());
            addToWalletDTOResponse.setAccountNo(request.getAccountNo());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(addToWalletDTOResponse) , headers , responseTime , requestInitiationTime , null , 1, "POST" ,e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_ADD_TO_WALLET,request.getAccountNo());
            return addToWalletDTOResponse;
        }

    }

    @PostMapping(value = "/buyPlanThirdParty")
    public ThirdPartyPaymentDTOResponse buyPlanThirdParty(@RequestBody ThirdPartyPaymentDTO request, @RequestHeader("Authorization") String token, HttpServletRequest req) throws JAXBException {
        logger.info("********** Inside  method buy Plan for third party **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        ThirdPartyPaymentDTOResponse thirdPartyPaymentDTOResponse = new ThirdPartyPaymentDTOResponse();
        try {
            customerPaymentService.validateAddPayment(request);
            customerPaymentService.processAddPayment(request,token);
            thirdPartyPaymentDTOResponse.setStatus(HttpStatus.OK.value());
            thirdPartyPaymentDTOResponse.setMessage("Plan bought Successfully.");
            thirdPartyPaymentDTOResponse.setAccountNo(request.getAccountNo());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.OK).body(thirdPartyPaymentDTOResponse) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_BUY_PLAN, thirdPartyPaymentDTOResponse.getAccountNo());
            return thirdPartyPaymentDTOResponse;
        } catch (CustomValidationException ex) {
            thirdPartyPaymentDTOResponse.setStatus(ex.getErrCode());
            thirdPartyPaymentDTOResponse.setMessage(ex.getMessage());
            if(request.getAccountNo() != null) {
                thirdPartyPaymentDTOResponse.setAccountNo(request.getAccountNo());
            }
            else{

                thirdPartyPaymentDTOResponse.setAccountNo("NIL_ACCOUNT");
            }
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            if(request.getAccountNo() != null && !request.getAccountNo().isEmpty() && !request.getAccountNo().equals(" ")) {
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(thirdPartyPaymentDTOResponse), headers, responseTime, requestInitiationTime, null, 1, "POST", ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_BUY_PLAN, request.getAccountNo());
            }
            else{
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(thirdPartyPaymentDTOResponse), headers, responseTime, requestInitiationTime, null, 1, "POST", ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_BUY_PLAN, "ACCOUNT_NIL");
            }
            return thirdPartyPaymentDTOResponse;
        } catch (Exception e) {
            thirdPartyPaymentDTOResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            thirdPartyPaymentDTOResponse.setMessage(e.getMessage());
            thirdPartyPaymentDTOResponse.setAccountNo(request.getAccountNo());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(thirdPartyPaymentDTOResponse) , headers , responseTime , requestInitiationTime , null , 1, "POST" ,e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_BUY_PLAN,request.getAccountNo());
            return thirdPartyPaymentDTOResponse;
        }

    }

    @PostMapping(value = "/getPlanListThirdParty")
    public ThirdPartyPlanFetchDTOResponse getPlanListThirdParty(@RequestBody ThirdPartyPlanFetchDTO request, @RequestHeader("Authorization") String token, HttpServletRequest req) throws JAXBException {
        logger.info("********** Inside  method fetch Plan for third party **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        ThirdPartyPlanFetchDTOResponse thirdPartyPlanFetchDTOResponse = new ThirdPartyPlanFetchDTOResponse();
        try {
            customerPaymentService.validateGetPlanListByparameter(request);
            List<LightPostpaidPlanDTO> postpaidPlanDTOList =  customerPaymentService.getPlanListByparameter(request,token);
            thirdPartyPlanFetchDTOResponse.setStatus(HttpStatus.OK.value());
            thirdPartyPlanFetchDTOResponse.setPlanList(postpaidPlanDTOList);
            thirdPartyPlanFetchDTOResponse.setMessage("Plan find successfully.");
            if(postpaidPlanDTOList.isEmpty()){
                thirdPartyPlanFetchDTOResponse.setStatus(HttpStatus.NO_CONTENT.value());
                thirdPartyPlanFetchDTOResponse.setMessage("No plan found for given parameters.");
            }
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.OK).body(thirdPartyPlanFetchDTOResponse) , headers , responseTime , requestInitiationTime , null , 1, "POST" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_BUY_PLAN, "");
            return thirdPartyPlanFetchDTOResponse;
        } catch (CustomValidationException ex) {
            thirdPartyPlanFetchDTOResponse.setStatus(ex.getErrCode());
            thirdPartyPlanFetchDTOResponse.setMessage(ex.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(thirdPartyPlanFetchDTOResponse), headers, responseTime, requestInitiationTime, null, 1, "POST", ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_FETCH_PLAN, "");
            return thirdPartyPlanFetchDTOResponse;
        } catch (Exception e) {
            thirdPartyPlanFetchDTOResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            thirdPartyPlanFetchDTOResponse.setMessage(e.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(thirdPartyPlanFetchDTOResponse) , headers , responseTime , requestInitiationTime , null , 1, "POST" ,e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_FETCH_PLAN,"");
            return thirdPartyPlanFetchDTOResponse;
        }

    }

    @GetMapping(value = "/getCustomerPendingAmount")
    public PendingAmountResponse getCustomerPendingAmount(@RequestParam("accountNo") String accountNumber , @RequestHeader("Authorization") String token, HttpServletRequest req) throws JAXBException {
        logger.info("********** Inside  method fetch pending amount for third party **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        PendingAmountResponse pendingAmountResponse = new PendingAmountResponse();
        Double amount = 0.0;
        try {
            amount = customerPaymentService.getCustomerPendingAmount(accountNumber,token);
            pendingAmountResponse.setStatus(HttpStatus.OK.value());
            pendingAmountResponse.setPendingAmount(amount);
            pendingAmountResponse.setMessage("Pending amount fetch successfully.");
            pendingAmountResponse.setAccountNo(accountNumber);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),null, ResponseEntity.status(HttpStatus.OK).body(pendingAmountResponse) , headers , responseTime , requestInitiationTime , null , 1, "GET" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_CHECK_PENDING_AMOUNT, accountNumber);
            return pendingAmountResponse;
        } catch (CustomValidationException ex) {
            pendingAmountResponse.setStatus(ex.getErrCode());
            pendingAmountResponse.setMessage(ex.getMessage());
            pendingAmountResponse.setAccountNo(accountNumber);
            pendingAmountResponse.setPendingAmount(amount);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), null, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pendingAmountResponse), headers, responseTime, requestInitiationTime, null, 1, "GET", ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_CHECK_PENDING_AMOUNT, accountNumber);
            return pendingAmountResponse;
        } catch (Exception e) {
            pendingAmountResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            pendingAmountResponse.setMessage(e.getMessage());
            pendingAmountResponse.setPendingAmount(amount);
            pendingAmountResponse.setAccountNo(accountNumber);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),null, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pendingAmountResponse) , headers , responseTime , requestInitiationTime , null , 1, "GET" ,e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_CHECK_PENDING_AMOUNT,accountNumber);
            return pendingAmountResponse;
        }

    }


    @GetMapping(value = "/checkTransactionByTransactionId")
    public CheckTransactionResponse checkTransactionByTransactionId(@RequestParam("transactionId") String transactionId , @RequestHeader("Authorization") String token, HttpServletRequest req) throws JAXBException {
        logger.info("********** Inside  method fetch pending amount for third party **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers",req.getHeaderNames().toString());
        CheckTransactionResponse checkTransactionResponse = new CheckTransactionResponse();
        try {
            CustomerPayment customerPayment = customerPaymentService.getCustomerPaymentBytransactionId(transactionId);
            checkTransactionResponse.setStatus(HttpStatus.OK.value());
            if(customerPayment.getStatus().equalsIgnoreCase(AirtelValidateConstant.SUCCESSFUL)){
                checkTransactionResponse.setTransactionStatus("Successfully");
            }
            else{
                checkTransactionResponse.setStatus(202);
                checkTransactionResponse.setTransactionStatus(customerPayment.getStatus());
            }
            checkTransactionResponse.setTransactionId(transactionId);
            checkTransactionResponse.setMessage("Transaction fetch successfully.");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),null, ResponseEntity.status(HttpStatus.OK).body(checkTransactionResponse) , headers , responseTime , requestInitiationTime , null , 1, "GET" , "Success", PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_CHECK_TRANSACTION, transactionId);
            return checkTransactionResponse;
        } catch (CustomValidationException ex) {
            checkTransactionResponse.setStatus(ex.getErrCode());
            checkTransactionResponse.setMessage(ex.getMessage());
            checkTransactionResponse.setTransactionId(transactionId);
            checkTransactionResponse.setTransactionStatus("NONE");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(), null, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(checkTransactionResponse), headers, responseTime, requestInitiationTime, null, 1, "GET", ex.getLocalizedMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_CHECK_TRANSACTION, transactionId);
            return checkTransactionResponse;
        } catch (Exception e) {
            checkTransactionResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            checkTransactionResponse.setMessage(e.getMessage());
            checkTransactionResponse.setTransactionId(transactionId);
            checkTransactionResponse.setTransactionStatus("NONE");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),null, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(checkTransactionResponse) , headers , responseTime , requestInitiationTime , null , 1, "GET" ,e.getMessage(), PaymentGatewayConfigurationConstant.AUDITCONSTANT.THIRD_PARTY_CHECK_PENDING_AMOUNT,transactionId);
            return checkTransactionResponse;
        }

    }



}
