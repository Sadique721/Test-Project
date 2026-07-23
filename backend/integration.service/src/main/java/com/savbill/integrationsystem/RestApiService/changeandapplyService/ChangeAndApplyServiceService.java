package com.savbill.integrationsystem.RestApiService.changeandapplyService;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML.ChangeServiceSubRequest;
import com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML.Override;
import com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML.ServiceSubscriptions;
import com.savbill.integrationsystem.SOAPService.service.ChangeServService;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.xml.bind.JAXBException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import java.util.Objects;

@Slf4j
@Service
public class ChangeAndApplyServiceService {
    @Autowired
    private ChangeServService changeServService;
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;

    public GenericResponse<Object> handleChangeAndApplyService(@RequestBody ChangeAndApplyServiceDTO request) {
        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        HashMap<String, Object> response = new HashMap<>();
        log.info("Started handling change and apply service for user: {}", request.getString_1());
        try {
            String userName = request.getString_1();
            ServiceSubscriptions serviceSubscriptionsDTO = request.getString_2();
            String serviceId = serviceSubscriptionsDTO.getServiceSubscriptions().get(0).getServiceId();
            List<Override> overridesValue = serviceSubscriptionsDTO.getServiceSubscriptions().get(0).getOverrides();

            String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
            userName = userName.toLowerCase().trim();

            if (userName == null || userName.isEmpty()) {
                responseCode = SoapConstants.EMPTY;
                responseMessage = "Input UserName is Empty or Null";
                log.warn("Username is empty or null . responseCode: {} responseMessage: {}", responseCode, responseMessage);
                response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.put(SoapConstants.RESPONSECODE, responseCode);
                genericResponse.setData(response);
                return genericResponse;
            }
            if (userName.contains(SoapConstants.INVALID_USERNAME)) {
                responseCode = SoapConstants.EMPTY;
                responseMessage = SoapConstants.INVALID_USERNAME_MSG;
                log.warn("Invalid username detected: {} responseCode: {} responseMessage: {} ", userName, responseCode, responseMessage);
                response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.put(SoapConstants.RESPONSECODE, responseCode);
                genericResponse.setData(response);
                return genericResponse;
            }
            if (serviceId == null || serviceId.isEmpty()) {
                responseCode = SoapConstants.EMPTY;
                responseMessage = "Service ID is Empty or Null";
                log.warn("Service ID is empty or null for user: {}. responseCode: {} responseMessage: {}", userName, responseCode, responseMessage);
                response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.put(SoapConstants.RESPONSECODE, responseCode);
                genericResponse.setData(response);
                return genericResponse;
            }
            if (overridesValue == null || overridesValue.isEmpty() || hasInvalidOverrides(overridesValue)) {
                responseCode = SoapConstants.EMPTY;
                responseMessage = "Invalid Package Configure with Empty or Null OCSCORELATION ID";
                log.warn("Overrides value is empty, null, or contains invalid data for request: {}. responseCode: {} responseMessage: {}", request, responseCode, responseMessage);
                response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.put(SoapConstants.RESPONSECODE, responseCode);
                genericResponse.setData(response);
                return genericResponse;
            }
            log.info("Checking customer entry In radius Service: {}", userName);
            Boolean checkCustomerEntryInCustTBL = changeServService.checkCustomerEntryInCustTBL(userName);
            if (!checkCustomerEntryInCustTBL) {
                responseCode = SoapConstants.NOT_FOUND;
                responseMessage = "No Records Found for Given Username.";
                log.warn("No records found for username In radius Service: {} responseCode: {} responseMessage: {} ", userName, responseCode, responseMessage);
                response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.put(SoapConstants.RESPONSECODE, responseCode);
                genericResponse.setData(response);
                return genericResponse;
            }
            Boolean usageExists = changeServService.checkCustEntryInUsageQuota(userName);
            if (!usageExists) {
                responseCode = SoapConstants.USER_DETAILS_NOT_FAOUND_IN_USAGE_TABLE_CODE;
                responseMessage = "User Details not found in Usages table for Quota Update for Given Username.";
                log.warn("User details not found in usage table for username: {}", userName);
            } else {
                try {
                    ChangeServiceSubRequest changeServiceSubRequest = new ChangeServiceSubRequest(request, serviceSubscriptionsDTO);
                    log.info("Sending request to CMS client service with Service ID: {}", serviceId);
                    ResponseEntity<?> responseEntity = cmsClientService.changeSubService(changeServiceSubRequest, SoapConstants.MVNOID, token);
                    Boolean changeServiceValidator = changeServService.changeServiceValidator(responseEntity);
                    if (changeServiceValidator) {
                        responseCode = SoapConstants.SUCCESS_CODE;
                        responseMessage = SoapConstants.SUCCESS;
                        log.info("Service change successfully applied for Service ID: {} responseCode: {} responseMessage: {}", serviceId, responseCode, responseMessage);
                    } else {
                        responseCode = SoapConstants.NOT_FOUND;
                        responseMessage = "Service ID is not Configured Properly HSQ = 0";
                        log.warn("Service ID not configured properly for Service ID: {} responseCode: {} responseMessage: {}", serviceId, responseCode, responseMessage);
                    }
                } catch (FeignException e) {
                    handleFeignException(e, response, genericResponse);
                    log.error("FeignException occurred while changing service for Service ID: {}", serviceId, e.getMessage());
                    return genericResponse;
                } catch (Exception e) {
                    responseCode = SoapConstants.NOT_FOUND;
                    responseMessage = "Service ID is not Configured in system";
                    log.error("Exception occurred while changing service for Service ID: {}", serviceId, e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (JAXBException e) {
            responseCode = SoapConstants.JAXBException;
            responseMessage = "Invalid Input XML String - Quota Provision";
            log.error("JAXBException occurred while handling change and apply service for user: {}", request.getString_1(), e.getMessage());
        } catch (SQLException e) {
            responseCode = SoapConstants.SQL_EXCPTION_CODE;
            responseMessage = "Invalid Input XML String - Change Package";
            log.error("SQLException occurred while handling change and apply service for user: {}", request.getString_1(), e.getMessage());
        } catch (RemoteException e) {
            responseCode = SoapConstants.REMOTE_EXCEPTION_GENERATED_CODE;
            responseMessage = "SubscriberProfileWebServiceException Exception due to technical issue";
            log.error("RemoteException occurred while handling change and apply service for user: {}", request.getString_1(), e.getMessage());
        } catch (Exception e) {
            responseCode = SoapConstants.InvalidActivation;
            responseMessage = "Invalid Package Configured";
            log.error("Exception occurred while handling change and apply service for user: {}", request.getString_1(), e.getMessage());
        }
        response.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
        response.put(SoapConstants.RESPONSECODE, responseCode);
        log.info("Returning response for change and apply service: {}", response);
        genericResponse.setData(response);
        return genericResponse;
    }

    private void handleFeignException(FeignException e, HashMap<String, Object> response, GenericResponse<Object> genericResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String errorMessage = e.contentUTF8();
            JsonNode jsonNode = objectMapper.readTree(errorMessage);
            String message = jsonNode.get("msg").asText();
            int status = jsonNode.get("status").asInt();
            if (Objects.nonNull(message)) {
                response.put(SoapConstants.RESPONSEMESSAGE, "InvalidSubscriberAccountException");
                response.put(SoapConstants.RESPONSECODE, 404);
                genericResponse.setData(response);
            }
        } catch (JsonProcessingException je) {
            je.printStackTrace();
            throw new RuntimeException("Error processing JSON response", je);
        }
    }

    private boolean hasInvalidOverrides(List<Override> overrides) {
        for (Override override : overrides) {
            if (override == null ||
                    isEmptyOrNull(override.getName()) ||
                    isEmptyOrNull(override.getValue())) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmptyOrNull(String str) {
        return str == null || str.trim().isEmpty();
    }
}
