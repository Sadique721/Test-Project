package com.savbill.integrationsystem.RestApiService.RemoveService;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccount;
import com.savbill.integrationsystem.generated.removeservice.RemoveService;
import com.savbill.integrationsystem.generated.removeservice.RemoveServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class RemoveServiceRestService {
    @Autowired
    private CmsClientService cmsClientService;

    @Autowired
    private JwtUtil jwtUtil;

    public GenericResponse<Object> handleRemoveService(RemoveService request) throws Exception {
        Map<String, Object> response = new HashMap<>();
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        RemoveServiceResponse removeServiceResponse = new RemoveServiceResponse();
        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;
        String username = request.getString1().trim();
        String serviceId = request.getString2().trim();
        log.info("Received request to remove service for username: {} and serviceId: {}", username, serviceId);
        try {
            if (username.isEmpty()) {
                log.warn("Username is empty or null");
                responseMessage = SoapConstants.Input_Username_is_Empty_or_null;
                responseCode = SoapConstants.EMPTY;
            } else if (request.getString2().isEmpty()) {
                log.warn("ServiceId is empty or null");
                responseMessage = SoapConstants.Input_ServiceId_is_Empty_or_null;
                responseCode = SoapConstants.EMPTY;
            } else {
                Long mvnoId = SoapConstants.MVNOID;
                String token = jwtUtil.generateJwtToken(mvnoId);

                log.info("Calling cmsClientService to remove service for user: {}", username);
                ResponseEntity<?> responseEntity = cmsClientService.removeService(request, mvnoId, token);
                Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();

                if (responseBody != null && "not available".equals(responseBody.get("Failure"))) {
                    log.error("Failed to update record in SPR table Customer: {} is Active", username);
                    responseMessage = SoapConstants.NOT_UPDATED_RECORD_IN_SPR_TABLE_DUE_TO_TECHNICAL_ISSUES;
                    responseCode = SoapConstants.NOT_FOUND;
                } else if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    if (responseBody != null && responseBody.get("status") != null
                            && (Integer) responseBody.get("status") == 200) {
                        log.info("Service successfully removed : {}", serviceId);
                        responseMessage = SoapConstants.SUCCESS;
                        responseCode = SoapConstants.SUCCESS_CODE;
                    }
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while processing the request for user: {}. Error: {}", username, e.getMessage());
            responseCode = responseCode;
            responseMessage = "An error occurred while processing the request";
        }
        response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
        response.put(SoapConstants.RESPONSECODE, responseCode);
        genericResponse.setData(response);
        log.info("Response generated with code: {} and message: {}", responseCode, responseMessage);
        return genericResponse;
    }
}
