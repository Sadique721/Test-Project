package com.savbill.integrationsystem.SOAPService.UpdateAccountService;

import com.savbill.integrationsystem.SOAPService.AddAccountService.WordToNumberConverter;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wsupdateaccount.UpdateAccountResponse;
import com.savbill.integrationsystem.generated.wsupdateaccount.WsUpdateAccountResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.*;

import static com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator.getSafeNumber;

@Slf4j
@Endpoint
public class WsUpdateAccountEndpoint {

    @Autowired
    CmsClientService cmsClientService;
    @Autowired
    JwtUtil jwtUtil;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsUpdateAccount")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newupdateaccount.WsUpdateAccountResponse handleUpdateAccountResponse(@RequestPayload wsUpdateAccount request, MessageContext messageContext) throws SOAPException, IOException {
        com.savbill.integrationsystem.generated.newupdateaccount.WsUpdateAccountResponse response = null;
        long startTime = System.currentTimeMillis();
        log.info("Starting handleUpdateAccountResponse for, username: {} At:{}", request.getUserName(), new Date(startTime));

        try {
//            updateWordWithNumber(request);
            response = handleUpdateAccount1(request);
//            return generateUpdateAccountSOAP11SuccessAndExceptionResponse(response, messageContext);
        } catch (Exception e) {
            log.error("Exception in handleUpdateAccountResponse: {}", e.getMessage(), e);
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
//            return generateUpdateAccountSOAP11SuccessAndExceptionResponse(response, messageContext);
        } finally {
            log.info("Completed handleUpdateAccountResponse Method in {}ms", System.currentTimeMillis() - startTime);
        }
        return response;
    }

