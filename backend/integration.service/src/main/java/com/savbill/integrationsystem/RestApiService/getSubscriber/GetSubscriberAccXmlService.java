package com.savbill.integrationsystem.RestApiService.getSubscriber;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.SubscriberAccount.GetSubscriberAccountDetailsDTO;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.SQLException;
import java.util.Objects;

@Slf4j
@Service
public class GetSubscriberAccXmlService {
    @Autowired
    private CmsClient cmsClient;
    @Autowired
    private RadiusClientService radiusClientService;

    public GenericDataDTO getSubscriberAccount(@RequestBody GetSubscriberAccount request) {
        GenericDataDTO response = new GenericDataDTO();
        String userName = request.getString1().trim();
        userName = userName.toLowerCase().trim();

        if (userName == null || userName.isEmpty()) {
            response.setResponseCode(SoapConstants.EMPTY);
            response.setResponseMessage("Input Username is Empty or Null");
            log.warn("Received empty or null username in the request.");
            return response;
        }
        try {
            GenericDataDTO genericDataDTO = radiusClientService.getSubscriberAccountDetails(userName, SoapConstants.MVNOID);
            if (genericDataDTO.getResponseCode() == 503) {
                response.setResponseCode(genericDataDTO.getResponseCode());
                response.setResponseMessage(genericDataDTO.getResponseMessage());
                log.warn("Received 503 error from Radius API for username: {}", userName);
                return response;
            }
            if (Objects.nonNull(genericDataDTO.getData())) {
                GetSubscriberAccountDetailsDTO dto = new ObjectMapper().registerModule(new JavaTimeModule())
                        .readValue(new ObjectMapper().writeValueAsString(genericDataDTO.getData()), GetSubscriberAccountDetailsDTO.class);
                if (Objects.nonNull(dto)) {
                    response.setData(dto);
                    response.setResponseCode(SoapConstants.SUCCESS_CODE);
                    response.setResponseMessage(SoapConstants.SUCCESS);
                    log.info("Successfully retrieved account details for username: {}", userName);
                }
            } else {
                response.setResponseMessage("Username is not available in SPR Table");
                response.setResponseCode(SoapConstants.NOT_FOUND);
                log.info("Could not find user in Customer Table for username: {}", userName);
            }
        } catch (FeignException e) {
            log.error("FeignException occurred while calling Radius API: {}", e.getMessage(), e);
            handleFeignException(e, response);
        } catch (RuntimeException e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("AxisFault Exception due to technical issue: " + e.getMessage());
            log.error("RuntimeException due to technical issue for username: {}: {}", userName, e.getMessage(), e);
        } catch (SQLException e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("SQL Exception: " + e.getMessage());
            log.error("SQLException encountered for username: {}: {}", userName, e.getMessage(), e);
        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error occurred for username: {}: {}", userName, e.getMessage(), e);
        }
        return response;
    }

    private void handleFeignException(FeignException e, GenericDataDTO response) {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = "";
        int status = 404;
        if (e instanceof feign.RetryableException) {
            response.setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value());
            response.setResponseMessage("Radius service is currently unavailable. Please try again later.");
            log.error("RetryableException occurred - Radius service is down: {}", e.getMessage());
            return;
        }
        try {
            String errorMessage = e.contentUTF8();
            JsonNode jsonNode = objectMapper.readTree(errorMessage);
            message = jsonNode.get("msg").asText();
            status = jsonNode.get("status").asInt();
            if (Objects.nonNull(message)) {
                response.setResponseMessage(message);
                response.setResponseCode(status);
            }
        } catch (JsonProcessingException je) {
            log.error("Error processing JSON response from FeignException: {}", je.getMessage(), je);
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Error processing JSON response");
        } catch (Exception ex) {
            log.error("Unexpected error while handling FeignException: {}", ex.getMessage(), ex);
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Unexpected error occurred while processing the error response");
        }
        response.setResponseCode(status);
        response.setResponseMessage(message);
    }
}
