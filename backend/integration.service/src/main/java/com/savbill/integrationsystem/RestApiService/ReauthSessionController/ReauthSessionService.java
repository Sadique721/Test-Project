package com.savbill.integrationsystem.RestApiService.ReauthSessionController;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ReauthSessionService {
    //private final Logger logger = LoggerFactory.getLogger(ReauthSessionService.class);

    @Autowired
    RadiusClientService radiusClientService;

    public GenericResponse<Object> handleReauthSession(@RequestBody ReAuthSessionDto request) {
        Map<String, Object> responseData = new HashMap<>();
        GenericResponse<Object> response = new GenericResponse<Object>();
        String responseMessage = SoapConstants.FAILURE;
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String username = request.getSubscriberId().trim();
        log.info("Received request to handle re-auth session for subscriberId: {}", username);

        try {
            if (username == null || username.isEmpty()) {
                log.warn("Input subscriberId is null or empty.");
                responseCode = SoapConstants.EMPTY;
                responseMessage = "Input subscriberId is null or empty.";
                responseData.put(SoapConstants.RESPONSECODE, responseCode);
                responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.setData(responseData);
                return response;
            }

            log.info("Requesting re-auth From Radius service session for subscriberId: {}", username);
            GenericDataDTO radiusResponse = radiusClientService.ReAuthSession(username, SoapConstants.MVNOID);

            if (radiusResponse.getData() == null) {
                log.debug("Session not found for subscriberId: {}", username);
                responseCode = SoapConstants.NOT_FOUND;
                responseMessage = "NOT FOUND. Unable to re-auth session(s) by subscriber Id:" + username + ". Reason: Session not found while performing Re-Auth for Id: " + username;
                responseData.put(SoapConstants.RESPONSECODE, responseCode);
                responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.setData(responseData);
                return response;
            }

            int status = radiusResponse.getResponseCode();
            if (200 == status) {
                log.info("Re-auth session successful for subscriberId: {}", username);
                responseCode = SoapConstants.SUCCESS_CODE;
                responseMessage = SoapConstants.SUCCESS;
            } else if (404 == status) {
                log.warn("Session not found for subscriberId: {}", username);
                responseCode = 404;
                responseMessage = "NOT FOUND. Unable to re-auth session(s) by subscriber Id:" + username + ". Reason: Session not found while performing Re-Auth for Id: " + username;
            } else {
                log.debug("Unexpected status from radius for subscriberId: {}. Status: {}", username, status);
                responseCode = SoapConstants.NOT_FOUND;
                responseMessage = "Unexpected status from radius: " + status;
            }
        } catch (Exception e) {
            log.error("An error occurred while processing the request from radius for subscriberId: {}", username, e);
            responseCode = SoapConstants.NOT_FOUND;
            responseMessage = "An error occurred while processing the request from radius";
        }

        responseData.put(SoapConstants.RESPONSECODE, responseCode);
        responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
        response.setData(responseData);
        log.info("Returning response for subscriberId: {} with responseCode: {} and responseMessage: {}",
                username, responseCode, responseMessage
        );
        return response;
    }
}
