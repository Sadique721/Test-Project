package com.savbill.integrationsystem.RestApiService.AddAccount;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.SOAPService.AddAccountService.wsAddAccount;
import com.savbill.integrationsystem.generated.getaccountdetails.WsGetAccountDetailsResponse;
import com.savbill.integrationsystem.generated.wsaddaccount.WsAddAccountResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class WsAddAccountServiceRestController {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    CmsClientService cmsClientService;

    @Value("${servicearea.name}")
    Long serviceArea;

    @Value("${defaultplan}")
    String plan;

    @PostMapping("/addAccount")
    public GenericResponse<Object> addAccount(@RequestBody wsAddAccount request) {
        long startTime = System.currentTimeMillis(); // Capture start time
        log.info("Method addAccount started at: {}", new Date(startTime)); // Log start time

        WsAddAccountResponse response = new WsAddAccountResponse();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        String userName = request.getUserName().trim();
        String password = request.getPassword().trim();
        String planName = request.getServiceId().trim();
        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;
        Object customerData = null;
        response.setRequestId(requestId);
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        WsGetAccountDetailsResponse getAccountDetailsResponse = new WsGetAccountDetailsResponse();
        Integer responsecode = HttpStatus.EXPECTATION_FAILED.value();

        if (userName == null || userName.isEmpty()) {
            responsecode = SoapConstants.EMPTY;
            responseMessage = SoapConstants.INPUT_USERNAME_NOT_AVAILABLE;
            response.setResponseMessage(responseMessage);
            response.setResponeCode(responseCode);
            response.setRequestId(requestId);
            genericResponse.setData(response);
            log.warn("Username is null or empty for requestId: {} responseCode: {} ", requestId, response.getResponeCode());
            log.info("Method addAccount completed in: {} ms", System.currentTimeMillis() - startTime); // Log execution time
            return genericResponse;
        }
        if (password == null || password.isEmpty()) {
            responsecode = SoapConstants.EMPTY;
            responseMessage = SoapConstants.INPUT_PASSWORD_NOT_AVAILABLE;
            response.setResponseMessage(responseMessage);
            response.setResponeCode(responseCode);
            response.setRequestId(requestId);
            genericResponse.setData(response);
            log.warn("Password is null or empty for requestId: {} responseCode: {} ", requestId, response.getResponeCode());
            log.info("Method addAccount completed in: {} ms", System.currentTimeMillis() - startTime); // Log execution time
            return genericResponse;
        }
        try {
            List<wsAddAccount.Item> items = request.getItem();
            String[] keysToCheck = {"CUSTOMERSTATUS", "PARAM1", "PARAM2"};

            if (items != null && !items.isEmpty()) {
                boolean isAnyKeyPresent = items.stream()
                        .map(wsAddAccount.Item::getKey) // Extract keys
                        .anyMatch(key -> key != null &&
                                (key.equals("CUSTOMERSTATUS") ||
                                        key.equals("PARAM1") ||
                                        key.equals("PARAM2")));
                boolean customerStatusExists = false;
                String resMessage = "";
                if (isAnyKeyPresent) {
                    for (wsAddAccount.Item item : items) {
                        if (SoapConstants.CUSTOMERSTATUS.equals(item.getKey())) {
                            customerStatusExists = true;
                            String reqStatus = item.getValue();
                            if (!reqStatus.equalsIgnoreCase("n") && !reqStatus.equalsIgnoreCase("y") && !reqStatus.equalsIgnoreCase("suspend")) {
                                responseMessage = SoapConstants.INVALID_ACTIVATION_WITH_STATUS + " " + item.getValue();
                                responseCode = SoapConstants.InvalidActivation;
                                log.warn("Invalid activation status: {} for username: {} responseCode: {} responseMessage: {}", item.getValue(), userName, responseCode, responseMessage);
                                break;
                            }
                        }
                    }
                } else {
                    responseMessage = SoapConstants.INVALID_ACTIVATION_WITH_STATUS_NULL;
                    responseCode = SoapConstants.InvalidActivation;
                    log.warn("Invalid activation status: status is null for userName: {}", userName);
                }
            }

            if (responseCode == SoapConstants.INTERNAL_ERROR) {
                Long mvnoId = SoapConstants.MVNOID;
                String token = jwtUtil.generateJwtToken(mvnoId);
                log.debug("Cms Client Calling For Add Account: {} with serviceId: {}", userName, planName);
                ResponseEntity<?> responseEntity = cmsClientService.AddAccount(request, serviceArea, mvnoId, token, plan);
                Object response1 = responseEntity.getBody();
                log.debug("Cms Client Retrive Data: {} For user: {}", response1);

                if (response1 instanceof LinkedHashMap) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> responseMap = (Map<String, Object>) response1;
                    if (responseMap.containsKey("message") && responseMap.containsValue("Service not available")) {
                        responseMessage = SoapConstants.SERVICE_ID_NOT_AVAILABLE;
                        responseCode = SoapConstants.NOT_FOUND;
                        log.warn("Service not available In System for ServiceId: {} responseCode: {} responseMessage: {}", planName, responseCode, responseMessage);
                    } else if (responseMap.containsKey("message") && responseMap.containsValue("username is already exist")) {
                        responseMessage = SoapConstants.USERNAME_IS_AVAILABLE;
                        responseCode = SoapConstants.NOT_FOUND;
                        log.warn("Username already exists for UserName: {} responseCode: {} responseMessage: {}", userName, responseCode, responseMessage);
                    } else if (responseMap.containsKey("message") && responseMap.containsValue("invalid location lock")) {
                        responseMessage = SoapConstants.INVALID_LOCATION_LOCK;
                        responseCode = SoapConstants.InvalidActivation;
                        log.warn("Invalid location lock for userName: {} responseCode: {} responseMessage: {}", userName, responseCode, responseMessage);
                    } else {
                        customerData = responseMap.get("customer");

                        if (customerData != null) {
                            responseMessage = SoapConstants.SUCCESS;
                            responseCode = SoapConstants.SUCCESS_CODE;
                            log.info("Account added successfully for UserName: {} responseCode: {} responseMessage: {} ", userName, responseCode, responseMessage);
                        } else {
                            responseMessage = responseMessage;
                            responseCode = responseCode;
                            log.warn("Customer data is null for UserName: {} responseCode: {} responseMessage: {}", userName, responseCode, responseMessage);
                        }
                    }
                }
            }
        } catch (RetryableException e) {
            log.error("RetryableException occurred for user: {} ResponseMessge: {}", userName, e.getMessage(), responseMessage);
            return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "RetryableException Due to Service Unavailable or Down", requestId, response, genericResponse);
        } catch (FeignException e) {
            log.error("Feign Client Exception occurred for user: {}", userName);
            return handleFeignException(e, userName, planName, requestId, response, genericResponse);
        } catch (Exception e) {
            log.error("Exception occurred for user: {}", userName, e.getMessage());
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing the request", requestId, response, genericResponse);
        }
        response.setResponseMessage(responseMessage);
        response.setResponeCode(responseCode);
        genericResponse.setData(response);
        log.info("Response sent for requestId: {} responseCode: {} responseMessage: {}", requestId, responseCode, responseMessage);
        log.info("Method addAccount completed in: {} ms", System.currentTimeMillis() - startTime); // Log execution time
        return genericResponse;
    }

    private GenericResponse<Object> createErrorResponse(int responseCode, String responseMessage, String requestId, WsAddAccountResponse response, GenericResponse<Object> genericResponse) {
        response.setResponseMessage(responseMessage);
        response.setResponeCode(responseCode);
        response.setRequestId(requestId);
        genericResponse.setData(response);
        return genericResponse;
    }

    private GenericResponse<Object> handleFeignException(FeignException e, String userName, String planName, String requestId, WsAddAccountResponse response, GenericResponse<Object> genericResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = "An error occurred while processing the request";
        int status = 404;
        try {
            String errorMessage = e.contentUTF8();
            JsonNode jsonNode = objectMapper.readTree(errorMessage);
            message = jsonNode.get("ERROR").asText();
            status = jsonNode.get("status").asInt();
            if (Objects.nonNull(message) && message.equalsIgnoreCase("Package not Available")) {
                return createErrorResponse(404, "Service ID is not available in System", requestId, response, genericResponse);
            } else if (Objects.nonNull(message) && message.equalsIgnoreCase("Status field is mandatory, Please add Status")) {
                return createErrorResponse(404, "Status field is mandatory, Please add Status", requestId, response, genericResponse);
            }
        } catch (JsonProcessingException je) {
            log.error("Error processing JSON response for username: {}", userName, je.getMessage());
            throw new RuntimeException("Error processing JSON response", je);
        }
        return createErrorResponse(status, message, requestId, response, genericResponse);
    }

}