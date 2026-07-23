package com.savbill.integrationsystem.SOAPService.logOnSubSession;


import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClient;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.service.ChangeServService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.logonsubsession.LogonSubSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.savbill.integrationsystem.RestApiService.logOnSubSession.LogOnSubSessionDTO;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.transform.dom.DOMSource;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Endpoint
public class LogOnSubSessionEndpoint {
    @Autowired
    CmsClientService cmsClientService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    ChangeServService changeServService;
    @Autowired
    RadiusClient radiusClient;
    @Autowired
    LogOnSubSessionService logOnSubSessionService;
    @Autowired
    private RadiusClientService radiusClientService;

    private final String LOG_ON_SUB_SESSION_RESPONSE = "logonSubSessionResponse";
    private final Logger logger = LoggerFactory.getLogger(LogOnSubSessionEndpoint.class);

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "logonSubSession")
    @ResponsePayload
    public DOMSource handleRequest(@RequestPayload LogonSubSession request, MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        logger.info("handleRequest Method Start AT:{}", new Date(startTime));
        DOMSource domSource = null;
        String ipAddress = request.getString1().trim();
        String userName = request.getString2().trim();
        String password = request.getString3().trim();
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        Long mvnoId = Long.valueOf(SoapConstants.MVNOID);
        String responseMessage = SoapConstants.FAILURE;
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        Map<String, String> payload = new HashMap<String, String>();
        logger.info("Received LogOnSubSession request");
        if (ipAddress == null || ipAddress.isEmpty()) {
            logger.warn("ipAddress is null or empty");
            logger.info("LogOnSubSessionEndpoint.handleRequest method finished IN:{}MS", System.currentTimeMillis() - startTime);
            return logOnSubSessionService.createSOAP11FaultResponse(
                    SoapConstants.GENERAL_EXCEPETION,
                    LOG_ON_SUB_SESSION_RESPONSE,
                    "IPAddress is Empty or Null",
                    SoapConstants.EMPTY,
                    messageContext
            );
        } else if (userName == null || userName.isEmpty()) {
            logger.warn("userName is null or empty");
            logger.info("LogOnSubSessionEndpoint.handleRequest method finished IN:{}MS", System.currentTimeMillis() - startTime);
            return logOnSubSessionService.createSOAP11FaultResponse(
                    SoapConstants.GENERAL_EXCEPETION,
                    LOG_ON_SUB_SESSION_RESPONSE,
                    "Username is Empty or Null",
                    SoapConstants.EMPTY,
                    messageContext
            );
        } else if (password == null || password.isEmpty()) {
            logger.warn("password is null or empty");
            logger.info("LogOnSubSessionEndpoint.handleRequest method finished IN:{}MS", System.currentTimeMillis() - startTime);
            return logOnSubSessionService.createSOAP11FaultResponse(
                    SoapConstants.GENERAL_EXCEPETION,
                    LOG_ON_SUB_SESSION_RESPONSE,
                    "Password is Empty or Null",
                    SoapConstants.EMPTY,
                    messageContext
            );
        }
        if (!logOnSubSessionService.isValidIPAddress(ipAddress)) {
            logger.info("Invalid IP address: {}", ipAddress);
            logger.info("LogOnSubSessionEndpoint.handleRequest method finished IN:{}MS", System.currentTimeMillis() - startTime);
            return logOnSubSessionService.createSOAP11FaultResponse(
                    SoapConstants.GENERAL_EXCEPETION,
                    LOG_ON_SUB_SESSION_RESPONSE,
                    "IP address is not valid for logged user",
                    306,
                    messageContext
            );
        }
        try {
            LogOnSubSessionDTO dto = new LogOnSubSessionDTO(request);
            logger.info("Calling radiusClientService.logOnSubSessionRadius");
            GenericDataDTO logOnSubSession = radiusClientService.logOnSubSessionRadius(dto, SoapConstants.MVNOID);
            Long endTime = System.currentTimeMillis();
            logger.debug("Radius client service call completed in {} ms for Response: {}", (endTime - startTime), logOnSubSession);
            Object data = logOnSubSession.getData();
            if (data instanceof Map) {
//                if (liveUserData instanceof Map) {
                if (Objects.nonNull(data)) {
                    Map<String, Object> customerdata = (Map<String, Object>) data;
                    String liveUsername = customerdata.get("username").toString();
                    String livePassword = customerdata.get("password").toString();
                    logger.info("Validated credentials for user: {}", userName);
                    if (userName.equalsIgnoreCase(liveUsername) && password.equalsIgnoreCase(livePassword)) {
                        payload.put("username", userName);
                        payload.put("password", password);
                        payload.put("name", "mtik");
                        payload.put("sa", "2");
                        payload.put("framed-ip-address", ipAddress);
                    }
                    Map<String, Object> locationLockResponse = logOnSubSessionService.getLocationLockResponse(payload, SoapConstants.MVNOID, token);
                    boolean checkLocationLock = false;
                    if (locationLockResponse.get("data") != null) {
                        checkLocationLock = (boolean) locationLockResponse.get("data");
                    }
                    if (!checkLocationLock) {
                        responseCode = SoapConstants.USER_NOT_ALLOW_CODE;
                        responseMessage = "User is not allow service at This Geo location.";
                        logger.info("User is not allowed service at this Geo location");
                        if (locationLockResponse.get("status") != null) {
                            responseCode = (Integer) locationLockResponse.get("status");
                            if (responseCode == 412) {
                                logger.info("LogOnSubSessionEndpoint.handleRequest method finished IN:{}MS", System.currentTimeMillis() - startTime);
                                return logOnSubSessionService.createSOAP11FaultResponse(
                                        SoapConstants.GENERAL_EXCEPETION,
                                        LOG_ON_SUB_SESSION_RESPONSE,
                                        SoapConstants.VLAN_ID_NOT_GEO_LOCATION_NOT_MATCH,
                                        SoapConstants.VLAN_ID_NOT_GEO_LOCATION_NOT_MATCH_CODE,
                                        messageContext
                                );
                            }
                        }
                        if (locationLockResponse.get("message") != null) {
                            responseMessage = (String) locationLockResponse.get("message");
                        }
                        logger.info("LogOnSubSessionEndpoint.handleRequest method finished IN:{}MS", System.currentTimeMillis() - startTime);
                        return logOnSubSessionService.generateSuccessResponse(LOG_ON_SUB_SESSION_RESPONSE, responseCode, responseMessage, messageContext);
                    }
                    responseCode = SoapConstants.SUCCESS_CODE;
                    responseMessage = "COA successfully";
                    logger.info("User authorized successfully");
                    logger.info("LogOnSubSessionEndpoint.handleRequest method finished IN:{}MS", System.currentTimeMillis() - startTime);
                    return logOnSubSessionService.generateSuccessResponse(LOG_ON_SUB_SESSION_RESPONSE, responseCode, responseMessage, messageContext);
                } else {
                    logger.info("LogOnSubSessionEndpoint.handleRequest method finished IN:{}MS", System.currentTimeMillis() - startTime);
                    return logOnSubSessionService.createSOAP11FaultResponse(
                            SoapConstants.GENERAL_EXCEPETION,
                            LOG_ON_SUB_SESSION_RESPONSE,
                            logOnSubSession.getResponseMessage(),
                            logOnSubSession.getResponseCode(),
                            messageContext
                    );
                }
            } else {
                logger.info("LogOnSubSessionEndpoint.handleRequest method finished IN:{}MS", System.currentTimeMillis() - startTime);
                return logOnSubSessionService.createSOAP11FaultResponse(
                        SoapConstants.GENERAL_EXCEPETION,
                        LOG_ON_SUB_SESSION_RESPONSE,
                        logOnSubSession.getResponseMessage(),
                        logOnSubSession.getResponseCode(),
                        messageContext
                );
            }
        } catch (SQLException e) {
            logger.error("SQL Exception: {}", e.getMessage(), e);
            return logOnSubSessionService.createSOAP11FaultResponse(
                    "SQLException",
                    LOG_ON_SUB_SESSION_RESPONSE,
                    "SQL Exception generated for logged user",
                    SoapConstants.SQL_EXCPTION_CODE,
                    messageContext
            );
        } catch (FeignException e) {
            logger.error("Feign Client Exception: {}", e.getMessage(), e);
            if (e.getMessage().equalsIgnoreCase("Invalid location lock")) {
                return logOnSubSessionService.createSOAP11FaultResponse(
                        SoapConstants.GENERAL_EXCEPETION,
                        LOG_ON_SUB_SESSION_RESPONSE,
                        "VLAN_ID or GEO_Location does not match for logged user",
                        SoapConstants.VLAN_ID_AND_GEO_LOCATIONDOES_NOT_MATCH_CODE,
                        messageContext
                );
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                String message = "";
                int status = 404;
                try {
                    String errorMessage = e.contentUTF8();
                    JsonNode jsonNode = objectMapper.readTree(errorMessage);
                    message = jsonNode.get("msg").asText();
                    status = jsonNode.get("status").asInt();
                    if (Objects.nonNull(message)) {
                        responseCode = SoapConstants.NOT_FOUND;
                        responseMessage = message;
                        return logOnSubSessionService.createSOAP11FaultResponse(
                                SoapConstants.GENERAL_EXCEPETION,
                                LOG_ON_SUB_SESSION_RESPONSE,
                                responseMessage,
                                responseCode,
                                messageContext
                        );
                    }
                } catch (JsonProcessingException je) {
                    // Handle specific JSON processing exceptions
                    logger.error("Error processing JSON response: {}", e.getMessage());
                    throw new RuntimeException("Error processing JSON response", je);
                }
                responseCode = SoapConstants.NOT_FOUND;
                responseMessage = message;
                return logOnSubSessionService.createSOAP11FaultResponse(
                        SoapConstants.GENERAL_EXCEPETION,
                        LOG_ON_SUB_SESSION_RESPONSE,
                        responseMessage,
                        responseCode,
                        messageContext
                );
            }
        } catch (RemoteException e) {
            logger.error("Remote Exception: {}", e.getMessage(), e);
            return logOnSubSessionService.createSOAP11FaultResponse(
                    SoapConstants.GENERAL_EXCEPETION,
                    LOG_ON_SUB_SESSION_RESPONSE,
                    e.getMessage(),
                    responseCode,
                    messageContext
            );
        } catch (RuntimeException e) {
            logger.error("Unhandled Exception: {}", e.getMessage(), e);
            return logOnSubSessionService.createSOAP11FaultResponse(
                    SoapConstants.GENERAL_EXCEPETION,
                    LOG_ON_SUB_SESSION_RESPONSE,
                    "Remote Exception generated",
                    SoapConstants.REMOTE_EXCEPTION_GENERATED_CODE,
                    messageContext
            );
        } catch (Exception e) {
            logger.error("Unhandled Exception: {}", e.getMessage(), e);
            e.printStackTrace();
            return logOnSubSessionService.createSOAP11FaultResponse(
                    "Exception",
                    LOG_ON_SUB_SESSION_RESPONSE,
                    "Exception",
                    responseCode,
                    messageContext
            );
        } finally {
            logger.info("LogOnSubSessionEndpoint.handleRequest method finished IN:{}MS", System.currentTimeMillis() - startTime);
        }

    }


}
