package com.savbill.integrationsystem.RestApiService.LoggOffSubSessions;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class LoggOffSubSessionsController {

    @Autowired
    private RadiusClientService radiusClientService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/logOffSubSessions")
    public GenericResponse<Object> handleRequest(@RequestBody LogOffSessionsDto request) {
        Map<String, Object> responseData = new HashMap<>();
        GenericResponse<Object> response = new GenericResponse<>();
        String responseMessage = SoapConstants.FAILURE;
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String username = request.getString1();
        log.info("Received logOffSubSessions request with username: {}", username);

        try {
            if (username == null || username.isEmpty()) {
                log.warn("Username is null or empty: {}", username);
                responseMessage = SoapConstants.Input_Username_is_Empty_or_null;
                responseCode = SoapConstants.EMPTY;
                responseData.put(SoapConstants.RESPONSECODE, responseCode);
                responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.setData(responseData);
                log.info("Response: {} with code {}", responseMessage, responseCode);
                return response;
            } else {
                log.info("Attempting to log off sub-sessions for username: {}", username);
                GenericDataDTO genericDataDTO = radiusClientService.LoggOffSubSessions(username, SoapConstants.MVNOID);

                if (genericDataDTO.getData() != null) {
                    responseMessage = SoapConstants.SUCCESS;
                    responseCode = SoapConstants.SUCCESS_CODE;
                    responseData.put("result", true);
                    log.info("Sub-sessions logged off successfully for username: {}", username);
                } else {
                    log.warn("No data found for username: {}", username);
                    responseMessage = SoapConstants.INPUT_USERNAME_UNKNOWN;
                    responseCode = SoapConstants.UNKNOWN;
                }
            }
        } catch (Exception e) {
            log.error("Exception occurred while processing the request for username: {}: ", username, e);
            responseMessage = SoapConstants.SQL_EXCEPTION;
            responseCode = SoapConstants.INTERNAL_ERROR;
        }
        responseData.put(SoapConstants.RESPONSECODE, responseCode);
        responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
        response.setData(responseData);
        log.info("Response: {} with code {}", responseMessage, responseCode);
        return response;
    }
}
