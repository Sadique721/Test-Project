package com.savbill.integrationsystem.RestApiService.chargeService;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.service.ChangeServService;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.changeservice.WsChangeService;
import com.savbill.integrationsystem.generated.changeservice.WsChangeServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
public class ChangeServiseServ {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ChangeServService changeServService;

    @Autowired
    private CmsClientService cmsClientService;

    public GenericResponse<Object> getWsChange(ChangeServiceRequest request) {
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        HashMap<String, Object> response = new HashMap<>();
        WsChangeServiceResponse wsChangeServiceResponse = new WsChangeServiceResponse();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        wsChangeServiceResponse.setRequestId(requestId);
        String userName = Optional.ofNullable(request.getUserName()).orElse("").trim();
        String serviceId = Optional.ofNullable(request.getServiceId()).orElse("").trim();

        log.info("Processing request for requestId: {}, userName: {}, serviceId: {}, overrides: {}", requestId, userName, serviceId, request.getOverrides());
        if (isNullOrEmpty(userName)) {
            log.warn("Input user name is empty or null for requestId: {}", requestId);
            return createErrorResponse(SoapConstants.EMPTY, "Input user name is Empty or Null.", requestId);
        }

        if (isNullOrEmpty(serviceId)) {
            log.warn("Input serviceId is empty or null for userName: {}", userName);
            return createErrorResponse(SoapConstants.EMPTY, "Input Overrides and ServiceId both are Empty or null.", requestId);
        }

        try {
            userName = userName.toLowerCase();
            log.debug("Calling...Radius client to Check customer Entry in Customer TBL for userName: {}", userName);
            boolean isCustomerExists = changeServService.checkCustomerEntryInCustTBL(userName);

            if (!isCustomerExists) {
                log.warn("Customer not found in custTBL for userName: {}, ServiceId: {}", userName, serviceId);
                return createErrorResponse(SoapConstants.NO_RECOED_UPDATE_CODE, "No Records Updated Via Product API[updateSubscriber] for given UserName", requestId);
            }

            log.debug("Calling...Radius client to Check customer entry in usage quota for userName: {}", userName);
            boolean isUsageExists = changeServService.checkCustEntryInUsageQuota(userName);
            if (!isUsageExists) {
                log.warn("User details not found in usage table for userName: {}", userName);
                response.put(SoapConstants.REQUESTID, requestId);
                response.put(SoapConstants.RESPONSECODE, SoapConstants.USER_DETAILS_NOT_FAOUND_IN_USAGE_TABLE_CODE);
                response.put(SoapConstants.RESPONSEMESSAGE, "User Details not found in Usages table for Qouta Update for Given Username.");
                genericResponse.setData(response);
                return genericResponse;
            }
            Double override = Math.abs(Optional.ofNullable(request.getOverrides()).orElse(0.0));
            request.setOverrides(override);
            log.debug("Overrides set to: {} for userName: {}, serviceId: {}", override, userName, serviceId);

            ResponseEntity<?> responseEntity = cmsClientService.changeService(request, SoapConstants.MVNOID, jwtUtil.generateJwtToken(SoapConstants.MVNOID));
            log.debug("Change service API called for userName: {}, serviceId: {}", userName, serviceId);

            boolean isChangeServiceValid = changeServService.changeServiceValidator(responseEntity);
            if (isChangeServiceValid) {
                log.info("Change service successful for userName: {}, serviceId: {}", userName, serviceId);
                response.put(SoapConstants.REQUESTID, requestId);
                response.put(SoapConstants.RESPONSECODE, SoapConstants.SUCCESS_CODE);
                response.put(SoapConstants.RESPONSEMESSAGE, "SUCCESS");
                genericResponse.setData(response);
                return genericResponse;
            } else {
                log.warn("No records updated via product API for userName: {}, serviceId: {}", userName, serviceId);
                response.put(SoapConstants.REQUESTID, requestId);
                response.put(SoapConstants.RESPONSECODE, SoapConstants.USER_DETAILS_NOT_FAOUND_IN_USAGE_TABLE_CODE);
                response.put(SoapConstants.RESPONSEMESSAGE, "No records update via product API [updateSubscriber] for given username.");
                genericResponse.setData(response);
                return genericResponse;
            }

        } catch (FeignException e) {
            log.debug("FeignException occurred for userName: {}, serviceId: {}", userName, serviceId, e.getMessage());
            return handleFeignException(e, requestId, userName, serviceId);
        } catch (CustomValidationException e) {
            log.error("CustomValidationException occurred for userName: {}, serviceId: {}", userName, serviceId, e.getMessage());
            return createErrorResponse(e.getErrCode(), e.getMessage(), requestId);
        } catch (Exception e) {
            log.error("Unexpected error occurred for userName: {}, serviceId: {}", userName, serviceId, e.getMessage());
            return createErrorResponse(HttpStatus.EXPECTATION_FAILED.value(), "An unexpected error occurred.", requestId);
        }
    }

    private GenericResponse<Object> handleFeignException(FeignException e, String requestId, String userName, String serviceId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String errorMessage = e.contentUTF8();
            JsonNode jsonNode = objectMapper.readTree(errorMessage);
            String message = jsonNode.get("msg").asText();
            int status = jsonNode.get("status").asInt();

            if (message.equalsIgnoreCase("Please enter a valid service")) {
                log.warn("Invalid service ID for userName: {}, serviceId: {}", userName, serviceId);
                return createErrorResponse(SoapConstants.NOT_FOUND, "Input Service ID is not found in Policy Group Table.", requestId);
            } else {
                log.debug("FeignException with message: {} for userName: {}, requestId: {}", message, userName, requestId);
                WsChangeServiceResponse wsChangeServiceResponse = new WsChangeServiceResponse();
                wsChangeServiceResponse.setRequestId(requestId);
                wsChangeServiceResponse.setResponeCode(status);
                wsChangeServiceResponse.setResponseMessage(message);
                GenericResponse<Object> genericResponse = new GenericResponse<>();
                genericResponse.setData(wsChangeServiceResponse);
                return genericResponse;
            }
        } catch (JsonProcessingException je) {
            log.error("JsonProcessingException occurred for userName: {}, requestId: {}", userName, requestId, je.getMessage());
            return createErrorResponse(HttpStatus.EXPECTATION_FAILED.value(), "Error processing JSON response.", requestId);
        }
    }

    private GenericResponse<Object> createErrorResponse(int responseCode, String responseMessage, String requestId) {
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        HashMap<String, Object> response = new HashMap<>();
        response.put(SoapConstants.REQUESTID, requestId);
        response.put(SoapConstants.RESPONSECODE, responseCode);
        response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
        genericResponse.setData(response);
        return genericResponse;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}