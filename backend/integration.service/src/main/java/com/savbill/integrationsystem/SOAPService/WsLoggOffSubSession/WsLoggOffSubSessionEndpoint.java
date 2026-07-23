package com.savbill.integrationsystem.SOAPService.WsLoggOffSubSession;

import com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.soap.SOAPException;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Endpoint
public class WsLoggOffSubSessionEndpoint {

    @Autowired
    private RadiusClientService radiusClientService;

    @Autowired
    private JwtUtil jwtUtil;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "logoffSubSession")
    @ResponsePayload
    public DOMSource getLogoffSubSession(@RequestPayload LoggOffSubsession request, MessageContext messageContext) throws SOAPException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        long startTime = System.currentTimeMillis();
        log.info("Starting getLogoffSubSession At: {}", new Date(startTime));
        String responseData = null;
        GenericDataDTO genericDataDTO = null;
        String ipAddress = request.getString_1().trim();
        try {
            if (ipAddress == null || StringUtils.isEmpty(ipAddress)) {
                log.info("Request parameter is null or empty");
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidIPAddressException",
                        "IP Address is Empty or Null",
                        "ecaaa1",
                        messageContext
                );

            }
            if (!isValidIPAddress(ipAddress)) {
                log.info("Request Ip is Invalid format");
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidIPAddressException",
                        "Invalid IP Address format",
                        "ecaaa1",
                        messageContext
                );
            }
            log.debug("call Radius Client To perform LoggOffSubsession operation for:{}", request.getString_1());
            genericDataDTO = radiusClientService.LoggOffSubSession(ipAddress, SoapConstants.MVNOID);
            responseData = genericDataDTO.getData() != null ? genericDataDTO.getData().toString() : null;
            log.info("Integration Received Response In:{}Ms, Response Data:{}", System.currentTimeMillis() - startTime, responseData);

            if (responseData != null && !responseData.isEmpty()) {
                log.info("Response fetched and return successfully");
                return CustomResponseGenerator.generateSoap11LogoffSubSessionsResponse("logoffSubSessionResponse", messageContext);
            } else {
                log.info("No data found for the given subscriber account");
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "SQLException",
                        "SQL Exception",
                        "ecaaa1",
                        messageContext
                );
            }

        } catch (Exception e) {
            log.info("SQLException Occurred due to {}", e.getCause());
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "generalException",
                    "SQLException",
                    "SQL Exception",
                    "ecaaa1",
                    messageContext
            );

        } finally {
            long endTime = System.currentTimeMillis();
            log.info("getLogoffSubSessions completed in {} ms for ipAddress: {}",
                    (endTime - startTime), ipAddress);
        }

    }

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }
}
