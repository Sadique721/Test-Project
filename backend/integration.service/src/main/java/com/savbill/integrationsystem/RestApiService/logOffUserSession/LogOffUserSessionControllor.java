package com.savbill.integrationsystem.RestApiService.logOffUserSession;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.GetUserUsageSummary.GetUserSessionresponseDto;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class LogOffUserSessionControllor {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RadiusClientService radiusClientService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/logOffUserSession")
    public GenericResponse<Object> getWsLogOffUserSession(@RequestBody LogOffUserSessionDTO request) {
        Map<String, Object> response = new HashMap<>();
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        Integer responsecode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = "FAILURE";
        Boolean result = false;
        Long mvnoId = SoapConstants.MVNOID;
        String token = jwtUtil.generateJwtToken(mvnoId);

        String ipAddress = request.getIpAddress();
        if (ipAddress == null || ipAddress.isEmpty()) {
            responsecode = SoapConstants.EMPTY;
            responseMessage = "Input UserName is Empty or Null";
            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responsecode);
            response.put("result", result);
            response.put(SoapConstants.REQUESTID, requestId);
            genericResponse.setData(response);
            log.warn("Ipaddress Is Null or Empty");
            return genericResponse;
        }
        try {
            ipAddress = ipAddress.toLowerCase().trim();
            GenericDataDTO genericDataDTO = radiusClientService.GetUserSessionApi(ipAddress, SoapConstants.MVNOID);
            log.info("Data received from radius client Service BY Ip : {} ", ipAddress);
            GetUserSessionresponseDto dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()), GetUserSessionresponseDto.class);
            if (Objects.nonNull(dataMessage)) {
                Long cdrId = dataMessage.getCdrID();
                if (cdrId != null) {
                    ResponseEntity<?> responseEntity = radiusClientService.logOffUserSession(cdrId, SoapConstants.MVNOID, token);
                    if (responseEntity.getStatusCode().value() == HttpStatus.OK.value()) {
                        responsecode = SoapConstants.SUCCESS_CODE;
                        responseMessage = "LOGOUT Session successfully";
                        log.info("Session successfully Logged Out For Ip: {}", ipAddress);
                        result = true;
                    } else {
                        responsecode = SoapConstants.SUCCESS_CODE;
                        responseMessage = "LOGOUT not heppend due to some Technical issue";
                        log.warn("Session Not Logged Out due to some Technical issue For Ip: {}", ipAddress);
                        result = false;
                    }
                }
            } else {
                responsecode = SoapConstants.SUCCESS_CODE;
                responseMessage = "LOGOUT not heppend due to some Technical issue";
                log.warn("Session Not Logged Out due to some Technical issue For Ip: {}", ipAddress);
                result = false;
            }
            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responsecode);
            response.put("result", result);
            response.put(SoapConstants.REQUESTID, requestId);
            genericResponse.setData(response);
            log.info("Successfully processed request for requestId: {}, Response Code: {}, Message: {}",
                    request.getRequestId(), responsecode, responseMessage);
            return genericResponse;
        } catch (Exception e) {
            response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.put(SoapConstants.RESPONSECODE, responsecode);
            response.put("result", result);
            response.put(SoapConstants.REQUESTID, requestId);
            genericResponse.setData(response);
            log.info("Failed to log off User Session : {}", e.getMessage());
        }
        return genericResponse;
    }
}
