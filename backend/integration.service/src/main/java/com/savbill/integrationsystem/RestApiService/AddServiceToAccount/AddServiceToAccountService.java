package com.savbill.integrationsystem.RestApiService.AddServiceToAccount;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccount;
import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccountResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class AddServiceToAccountService {
    @Autowired
    CmsClientService cmsClientService;

    @Autowired
    private JwtUtil jwtUtil;

    public WsAddServiceToAccountResponse getWsAddServiceToAccount(WsAddServiceToAccount request) {
        WsAddServiceToAccountResponse response = new WsAddServiceToAccountResponse();
        WsAddServiceToAccountResponse.AddServiceToAccount response1 = new WsAddServiceToAccountResponse.AddServiceToAccount();
        long startTime = System.currentTimeMillis();
        log.info("Starting method: getWsAddServiceToAccount At{}", new Date(startTime));
        String requestId = request.getRequestId();
        response1.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;
        String userName = request.getUserName().trim();
        String serviceId = request.getServiceId().trim();

        try {
            if (userName.isEmpty() || userName == null) {
                responseMessage = SoapConstants.Input_Username_is_Empty_or_null;
                responseCode = SoapConstants.EMPTY;
                log.warn("User name is empty or null: {}", userName);
            } else if (serviceId.isEmpty() || serviceId == null) {
                responseMessage = SoapConstants.Input_ServiceId_is_Empty_or_null;
                responseCode = SoapConstants.EMPTY;
                log.warn("Service ID is empty or null: {}", serviceId);
            } else {
                Long mvnoId = SoapConstants.MVNOID;
                String token = jwtUtil.generateJwtToken(mvnoId);
                log.debug("Call CmsClient To AddService:{} To Account:{}", serviceId, userName);
                ResponseEntity<?> responseEntity = cmsClientService.AddServiceToAccountAccount(request, mvnoId, token);
                Object responseData = responseEntity.getBody();
                log.debug("Integration Received Response In:{}MS,Response:{}", System.currentTimeMillis() - startTime, responseData);

                if (responseData instanceof LinkedHashMap) {
                    Map<String, Object> responseMap = (Map<String, Object>) responseData;
                    if (responseMap.containsKey("message") && responseMap.containsValue("Username Not available")) {
                        responseMessage = "Not Updated Record in SPR table due to Technical Issue Via Product API[updateSubscriber]";
                        responseCode = SoapConstants.NO_RECOED_UPDATE_CODE;
                        log.warn("UserName is Not available in SPR table : {} ", userName);
                    } else if (responseMap.containsKey("message") && responseMap.containsValue("ServiceId Not available")) {
                        responseMessage = SoapConstants.SERVICE_ID_NOT_AVAILABLE;
                        responseCode = SoapConstants.NOT_FOUND;
                        log.warn("ServiceID: {} Not available ", serviceId);
                    } else if (responseMap.get("deActivateResponse") != null) {
                        responseMessage = SoapConstants.CUSTOMER_UPDATED_IN_SPR_TABLE;
                        responseCode = SoapConstants.SUCCESS_CODE;
                        log.info("Customer Update Successfully In SPR Table : {} ", userName);
                    }
                }
            }
        } catch (FeignException e) {
            log.debug("FeignException occurred for request ID: {}", requestId, e);
            ObjectMapper objectMapper = new ObjectMapper();
            String message = "";
            int status = 404;

            try {
                String errorMessage = e.contentUTF8();
                JsonNode jsonNode = objectMapper.readTree(errorMessage);
                if (jsonNode.has("msg")) {
                    message = jsonNode.get("msg").asText();
                }
                if (jsonNode.has("status")) {
                    status = jsonNode.get("status").asInt();
                }
                if (Objects.nonNull(message) && message.equalsIgnoreCase("Please enter a valid service")) {
                    response1.setResponeCode(417);
                    response1.setResponseMessage("Base plan Can't change with Bandwidth booster and volume booster.");
                    response1.setRequestId(requestId);
                    response.setAddServiceToAccount(response1);
                    log.warn("Invalid service Can't change with Bandwidth booster and volume booster : {}", serviceId);
                    return response;
                }
            } catch (Exception ex) {
                log.error("Error processing FeignException for request ID: {}", requestId, ex);
                responseCode = SoapConstants.INTERNAL_ERROR;
                responseMessage = "An error occurred while processing the request";
            }
        } catch (Exception e) {
            log.error("An unexpected error occurred for request ID: {}", requestId, e);
            responseMessage = SoapConstants.NOT_UPDATED_RECORD_IN_SPR_TABLE_DUE_TO_TECHNICAL_ISSUES;
            responseCode = SoapConstants.NO_RECOED_UPDATE_CODE;
        }

        response1.setResponseMessage(responseMessage);
        response1.setResponeCode(responseCode);
        response1.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
        response.setAddServiceToAccount(response1);
        log.info("Processed request with ID: {}, Response Code: {}, Message: {}", requestId, responseCode, responseMessage);

        return response;
    }
}
