package com.savbill.integrationsystem.RestApiService.SessionLoginStatus;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wssessionloginstatus.WsSessionLoginStatus;
import com.savbill.integrationsystem.generated.wssessionloginstatus.WsSessionLoginStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class SessionLoginStatusController {

    @Autowired
    RadiusClientService radiusClientService;

    @PostMapping("/sessionLoginstatus")
    public GenericDataDTO handleSessionLoginStatus(@RequestBody WsSessionLoginStatus request) {
        log.info("Received session login status request with Ip: {}", request.getIpAddress());
        WsSessionLoginStatusResponse responseData = new WsSessionLoginStatusResponse();
        GenericDataDTO response = new GenericDataDTO();
        String responseMessage;
        Integer responseCode;
        boolean result = false;
        String ipAddress = request.getIpAddress().trim();
        responseData = handleWsSessionLoginStatus(request);

        try {
            if (Objects.nonNull(responseData)) {
                responseCode = responseData.getSessionLoginStatus().getResponeCode();
                responseMessage = responseData.getSessionLoginStatus().getResponseMessage();
                responseData.getSessionLoginStatus().setResult(result);
                response.setResponseCode(responseCode);
                response.setResponseMessage(responseMessage);
                response.setData(responseData);
                log.info("Session login status processed successfully for Ip: {}", ipAddress);
            } else {
                responseCode = responseData.getSessionLoginStatus().getResponeCode();
                responseMessage = responseData.getSessionLoginStatus().getResponseMessage();
                responseData.getSessionLoginStatus().setResult(result);
                response.setResponseCode(responseCode);
                response.setResponseMessage(responseMessage);
                response.setData(responseData);
                log.warn("Session login status response data is null for Ip: {}", ipAddress);
            }
        } catch (Exception e) {
            responseCode = 500;
            responseMessage = e.getMessage();
            responseData.getSessionLoginStatus().setResult(result);
            response.setResponseCode(responseCode);
            response.setResponseMessage(responseMessage);
            response.setData(responseData);
            log.error("Exception occurred while processing session login status for Ip: {} ErrorMessage: {}", ipAddress, e.getMessage());
        }
        return response;
    }

    public WsSessionLoginStatusResponse handleWsSessionLoginStatus(WsSessionLoginStatus request) {
        log.debug("Handling session login status for Ip: {}", request.getIpAddress());
        WsSessionLoginStatusResponse response = new WsSessionLoginStatusResponse();
        WsSessionLoginStatusResponse.SessionLoginStatus sessionStatus = new WsSessionLoginStatusResponse.SessionLoginStatus();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        sessionStatus.setRequestId(requestId);

        String ipAddress = request.getIpAddress().trim();

        if (ipAddress == null || ipAddress.isEmpty()) {
            sessionStatus.setRequestId(requestId);
            sessionStatus.setResponeCode(SoapConstants.EMPTY);
            sessionStatus.setResponseMessage("Input Ip Address is Empty or Null.");
            sessionStatus.setResult(false);
            response.setSessionLoginStatus(sessionStatus);
            log.warn("Empty or null IP address provided for requestId: {}", requestId);
            return response;
        }
        if (!isValidIPAddress(ipAddress)) {
            sessionStatus.setRequestId(requestId);
            sessionStatus.setResponeCode(SoapConstants.InvalidActivation);
            sessionStatus.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID);
            sessionStatus.setResult(false);
            response.setSessionLoginStatus(sessionStatus);
            log.warn("Invalid IpAddress: {} Formate provided", ipAddress);
            return response;
        } else {
            try {
                log.info("RadiusClient Call to check Session Status For: {}", ipAddress);
                GenericDataDTO radiusResponse = radiusClientService.SessionLoginStatus(ipAddress, SoapConstants.MVNOID);
                if (radiusResponse != null && radiusResponse.getData() instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> radiusData = (Map<String, Object>) radiusResponse.getData();
                    String framedIpAddress = (String) radiusData.get("framedIpAddress");
                    String username = (String) radiusData.get("userName");
                    log.info("Session Found In LiveUser Now Check User Known Or UnKnown: {}", ipAddress);
                    GenericDataDTO checkKnoewUser = radiusClientService.checkUnKnownUser(ipAddress, SoapConstants.MVNOID);

                    if (framedIpAddress != null && framedIpAddress.equals(ipAddress)) {
                        if (username.equals(username) && !username.isEmpty() && checkKnoewUser.getResponseMessage().equalsIgnoreCase("SUCCESS")) {
                            sessionStatus.setResponeCode(SoapConstants.SUCCESS_CODE);
                            sessionStatus.setResponseMessage(checkKnoewUser.getData().toString());
                            sessionStatus.setResult(true);
                            log.info("Session login status successful for ip: {}", ipAddress);
                        } else {
                            sessionStatus.setResponeCode(SoapConstants.UNKNOWN_PARAM);
                            sessionStatus.setResponseMessage(checkKnoewUser.getData().toString());
                            sessionStatus.setResult(false);
                            log.warn("Unknown user or invalid username for ip: {}", ipAddress);
                        }
                    } else {
                        sessionStatus.setResponeCode(SoapConstants.NOT_AVAILABLE);
                        sessionStatus.setResponseMessage("IP is not available in session table");
                        sessionStatus.setResult(false);
                        log.warn("IP address not found in session table for ip: {}", ipAddress);
                    }
                } else {
                    sessionStatus.setResponeCode(SoapConstants.NOT_AVAILABLE);
                    sessionStatus.setResponseMessage("IP is not available in session table");
                    sessionStatus.setResult(false);
                    log.warn("No data found in radius response for ipAddress: {}", ipAddress);
                }
            } catch (Exception e) {
                sessionStatus.setResponeCode(SoapConstants.INTERNAL_ERROR);
                sessionStatus.setResponseMessage("Internal Server Error: " + e.getMessage());
                sessionStatus.setResult(false);
                log.error("Exception occurred while processing radius response for requestId: {}", requestId, e.getMessage());
                e.printStackTrace();
            }
        }

        response.setSessionLoginStatus(sessionStatus);
        return response;
    }

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }
}