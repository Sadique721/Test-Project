package com.savbill.integrationsystem.RestApiService.WsGetUserSessions;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.GetUserUsageSummary.GetUserSessionresponseDto;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wsgetsessionsbyip.WsGetUserSession;
import com.savbill.integrationsystem.generated.wsgetsessionsbyip.WsGetUserSessionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class GetUserSessionController {

    @Autowired
    public RadiusClientService radiusClientService;

    @PostMapping("/getUserSessionByIp")
    public GenericResponse getWsUserSessionResponse(@RequestBody WsGetUserSession request) {
        GenericResponse genericResponse = new GenericResponse();
        Map<String, Object> response = new HashMap<>();
        WsGetUserSessionResponse resp;
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        log.info("Received request to get user session with IP: {} and requestId: {}", request.getIpAddress(), requestId);
        try {
            if (request != null) {
                genericResponse = getUserSession(request.getIpAddress().trim(), requestId, genericResponse);
                log.info("Successfully retrieved session for IP: {}", request.getIpAddress());
            } else {
                log.warn("Logoff User Sessions Request is null");
                response.put(SoapConstants.REQUESTID, requestId);
                response.put(SoapConstants.RESPONSECODE, SoapConstants.NO_SESSION_FOUND_CODE);
                response.put(SoapConstants.RESPONSEMESSAGE, SoapConstants.NO_RECORD_FOUND_IN_SESSION_TABLE_FOR_GIVEN_IP);
                genericResponse.setData(response);
                log.warn("No session found for IP: {}", request.getIpAddress());
                return genericResponse;
            }
        } catch (Exception e) {
            log.error("Error while fetching session for IP: {} - {}", request.getIpAddress(), e.getMessage());
            response.put(SoapConstants.REQUESTID, requestId);
            response.put(SoapConstants.RESPONSECODE, SoapConstants.INTERNAL_ERROR);
            response.put(SoapConstants.RESPONSEMESSAGE, "In Request All fuild Are null: " + e.getMessage());
            genericResponse.setData(response);
        }
        return genericResponse;
    }

    public GenericResponse getUserSession(String getIpAddress, String requestId, GenericResponse genericResponse) {
        WsGetUserSessionResponse resp = new WsGetUserSessionResponse();
        Map<String, Object> response = new HashMap<>();
        WsGetUserSessionResponse.GetUserSession getUserSession = new WsGetUserSessionResponse.GetUserSession();
        String responseMessage;
        int responseCode;
        try {
            log.info("Processing session request for IP: {} with requestId: {}", getIpAddress, requestId);
            if (getIpAddress == null || getIpAddress.trim().isEmpty()) {
                response.put(SoapConstants.RESPONSECODE, SoapConstants.EMPTY);
                response.put(SoapConstants.RESPONSEMESSAGE, "Input UserName is Empty or Null.");
                response.put(SoapConstants.REQUESTID, requestId);
                genericResponse.setData(response);
                log.warn("IpAddress is Empty or Null");
                return genericResponse;
            }
            if (!isValidIPAddress(getIpAddress)) {
                response.put(SoapConstants.REQUESTID, requestId);
                response.put(SoapConstants.RESPONSECODE, SoapConstants.InvalidActivation);
                response.put(SoapConstants.RESPONSEMESSAGE, SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID);
                log.warn("Invalid IP address provided for requestId: {}", requestId);
                genericResponse.setData(response);
                return genericResponse;
            } else {
                Long mvnoId = SoapConstants.MVNOID;
                log.info("Radius Client Call To check User Session");
                GenericDataDTO genericDataDTO = radiusClientService.GetUserSessionApi(getIpAddress, mvnoId);
                GetUserSessionresponseDto dataMessage = new ObjectMapper().readValue(
                        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()),
                        GetUserSessionresponseDto.class);

                if (getUserSession == null) {
                    resp.setGetUserSession(new WsGetUserSessionResponse.GetUserSession());
                }
                if (dataMessage != null) {
                    getUserSession.setAcctSessionId(dataMessage.getAcctSessionId());
                    if (dataMessage.isKnownUser()) {
                        getUserSession.setCallingStationId(dataMessage.getUserName());
                        getUserSession.setSubscriberAccount(dataMessage.getUserName());
                    } else {
                        getUserSession.setCallingStationId("UNKNOWN");
                        getUserSession.setSubscriberAccount("UNKNOWN");
                        log.info("Ip Is Unknown :{}", getIpAddress);
                    }
                    getUserSession.setCircuitType(null);
                    getUserSession.setContext(null);
                    getUserSession.setDelegatedIpv6Prefixes(dataMessage.getDelegatedIPv6Prefix());
                    getUserSession.setFramedIpv6Prefixes(dataMessage.getFramedIPv6Prefix());
                    getUserSession.setMacAddress(dataMessage.getCallingStationId());
                    getUserSession.setMedium(null);
                    getUserSession.setNASPortId(dataMessage.getNasPortId());
                    getUserSession.setNASPortType(dataMessage.getNasPortType());
                    getUserSession.setNasId(dataMessage.getNasPortId());
                    getUserSession.setNasType(dataMessage.getNasPortType());
                    getUserSession.setRequestId(requestId);
                    getUserSession.setResponeCode(SoapConstants.SUCCESS_CODE);
                    getUserSession.setResponseMessage(SoapConstants.SUCCESS);
                    getUserSession.setSessionId(dataMessage.getAcctSessionId());
                    getUserSession.setSessionIp(dataMessage.getFramedIpAddress());
                    getUserSession.setStartTime(dataMessage.getCreatedDateString());
                    log.info("User session successfully retrieved for IP: {}", getIpAddress);
                } else {
                    getUserSession.setRequestId(requestId);
                    getUserSession.setResponeCode(SoapConstants.NOT_AVAILABLE);
                    getUserSession.setResponseMessage(SoapConstants.NO_RECORD_FOUND_IN_SESSION_TABLE_FOR_GIVEN_IP);
                    log.warn("No records found in session table for IP: {}", getIpAddress);
                }
            }
        } catch (RetryableException e) {
            getUserSession.setRequestId(requestId);
            getUserSession.setResponeCode(SoapConstants.NOT_AVAILABLE);
            getUserSession.setResponseMessage(SoapConstants.FEING_CLIENT_EXCEPTION);
            log.error("Facing RetryableException Due to radius unavailable to send data: {}", getIpAddress);

        } catch (Exception e) {
            log.error("Exception occurred while processing session request for IP: {} - {}", getIpAddress, e.getMessage(), e);
        }
        genericResponse.setData(getUserSession);
        return genericResponse;
    }

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }
}