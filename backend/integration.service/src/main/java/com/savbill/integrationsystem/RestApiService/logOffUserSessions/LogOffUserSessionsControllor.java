package com.savbill.integrationsystem.RestApiService.logOffUserSessions;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class LogOffUserSessionsControllor {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RadiusClientService radiusClientService;

    @PostMapping("/getLogOffUserSession")
    public GenericResponse getLogOffUserSession(@RequestBody LogOffUserSessions request) {
        log.info("Received request to log off user session: {}", request);
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        Map<String, Object> response = new HashMap<>();
        GenericResponse genericResponse = new GenericResponse();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = "FAILURE";
        Boolean result = false;
        try {
            if (request != null) {
                genericResponse = getWsLogOffUserSession(request, requestId, genericResponse);
                log.info("Successfully processed logoff request for user: {}", request.getUserName());
            } else {
                log.warn("Logoff User Sessions Request is null");
                response.put(SoapConstants.RESPONSEMESSAGE, "Logoff User Sessions Request is null.");
                response.put(SoapConstants.RESPONSECODE, responseCode);
                response.put("result", result);
                response.put(SoapConstants.REQUESTID, requestId);
                genericResponse.setData(response);
            }
        } catch (Exception e) {
            log.error("Exception occurred while processing logoff request: {}", e.getMessage(), e);
            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responseCode);
            response.put("result", result);
            response.put(SoapConstants.REQUESTID, requestId);
            genericResponse.setData(response);
        }
        return genericResponse;
    }

    public GenericResponse getWsLogOffUserSession(LogOffUserSessions request, String requestId, GenericResponse genericResponse) {
        log.info("Processing logoff for user: {}", request.getUserName());
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = "FAILURE";
        Boolean result = false;
        Map<String, Object> response = new HashMap<>();
        Long mvnoId = SoapConstants.MVNOID;
        String token = jwtUtil.generateJwtToken(mvnoId);

        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            response.put(SoapConstants.RESPONSECODE, SoapConstants.EMPTY);
            response.put(SoapConstants.RESPONSEMESSAGE, "Input UserName is Empty or Null.");
            response.put("result", result);
            response.put(SoapConstants.REQUESTID, requestId);
            genericResponse.setData(response);
            log.warn("UserName is Empty or Null ResponseCode: {}", responseCode);
            return genericResponse;
        }
        try {
            String userName = request.getUserName().toLowerCase().trim();
            log.info("Radius Client Calling..To Check LiveUser Login Status with user: {}", userName);
            GenericDataDTO genericDataDTO = radiusClientService.getLiveUserLoginStatus(userName, SoapConstants.MVNOID);
            if (genericDataDTO.getData() instanceof Map) {
                Map<String, Object> dataMessage = (Map<String, Object>) genericDataDTO.getData();
                if (Objects.nonNull(dataMessage) && dataMessage.get("cdrID") != null) {
                    String cdrId = dataMessage.get("cdrID").toString();
                    if (!cdrId.isEmpty()) {
                        log.info("Radius Client Calling To Disconnect user: {}", userName);
                        ResponseEntity<?> responseEntity = radiusClientService.logOffUserSession(Long.parseLong(cdrId), SoapConstants.MVNOID, token);
                        if (responseEntity.getStatusCode().value() == HttpStatus.OK.value()) {
                            responseCode = SoapConstants.SUCCESS_CODE;
                            responseMessage = "LOGOUT session successfully";
                            result = true;
                            log.info("User session logged out successfully: {} with ResponseCode: {}", userName, responseCode);
                        } else {
                            responseCode = SoapConstants.SUCCESS_CODE;
                            responseMessage = "LOGOUT not happend due to some Techincal issue";
                            log.warn("Logout failed due to technical issue for user: {},ResponseCode: {}", userName, responseCode);
                        }
                    }
                } else {
                    responseCode = SoapConstants.SUCCESS_CODE;
                    responseMessage = "LOGOUT not happend due to some Techincal issue";
                    log.warn("Logout failed Radius Client Unable To Disconnect user: {},ResponseCode: {}", userName, responseCode);
                }
            } else {
                responseCode = SoapConstants.SUCCESS_CODE;
                responseMessage = "LOGOUT not happend due to some Techincal issue";
                log.warn("Logout failed Radius Client Unable To Find LiveUser : {},ResponseCode: {}", userName, responseCode);
            }
        } catch (Exception e) {
            responseCode = SoapConstants.INTERNAL_ERROR;
            responseMessage = "Failed to log off User Sessions due to technical issue";
            log.error("Failed to log off User Sessions: {}", e.getMessage(), e);
        }
        response.put(SoapConstants.REQUESTID, requestId);
        response.put(SoapConstants.RESPONSECODE, responseCode);
        response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
        response.put("result", result);
        genericResponse.setData(response);

        return genericResponse;
    }
}
