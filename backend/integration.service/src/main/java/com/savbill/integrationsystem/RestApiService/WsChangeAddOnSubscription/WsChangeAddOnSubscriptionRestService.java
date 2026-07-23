package com.savbill.integrationsystem.RestApiService.WsChangeAddOnSubscription;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.WsChangeAddOnSubscription.WsChangeAddOnSubscriptionResponseDto;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wschangeaddonsubscription.WsChangeAddOnSubscription;
import com.savbill.integrationsystem.generated.wschangeaddonsubscription.WsChangeAddOnSubscriptionResponse;
import com.savbill.integrationsystem.generated.wschangeaddonsubscription.WsChangeAddOnSubscriptionResponse.Return.AddOnSubscriptions;
import com.savbill.integrationsystem.utility.CommonUtilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class WsChangeAddOnSubscriptionRestService {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    CmsClientService cmsClientService;
    @Autowired
    public CommonUtilityService commonUtilityService;

    public GenericResponse<Object> handleChangeSubscribeAddOn(WsChangeAddOnSubscription request) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        WsChangeAddOnSubscriptionResponseDto responseEntity = null;
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;
        WsChangeAddOnSubscriptionResponse.Return.AddOnSubscriptions responseData = new AddOnSubscriptions();

        try {
            log.info("Handling change subscription request...");
            if (null == request.getSubscriptionStatusValue()) {
                responseMessage = "Input SubscriptionStatus Null ";
                responseCode = SoapConstants.INPUT_MISSING_CODE;
                genericDataDTO.setResponseCode(responseCode);
                genericDataDTO.setResponseMessage(responseMessage);
                genericResponse.setData(genericDataDTO);
            } else {
                Long mvnoId = SoapConstants.MVNOID;
                String token = jwtUtil.generateJwtToken(mvnoId);
                genericDataDTO = cmsClientService.changeAddOnSubscription(request, mvnoId, token);
                responseMessage = genericDataDTO.getResponseMessage();

                if (responseMessage.contains("AddOn subcription not found by subscriberId:")) {
                    responseMessage = "AddOn subcription not found by subscriberId";
                    responseCode = SoapConstants.INPUT_MISSING_CODE;
                    genericDataDTO.setResponseCode(responseCode);
                    genericDataDTO.setResponseMessage(responseMessage);
                    genericResponse.setData(genericDataDTO);
                    return genericResponse;
                } else if (responseMessage.contains("Plan already expired")) {
                    responseMessage = "Plan already expired";
                    responseCode = SoapConstants.NOT_FOUND;
                    genericDataDTO.setResponseCode(responseCode);
                    genericDataDTO.setResponseMessage(responseMessage);
                    genericResponse.setData(genericDataDTO);
                    return genericResponse;
                } else if (Objects.nonNull(genericDataDTO.getData())) {
                    log.debug("Data received from CMS client service: {}", genericDataDTO.getData());
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());

                    responseEntity = objectMapper.convertValue(genericDataDTO.getData(), WsChangeAddOnSubscriptionResponseDto.class);
                    log.debug("Converted response entity: {}", responseEntity);
                }
                if (!genericDataDTO.getResponseMessage().isEmpty() && genericDataDTO.getResponseMessage().equalsIgnoreCase("Invalid subscription status received")) {
                    responseCode = SoapConstants.INPUT_MISSING_CODE;
                    responseMessage = SoapConstants.INVALID_SUBSCRIPTION_STATUS_RECEIVED;
                    log.warn("Received invalid subscription status message.");
                } else {
                    if (responseEntity != null) {
                        long endTime = commonUtilityService.localStartDateTime(responseEntity.getEndTime().toString());
                        responseData.setAddOnId(responseEntity.getAddOnId().toString());
                        responseData.setAddOnName(responseEntity.getAddOnName());
                        responseData.setAddOnStatus(commonUtilityService.statusValue(request.getSubscriptionStatusValue().intValue()));
                        responseData.setSubscriberIdentity(responseEntity.getSubscriberIdentity());
                        responseData.setEndTime(endTime);
                        responseData.setParameter1("");
                        responseData.setParameter2("");
                        responseData.setAddonSubscriptionId(responseEntity.getAddonSubscriptionId().toString());
                        responseData.setUsageResetTime(endTime);

                        responseCode = SoapConstants.SUCCESS_CODE;
                        responseMessage = SoapConstants.SUCCESS;

                        genericDataDTO.setData(responseData);
                        log.info("Successfully processed change subscription response.");
                    } else {
                        responseCode = SoapConstants.INPUT_MISSING_CODE;
                        responseMessage = SoapConstants.INVALID_SUBSCRIPTION_STATUS_RECEIVED;
                        log.error("Failed to parse response entity, invalid data.");
                    }
                }
            }
        } catch (Exception e) {
            responseMessage = SoapConstants.FAILURE;
            responseCode = SoapConstants.INTERNAL_ERROR;
            log.error("Error processing change subscription request: ", e);
        }
        genericDataDTO.setResponseCode(responseCode);
        genericDataDTO.setResponseMessage(responseMessage);
        genericResponse.setData(genericDataDTO);
        log.info("Completed handleChangeSubscribeAddOn with response code: {} and message: {}", responseCode, responseMessage);
        return genericResponse;
    }

}
