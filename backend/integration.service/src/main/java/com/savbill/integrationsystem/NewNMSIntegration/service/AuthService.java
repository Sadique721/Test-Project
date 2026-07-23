package com.savbill.integrationsystem.NewNMSIntegration.service;

import com.savbill.integrationsystem.NewNMSIntegration.configuration.ApiConfig;
import com.savbill.integrationsystem.NewNMSIntegration.constants.NMSIntegrationConstant;
import com.savbill.integrationsystem.NewNMSIntegration.dto.*;
import com.savbill.integrationsystem.NewNMSIntegration.dto.AuthRequestDTO;
import com.savbill.integrationsystem.NewNMSIntegration.dto.DynamicRequestDTO;
import com.savbill.integrationsystem.NewNMSIntegration.dto.LoginRequestDTO;
import com.savbill.integrationsystem.NewNMSIntegration.dto.ONUResponseDTO;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;

import com.savbill.integrationsystem.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;


/**
 * The type Auth service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    /**
     * The Api config.
     */
    @Autowired
    private ApiConfig apiConfig;

    /**
     * The Api audits service.
     */
    @Autowired
    ApiAuditsService apiAuditsService;

    /**
     * The Required params config.
     */
    private final Map<String, Set<String>> requiredParamsConfig;

    /**
     * Login onu response dto.
     * @param request the request
     * @param userName the user name
     * @param mvnoId the mvno id
     * @return the onu response dto
     */
    public ONUResponseDTO login(LoginRequestDTO request, String userName, Long mvnoId) throws Exception {
        try {
            DynamicRequestDTO dynamicRequest = new DynamicRequestDTO();
            dynamicRequest.setApiName(NMSIntegrationConstant.API_CONSTANT.LOGIN);
            dynamicRequest.getParameters().put("grantType", request.getGrantType());
            dynamicRequest.getParameters().put("userName", request.getUserName());
            dynamicRequest.getParameters().put("value", request.getValue());
            return processAuthRequest(dynamicRequest, request.getBaseURL(), request.getPort(), userName, mvnoId);
        } catch (Exception ex) {
            log.error("Error during login method: " + ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Process auth request onu response dto.
     * @param request the request
     * @param baseURL the base url
     * @param port the port
     * @param userName the user name
     * @param mvnoId the mvno id
     * @return the onu response dto
     */
    private ONUResponseDTO processAuthRequest(DynamicRequestDTO request, String baseURL, String port, String userName, Long mvnoId) throws Exception {
        String uri = baseURL + ":" + port + "/pon/oauth/token";
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        // Convert map to DTO
        AuthRequestDTO authRequest = new AuthRequestDTO(
                request.getParameters().get("grantType"),
                request.getParameters().get("userName"),
                request.getParameters().get("value")
        );
        HttpEntity<AuthRequestDTO> entity = new HttpEntity<>(authRequest, headers);
        try {
            RestTemplate restTemplate = apiConfig.restTemplate();
            ResponseEntity<ONUResponseDTO> response = restTemplate.exchange(uri, HttpMethod.POST, entity, ONUResponseDTO.class);
            log.info("Auth Request Config URL: " + baseURL);
            log.info("Payload for WAN Config Request: " + response);
            apiAuditsService.saveAuthAudit(uri, response, headers, requestInitiationTime, userName, mvnoId.intValue(), response.getBody().getData(), NMSIntegrationConstant.API_CONSTANT.POST, null);
            return response.getBody();
        } catch (BadRequestException e) {
            e.printStackTrace();
            log.error("BadRequest Exception while processing authentication request with message: " + e.getMessage(), e);
            apiAuditsService.saveAuthAudit(uri, null, headers, requestInitiationTime, userName, mvnoId.intValue(), null, NMSIntegrationConstant.API_CONSTANT.POST, e.getMessage());
            throw new BadRequestException("Failed to process request");
        } catch (ResourceAccessException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            if(cause!=null){
                log.error("Resource Access Exception while processing authentication request with message: " + e.getMessage(), e);
                apiAuditsService.saveAuthAudit(uri, null, headers, requestInitiationTime, userName, mvnoId.intValue(), null, NMSIntegrationConstant.API_CONSTANT.POST, cause.getMessage());
                throw new Exception(cause.getMessage());
            }
            log.error("Resource Access Exception while processing authentication request with message: " + e.getMessage(), e);
            apiAuditsService.saveAuthAudit(uri, null, headers, requestInitiationTime, userName, mvnoId.intValue(), null, NMSIntegrationConstant.API_CONSTANT.POST, e.getMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception while processing authentication request with message: " + e.getMessage(), e);
            apiAuditsService.saveAuthAudit(uri, null, headers, requestInitiationTime, userName, mvnoId.intValue(), null, NMSIntegrationConstant.API_CONSTANT.POST, e.getMessage());
            throw new Exception("Failed to process request");
        }
    }

    /**
     * Gets access token.
     * @param responseDTO the response dto
     * @return the access token
     */
    public String getAccessToken(ONUResponseDTO responseDTO) {
        try {
            if (responseDTO != null && responseDTO.getData() != null) {
                return responseDTO.getData().get("accessToken");
            }
            throw new IllegalArgumentException("Invalid response or DATA object is null");
        } catch (IllegalArgumentException e) {
            log.error("Invalid response or DATA object is null", e);
            throw new IllegalArgumentException("Failed to retrieve access token");
        }
    }

    /**
     * Gets expires.
     * @param responseDTO the response dto
     * @return the expires
     */
    public String getExpires(ONUResponseDTO responseDTO) {
        try {
            if (responseDTO != null && responseDTO.getData() != null) {
                return responseDTO.getData().get("expires");
            }
            throw new IllegalArgumentException("Invalid response or DATA object is null");
        } catch (IllegalArgumentException e) {
            log.error("Invalid response or DATA object is null", e);
            throw new IllegalArgumentException("Failed to retrieve expires");
        }
    }
}


