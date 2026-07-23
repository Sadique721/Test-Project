package com.savbill.integrationsystem.RestApiService.LoggOffSubSession;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;

import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class LoggOffSubSessionController {

    @Autowired
    private RadiusClientService radiusClientService;

    @Autowired
    private JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(LoggOffSubSessionController.class);

    @PostMapping("/logOffSubSession")
    public GenericDataDTO handleRequest(@RequestBody LoggOffSubbsessionDto request) {
        try {
            return getLogOffSubSesion(request);
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            return createResponse(SoapConstants.INTERNAL_ERROR, "An unexpected error occurred", null);
        }
    }

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }

    public GenericDataDTO getLogOffSubSesion(LoggOffSubbsessionDto request) {
        String ipAddress = request.getString1().trim();
        if (StringUtils.isEmpty(ipAddress)) {
            logger.warn("IP Address is missing");
            return createResponse(SoapConstants.INPUT_MISSING_CODE, "IP Address is empty or null", null);
        }

        if (!isValidIPAddress(ipAddress)) {
            logger.warn("Invalid IP Address format: {}", ipAddress);
            return createResponse(SoapConstants.InvalidActivation, "Invalid IP Address format", null);
        }

        try {
            GenericDataDTO result = radiusClientService.LoggOffSubSession(ipAddress, SoapConstants.MVNOID);
            if (result.getData() != null) {
                logger.info("Session log-off successful for IP: {}", ipAddress);
                return createResponse(SoapConstants.SUCCESS_CODE, SoapConstants.SUCCESS, result.getData());
            } else {
                logger.warn("No session data found for IP: {}", ipAddress);
                return createResponse(304, "No session data found", null);
            }
        } catch (Exception ex) {
            logger.error("Error during session log-off for IP: {}", ipAddress, ex);
            return createResponse(500, ex.getMessage(), null);
        }
    }

    private GenericDataDTO createResponse(int code, String message, Object data) {
        GenericDataDTO response = new GenericDataDTO();
        response.setResponseCode(code);
        response.setResponseMessage(message);
        response.setData(data);
        return response;
    }
}