//    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "UpdateAccount")
//    @ResponsePayload
//    public DOMSource handleUpdateAccountResponse(@RequestPayload UpdateAccount request, MessageContext messageContext) throws SOAPException, IOException {
//        UpdateAccountResponse response = null;
//        try {
////            updateWordWithNumber(request);
//            response = handleUpdateAccount(request);
//            return generateUpdateAccountSOAP11SuccessAndExceptionResponse1(response, messageContext);
//        } catch (Exception e) {
//            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
//            return generateUpdateAccountSOAP11SuccessAndExceptionResponse1(response, messageContext);
//        }
//    }

    public WsUpdateAccountResponse handleUpdateAccount(wsUpdateAccount request) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Starting handleUpdateAccount Method for username: {}", request.getUserName());

        WsUpdateAccountResponse response = new WsUpdateAccountResponse();
        WsUpdateAccountResponse.UpdateAccount updateAccount = new WsUpdateAccountResponse.UpdateAccount();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String userName = request.getUserName().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        updateAccount.setRequestId(requestId);
        if (userName == null || userName.isEmpty()) {
            log.warn("Empty Or Null username received for requestId: {}", requestId);
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input Username Id is Empty or Null.";
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(responseCode);
            updateAccount.setResponseMessage(responseMessage);
            response.setUpdateAccount(updateAccount);
            return response;
        }
        List<wsUpdateAccount.Item> itms = request.getItem();
        if (itms != null && !itms.isEmpty()) {
            log.debug("Processing {} items for requestId: {}", itms.size(), requestId);
            for (wsUpdateAccount.Item item : itms) {
                log.debug("Processing item - Key: {}, Value: {}", item.getKey(), item.getValue());
                if ((SoapConstants.PARAM1.equalsIgnoreCase(item.getKey()) && !isValidIPAddress(item.getValue())) || (SoapConstants.PARAM2.equalsIgnoreCase(item.getKey()) && !isValidIPAddress(item.getValue()))) {
                    log.warn("Invalid IP address format detected for key: {}, value: {}", item.getKey(), item.getValue());
                    updateAccount.setRequestId(requestId);
                    updateAccount.setResponeCode(SoapConstants.BAD_REQUEST);
                    updateAccount.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID);
                    response.setUpdateAccount(updateAccount);
                    return response;
                }
//                if (!SoapConstants.CUSTOMERSTATUS.equals(item.getKey()) && !SoapConstants.PARAM1.equals(item.getKey()) && !SoapConstants.PARAM2.equals(item.getKey())) {
//                    updateAccount.setResponeCode(SoapConstants.BAD_REQUEST);
//                    updateAccount.setResponseMessage(SoapConstants.BAD_REQUEST_MESSAGE);
//                    response.setUpdateAccount(updateAccount);
//                    return response;
//                }
                if (SoapConstants.CUSTOMERSTATUS.equalsIgnoreCase(item.getKey())) {
                    log.debug("Processing customer status: {}", item.getValue());
                    if (SoapConstants.ACTIVE.equalsIgnoreCase(item.getValue()) || SoapConstants.INACTIVE.equalsIgnoreCase(item.getValue()) || SoapConstants.SUSPEND.equalsIgnoreCase(item.getValue())) {
                        updateAccount.setResponseMessage(SoapConstants.SUCCESS);
                        updateAccount.setResponeCode(SoapConstants.SUCCESS_CODE);
                    } else if (SoapConstants.INVALID_ACTIVATION.equalsIgnoreCase(item.getValue())) {
                        log.warn("Invalid activation status received: {}", item.getValue());
                        updateAccount.setRequestId(requestId);
                        updateAccount.setResponseMessage(SoapConstants.INVALID_ACTIVATION_WITH_STATUS_H);
                        updateAccount.setResponeCode(SoapConstants.InvalidActivation);
                        response.setUpdateAccount(updateAccount);
                        return response;
                    } else {
                        log.warn("Invalid Or Null Activation status received: {}", item.getValue());
                        updateAccount.setRequestId(requestId);
                        updateAccount.setResponeCode(SoapConstants.InvalidActivation);
                        updateAccount.setResponseMessage(SoapConstants.INVALID_ACTIVATION_WITH_STATUS + " " + item.getValue());
                        response.setUpdateAccount(updateAccount);
                        return response;
                    }
                }
            }
        } else {
            log.warn("No items provided in the request for Update Account: {}", userName);
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(SoapConstants.INTERNAL_ERROR);
            updateAccount.setResponseMessage("No items provided in the request.");
            response.setUpdateAccount(updateAccount);
            return response;
        }
        try {
            log.info("Calling CMS client service for username: {}", userName);
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);
            ResponseEntity<?> responseEntity = cmsClientService.UpdateAccount(request, mvnoId, token);
            log.debug("CMS client service response received : {}", responseEntity.getBody());

            if (responseEntity != null && responseEntity.getBody() instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) responseEntity.getBody();
                if (responseMap.get(SoapConstants.STATUS).equals(SoapConstants.SUCCESS_CODE)) {
                    if (responseMap.containsKey("message") && responseMap.containsValue("invalid location lock")) {
                        log.warn("Invalid location lock detected for Account: {}", request.getUserName());
                        updateAccount.setResponseMessage(SoapConstants.INVALID_LOCATION_LOCK);
                        updateAccount.setResponeCode(SoapConstants.InvalidActivation);
                    } else {
                        log.info("Successfully updated account for Account: {}", request.getUserName());
                        updateAccount.setResponseMessage(SoapConstants.SUCCESS);
                        updateAccount.setResponeCode(SoapConstants.SUCCESS_CODE);
                    }
                } else {
                    log.warn("Invalid response User Name Incorrect from CMS service for Account: {}", request.getUserName());
                    updateAccount.setResponeCode(SoapConstants.INTERNAL_ERROR);
                    updateAccount.setResponseMessage("User Name Incorrect or Service returned an invalid response");
                }
            } else {
                log.warn("Null or invalid response body from CMS service for requestId: {}", requestId);
                updateAccount.setResponeCode(SoapConstants.INTERNAL_ERROR);
                updateAccount.setResponseMessage("User Name Incorrect or Service returned an invalid response");
            }
        } catch (FeignException e) {
            log.debug("FeignException occurred for UpdateAccount: {}", userName, e.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            String message = "An error occurred while processing the request";
            int status = 404;

            try {
                String errorMessage = e.contentUTF8();
                JsonNode jsonNode = objectMapper.readTree(errorMessage);
                message = jsonNode.get("ERROR").asText();
                status = jsonNode.get("status").asInt();
                if (Objects.nonNull(message) && message.equalsIgnoreCase("Package not Available")) {
                    log.warn("Package not available for Account: {}", request.getUserName());
                    updateAccount.setRequestId(requestId);
                    updateAccount.setResponeCode(SoapConstants.NOT_FOUND);
                    updateAccount.setResponseMessage("Service ID is not available in System");
                    response.setUpdateAccount(updateAccount);
                    return response;
                } else if (Objects.nonNull(message) && message.equalsIgnoreCase("Status field is mandatory, Please add Status")) {
                    log.warn("Status field missing for Account: {}", request.getUserName());
                    updateAccount.setRequestId(requestId);
                    updateAccount.setResponeCode(SoapConstants.NOT_FOUND);
                    updateAccount.setResponseMessage("Status field is mandatory, Please add Status");
                    response.setUpdateAccount(updateAccount);
                    return response;
                }
            } catch (JsonProcessingException je) {
                // Handle specific JSON processing exceptions
                je.printStackTrace();
                log.error("Error processing JSON response for requestId: {}", requestId, je.getMessage());
                throw new RuntimeException("Error processing JSON response", je);
            }
            e.printStackTrace();
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(status);
            updateAccount.setResponseMessage(message);
            response.setUpdateAccount(updateAccount);
            return response;
        } catch (Exception e) {
            log.error("Error processing update account request for Account: {}", userName, e);
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(SoapConstants.NOT_FOUND);
            updateAccount.setResponseMessage("Error processing update account request Or User Not Found");
            response.setUpdateAccount(updateAccount);
            return response;
        } finally {
            log.info("Completed handleUpdateAccount in {}ms", System.currentTimeMillis() - startTime);
        }

        response.setUpdateAccount(updateAccount);
        return response;
    }

    public com.savbill.integrationsystem.generated.newupdateaccount.WsUpdateAccountResponse handleUpdateAccount1(wsUpdateAccount request) throws Exception {
        com.savbill.integrationsystem.generated.newupdateaccount.WsUpdateAccountResponse reps = new com.savbill.integrationsystem.generated.newupdateaccount.WsUpdateAccountResponse();
        com.savbill.integrationsystem.generated.newupdateaccount.UpdateAccountResponse accountResponse = new com.savbill.integrationsystem.generated.newupdateaccount.UpdateAccountResponse();
        long startTime = System.currentTimeMillis();
        log.info("Starting handleUpdateAccount1 Method for username: {} AT:{}", request.getUserName(), new Date(startTime));

        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String userName = request.getUserName().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        accountResponse.setRequestId(requestId);
        if (userName == null || userName.isEmpty()) {
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input Username Id is Empty or Null.";
            accountResponse.setRequestId(requestId);
            accountResponse.setResponeCode(responseCode);
            accountResponse.setResponseMessage(responseMessage);
            reps.setUpdateAccount(accountResponse);
            log.warn("Empty Or Null username received for requestId: {}", requestId);
            return reps;
        }
        List<wsUpdateAccount.Item> itms = request.getItem();
        if (itms != null && !itms.isEmpty()) {
            log.debug("Processing {} items for requestId: {}", itms.size(), requestId);
            for (wsUpdateAccount.Item item : itms) {
                log.debug("Processing item - Key: {}, Value: {}", item.getKey(), item.getValue());
                if ((SoapConstants.PARAM1.equalsIgnoreCase(item.getKey()) && !isValidIPAddress(item.getValue())) || (SoapConstants.PARAM2.equalsIgnoreCase(item.getKey()) && !isValidIPAddress(item.getValue()))) {
                    log.warn("Invalid IP address format detected for key: {}, value: {}", item.getKey(), item.getValue());
                    accountResponse.setRequestId(requestId);
                    accountResponse.setResponeCode(SoapConstants.BAD_REQUEST);
                    accountResponse.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID);
                    reps.setUpdateAccount(accountResponse);
                    return reps;
                }
                if ((SoapConstants.PARAM6.equalsIgnoreCase(item.getKey()) && !isValidIPAddress(item.getValue()))) {
                    log.warn("Invalid IP address format detected for key: {}, value: {}", item.getKey(), item.getValue());
                    accountResponse.setRequestId(requestId);
                    accountResponse.setResponeCode(SoapConstants.BAD_REQUEST);
                    accountResponse.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID);
                    reps.setUpdateAccount(accountResponse);
                    return reps;
                }
                if ((SoapConstants.MACVALIDATION.equalsIgnoreCase(item.getKey())) && (!item.getValue().equalsIgnoreCase("Y") && !item.getValue().equalsIgnoreCase("N") &&  !item.getValue().equalsIgnoreCase(""))) {
                    log.warn("Invalid IP address format detected for key: {}, value: {}", item.getKey(), item.getValue());
                    accountResponse.setRequestId(requestId);
                    accountResponse.setResponeCode(SoapConstants.InvalidActivation);
                    accountResponse.setResponseMessage(SoapConstants.INVALID_MAC_IN_INPUT);
                    reps.setUpdateAccount(accountResponse);
                    return reps;
                }
                if ((SoapConstants.PASSWORD.equalsIgnoreCase(item.getKey())) && (item.getValue().equalsIgnoreCase("") || item.getValue().isEmpty())) {
                    log.warn("Invalid IP address format detected for key: {}, value: {}", item.getKey(), item.getValue());
                    accountResponse.setRequestId(requestId);
                    accountResponse.setResponeCode(401);
                    accountResponse.setResponseMessage("Password is Empty or null in Input XML.");
                    reps.setUpdateAccount(accountResponse);
                    return reps;
                }
//                if (!SoapConstants.CUSTOMERSTATUS.equals(item.getKey()) && !SoapConstants.PARAM1.equals(item.getKey()) && !SoapConstants.PARAM2.equals(item.getKey())) {
//                    updateAccount.setResponeCode(SoapConstants.BAD_REQUEST);
//                    updateAccount.setResponseMessage(SoapConstants.BAD_REQUEST_MESSAGE);
//                    response.setUpdateAccount(updateAccount);
//                    return response;
//                }
                if (SoapConstants.CUSTOMERSTATUS.equalsIgnoreCase(item.getKey())) {
                    log.debug("Processing customer status: {}", item.getValue());
                    if (SoapConstants.ACTIVE.equalsIgnoreCase(item.getValue()) || SoapConstants.INACTIVE.equalsIgnoreCase(item.getValue()) || SoapConstants.SUSPEND.equalsIgnoreCase(item.getValue())) {
                        accountResponse.setResponseMessage(SoapConstants.SUCCESS);
                        accountResponse.setResponeCode(SoapConstants.SUCCESS_CODE);
                    } else if (SoapConstants.INVALID_ACTIVATION.equalsIgnoreCase(item.getValue())) {
                        log.warn("Invalid activation status received: {}", item.getValue());
                        accountResponse.setRequestId(requestId);
                        accountResponse.setResponseMessage(SoapConstants.INVALID_ACTIVATION_WITH_STATUS_H);
                        accountResponse.setResponeCode(SoapConstants.InvalidActivation);
                        reps.setUpdateAccount(accountResponse);
                        return reps;
                    } else {
                        log.warn("Invalid Or Null Activation status received: {}", item.getValue());
                        accountResponse.setRequestId(requestId);
                        accountResponse.setResponeCode(SoapConstants.InvalidActivation);
                        accountResponse.setResponseMessage(SoapConstants.INVALID_ACTIVATION_WITH_STATUS + " " + item.getValue());
                        reps.setUpdateAccount(accountResponse);
                        return reps;
                    }
                }
            }
        } else {
            log.warn("No items provided in the request for Update Account: {}", userName);
            accountResponse.setRequestId(requestId);
            accountResponse.setResponeCode(SoapConstants.INTERNAL_ERROR);
            accountResponse.setResponseMessage("No items provided in the request.");
            reps.setUpdateAccount(accountResponse);
            return reps;
        }
        try {
            String param4Value = null;
            for (wsUpdateAccount.Item item : request.getItem()) {
                if(item.getKey().equalsIgnoreCase("PARAM4")){
                    param4Value = item.getValue();
                }
            }

            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);
            log.info("Calling CMS client service for username: {}", userName);
            ResponseEntity<?> responseEntity = cmsClientService.UpdateAccount(request, mvnoId, token);
            log.debug("CMS client service response received IN:{}MS,Response:{}", System.currentTimeMillis() - startTime, responseEntity.getBody());
            if (responseEntity != null && responseEntity.getBody() instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) responseEntity.getBody();
                if (responseMap.get(SoapConstants.STATUS).equals(SoapConstants.SUCCESS_CODE)) {
                    if (responseMap.containsKey("message") && responseMap.containsValue("invalid location lock")) {
                        log.warn("Invalid location lock detected for Account: {}", request.getUserName());
                        accountResponse.setResponseMessage("Input Location Lock is not in Proper Format- 0:92=\"[val1,val2...]\" :"+param4Value);
                        accountResponse.setResponeCode(SoapConstants.InvalidActivation);
                    } else {
                        log.info("Successfully updated account for Account: {}", request.getUserName());
                        accountResponse.setResponseMessage(SoapConstants.SUCCESS);
                        accountResponse.setResponeCode(SoapConstants.SUCCESS_CODE);
                    }
                } else {
                    log.warn("Invalid response User Name Incorrect from CMS service for Account: {}", request.getUserName());
                    accountResponse.setResponeCode(SoapConstants.INTERNAL_ERROR);
                    accountResponse.setResponseMessage("User Name Incorrect or Service returned an invalid response");
                }
            } else {
                log.warn("Null or invalid response body from CMS service for requestId: {}", requestId);
                accountResponse.setResponeCode(SoapConstants.INTERNAL_ERROR);
                accountResponse.setResponseMessage("User Name Incorrect or Service returned an invalid response");
            }
        } catch (FeignException e) {
            log.debug("Error processing update account request for Account: {}", userName, e);
            ObjectMapper objectMapper = new ObjectMapper();
            String message = "An error occurred while processing the request";
            int status = 404;
            try {
                String errorMessage = e.contentUTF8();
                JsonNode jsonNode = objectMapper.readTree(errorMessage);
                message = jsonNode.get("ERROR").asText();
                status = jsonNode.get("status").asInt();
                if (Objects.nonNull(message) && message.equalsIgnoreCase("Package not Available")) {
                    accountResponse.setRequestId(requestId);
                    accountResponse.setResponeCode(SoapConstants.NOT_FOUND);
                    accountResponse.setResponseMessage("Service ID is not available in System");
                    reps.setUpdateAccount(accountResponse);
                    log.warn("Service ID is not available in System for update user:{}", userName);
                    return reps;
                } else if (Objects.nonNull(message) && message.equalsIgnoreCase("Status field is mandatory, Please add Status")) {
                    accountResponse.setRequestId(requestId);
                    accountResponse.setResponeCode(SoapConstants.NOT_FOUND);
                    accountResponse.setResponseMessage("Status field is mandatory, Please add Status");
                    reps.setUpdateAccount(accountResponse);
                    log.warn("Status field is mandatory, Please add Value");
                    return reps;
                }
            } catch (JsonProcessingException je) {
                e.printStackTrace();
                accountResponse.setRequestId(requestId);
                accountResponse.setResponeCode(status);
                accountResponse.setResponseMessage(message);
                reps.setUpdateAccount(accountResponse);
                log.error("JsonProcessingException occurred while processing:{}", je.getMessage());
                return reps;
            }
            e.printStackTrace();
            if(e.getMessage().contains("User not found")){
                accountResponse.setRequestId(requestId);
                accountResponse.setResponeCode(501);
                accountResponse.setResponseMessage("Username is not available in SPR");
                reps.setUpdateAccount(accountResponse);
                return reps;
            }
            accountResponse.setRequestId(requestId);
            accountResponse.setResponeCode(status);
            accountResponse.setResponseMessage(message);
            reps.setUpdateAccount(accountResponse);
            return reps;
        } catch (Exception e) {
            log.error("Error processing update account request", e);
            accountResponse.setRequestId(requestId);
            accountResponse.setResponeCode(SoapConstants.NOT_FOUND);
            accountResponse.setResponseMessage("Error processing update account request Or User Not Found");
            reps.setUpdateAccount(accountResponse);
            return reps;
        } finally {
            log.info("Completed handleUpdateAccount in {}ms", System.currentTimeMillis() - startTime);
        }

        reps.setUpdateAccount(accountResponse);
        return reps;
    }

    public UpdateAccountResponse handleUpdateAccount(UpdateAccount request) throws Exception {
        UpdateAccountResponse response = new UpdateAccountResponse();
        UpdateAccountResponse.UpdateAccount updateAccount = new UpdateAccountResponse.UpdateAccount();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String userName = request.getUserName().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        updateAccount.setRequestId(requestId);
        if (userName == null || userName.isEmpty()) {
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input Username Id is Empty or Null.";
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(responseCode);
            updateAccount.setResponseMessage(responseMessage);
            response.setUpdateAccount(updateAccount);
            return response;
        }
        List<wsUpdateAccount.Item> itms = request.getItem();
        if (itms != null && !itms.isEmpty()) {
            for (wsUpdateAccount.Item item : itms) {
                if ((SoapConstants.PARAM1.equalsIgnoreCase(item.getKey()) && !isValidIPAddress(item.getValue())) || (SoapConstants.PARAM2.equalsIgnoreCase(item.getKey()) && !isValidIPAddress(item.getValue()))) {
                    updateAccount.setRequestId(requestId);
                    updateAccount.setResponeCode(SoapConstants.BAD_REQUEST);
                    updateAccount.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID);
                    response.setUpdateAccount(updateAccount);
                    return response;
                }
//                if (!SoapConstants.CUSTOMERSTATUS.equals(item.getKey()) && !SoapConstants.PARAM1.equals(item.getKey()) && !SoapConstants.PARAM2.equals(item.getKey())) {
//                    updateAccount.setResponeCode(SoapConstants.BAD_REQUEST);
//                    updateAccount.setResponseMessage(SoapConstants.BAD_REQUEST_MESSAGE);
//                    response.setUpdateAccount(updateAccount);
//                    return response;
//                }
                if (SoapConstants.CUSTOMERSTATUS.equalsIgnoreCase(item.getKey())) {
                    if (SoapConstants.ACTIVE.equalsIgnoreCase(item.getValue()) || SoapConstants.INACTIVE.equalsIgnoreCase(item.getValue()) || SoapConstants.SUSPEND.equalsIgnoreCase(item.getValue())) {
                        updateAccount.setResponseMessage(SoapConstants.SUCCESS);
                        updateAccount.setResponeCode(SoapConstants.SUCCESS_CODE);
                    } else if (SoapConstants.INVALID_ACTIVATION.equalsIgnoreCase(item.getValue())) {
                        updateAccount.setRequestId(requestId);
                        updateAccount.setResponseMessage(SoapConstants.INVALID_ACTIVATION_WITH_STATUS_H);
                        updateAccount.setResponeCode(SoapConstants.InvalidActivation);
                        response.setUpdateAccount(updateAccount);
                        return response;
                    } else {
                        updateAccount.setRequestId(requestId);
                        updateAccount.setResponeCode(SoapConstants.InvalidActivation);
                        updateAccount.setResponseMessage(SoapConstants.INVALID_ACTIVATION_WITH_STATUS + " " + item.getValue());
                        response.setUpdateAccount(updateAccount);
                        return response;
                    }
                }
            }
        } else {
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(SoapConstants.INTERNAL_ERROR);
            updateAccount.setResponseMessage("No items provided in the request.");
            response.setUpdateAccount(updateAccount);
            return response;
        }
        try {
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);
            wsUpdateAccount wsUpdateAccount = wsUpdateAccountMapper(request);
            ResponseEntity<?> responseEntity = cmsClientService.UpdateAccount(wsUpdateAccount, mvnoId, token);
            if (responseEntity != null && responseEntity.getBody() instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) responseEntity.getBody();
                if (responseMap.get(SoapConstants.STATUS).equals(SoapConstants.SUCCESS_CODE)) {
                    if (responseMap.containsKey("message") && responseMap.containsValue("invalid location lock")) {
                        updateAccount.setResponseMessage(SoapConstants.INVALID_LOCATION_LOCK);
                        updateAccount.setResponeCode(SoapConstants.InvalidActivation);
                    } else {
                        updateAccount.setResponseMessage(SoapConstants.SUCCESS);
                        updateAccount.setResponeCode(SoapConstants.SUCCESS_CODE);
                    }
                } else {
                    updateAccount.setResponeCode(SoapConstants.INTERNAL_ERROR);
                    updateAccount.setResponseMessage("User Name Incorrect or Service returned an invalid response");
                }
            } else {
                updateAccount.setResponeCode(SoapConstants.INTERNAL_ERROR);
                updateAccount.setResponseMessage("User Name Incorrect or Service returned an invalid response");
            }
        } catch (FeignException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            String message = "An error occurred while processing the request";
            int status = 404;
            try {
                String errorMessage = e.contentUTF8();
                JsonNode jsonNode = objectMapper.readTree(errorMessage);
                message = jsonNode.get("ERROR").asText();
                status = jsonNode.get("status").asInt();
                if (Objects.nonNull(message) && message.equalsIgnoreCase("Package not Available")) {
                    updateAccount.setRequestId(requestId);
                    updateAccount.setResponeCode(SoapConstants.NOT_FOUND);
                    updateAccount.setResponseMessage("Service ID is not available in System");
                    response.setUpdateAccount(updateAccount);
                    return response;
                } else if (Objects.nonNull(message) && message.equalsIgnoreCase("Status field is mandatory, Please add Status")) {
                    updateAccount.setRequestId(requestId);
                    updateAccount.setResponeCode(SoapConstants.NOT_FOUND);
                    updateAccount.setResponseMessage("Status field is mandatory, Please add Status");
                    response.setUpdateAccount(updateAccount);
                    return response;
                }
            } catch (JsonProcessingException je) {
                // Handle specific JSON processing exceptions
                je.printStackTrace();
                throw new RuntimeException("Error processing JSON response", je);
            }
            e.printStackTrace();
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(status);
            updateAccount.setResponseMessage(message);
            response.setUpdateAccount(updateAccount);
            return response;
        } catch (Exception e) {
            log.error("Error processing update account request", e);
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(SoapConstants.NOT_FOUND);
            updateAccount.setResponseMessage("Error processing update account request Or User Not Found");
            response.setUpdateAccount(updateAccount);
            return response;
        }

        response.setUpdateAccount(updateAccount);
        return response;
    }

    private boolean isValidIPAddress(String ip) {
        if (ip.isEmpty()) {
            return true;
        } else {
            String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
            String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
            return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
        }
    }
    /*
    public DOMSource generateUpdateAccountSOAPResponse(WsUpdateAccountResponse response) throws SOAPException, ParserConfigurationException {
        // Create a SOAP Message factory and message
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Add namespace declarations
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("ns2", "http://api.act.com/");

        SOAPBody body = envelope.getBody();

        // Create the response element
        SOAPElement responseElement = body.addChildElement("wsUpdateAccountResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add UpdateAccount element
        SOAPElement updateAccountElement = responseElement.addChildElement("UpdateAccount");

        // Add child elements to UpdateAccount
        if(response.getUpdateAccount().getRequestId()==null || response.getUpdateAccount().getRequestId().equals("?") || response.getUpdateAccount().getRequestId().equals("") || response.getUpdateAccount().getRequestId().equals(" ")){
            updateAccountElement.addChildElement("requestId").addTextNode("?");
        }else {
            updateAccountElement.addChildElement("requestId").addTextNode(getSafeText(response.getUpdateAccount().getRequestId()));
        }
        updateAccountElement.addChildElement("responeCode").addTextNode(getSafeNumber(response.getUpdateAccount().getResponeCode()));
        updateAccountElement.addChildElement("responseMessage").addTextNode(getSafeText(response.getUpdateAccount().getResponseMessage()));

        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // Convert SOAP message to DOMSource
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for the constructed XML
        return new DOMSource(fragment);
    }
     */

    /**
     * Creates a SOAP 1.1 response for updating an account with a success message and status details.
     * The response contains a success code and message indicating the account update was successful.
     *
     * @param response       The response object containing update account details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for account update.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateUpdateAccountSOAP11SuccessAndExceptionResponse(com.savbill.integrationsystem.generated.newupdateaccount.WsUpdateAccountResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        // Set the body
        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");

        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement updateAccountResponseElement = body.addChildElement("wsUpdateAccountResponse", "ns2", "http://api.act.com/");
        updateAccountResponseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement updateAccount = updateAccountResponseElement.addChildElement("UpdateAccount");
        updateAccount.addChildElement("requestId").addTextNode(response.getUpdateAccount().getRequestId());
        updateAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getUpdateAccount().getResponeCode()));
        updateAccount.addChildElement("responseMessage").addTextNode(response.getUpdateAccount().getResponseMessage());

        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    public DOMSource generateUpdateAccountSOAP11SuccessAndExceptionResponse1(UpdateAccountResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        // Set the body
        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");

        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement updateAccountResponseElement = body.addChildElement("wsUpdateAccountResponse", "ns2", "http://api.act.com/");
        updateAccountResponseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement updateAccount = updateAccountResponseElement.addChildElement("UpdateAccount");
        updateAccount.addChildElement("requestId").addTextNode(response.getUpdateAccount().getRequestId());
        updateAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getUpdateAccount().getResponeCode()));
        updateAccount.addChildElement("responseMessage").addTextNode(response.getUpdateAccount().getResponseMessage());

        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    private void updateWordWithNumber(wsUpdateAccount request) {
        Optional<wsUpdateAccount.Item> first = request.getItem()
                .stream()
                .filter(item -> "CONCURRENTLOGINPOLICY".equals(item.getKey())) // Find the item with the target key
                .findFirst();
        if (first.isPresent() && !first.get().getValue().isEmpty()) {
            String value = first.get().getValue();
            int intNumber = WordToNumberConverter.convertWordToNumber(value);
            first.ifPresent(item -> item.setValue(getSafeNumber(intNumber))); // Update the value if the item is found
        }
    }

    private wsUpdateAccount wsUpdateAccountMapper(UpdateAccount request) {
        // Map your request to your wsUpdateAccount class
        wsUpdateAccount account = new wsUpdateAccount();
        account.setRequestId(request.getRequestId());
        account.setPassword(request.getPassword());
        account.setActionItem(request.getActionItem());
        account.setItem(request.getItem());
        account.setUserName(request.getUserName());
        return account;
    }
}