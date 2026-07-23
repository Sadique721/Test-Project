package com.savbill.integrationsystem.RestApiService.WsChangeTOpUpSubscription;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.WsChangeTopUpSubscription.WsChangeTopUpSubscriptionSoapResponse;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wschangetopupsubscription.WsChangeTopUpSubscription;
import com.savbill.integrationsystem.generated.wschangetopupsubscription.WsChangeTopUpSubscriptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Slf4j
@Service
public class WsChangeTopUpSubscriptionRestService {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    CmsClientService cmsClientService;

    public GenericResponse<Object> handleChangeSubscribeTopUp(WsChangeTopUpSubscription request) {
        log.info("Starting handleChangeSubscribeTopUp for User: {}", request.getSubscriberId());
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        WsChangeTopUpSubscriptionSoapResponse responseEntity = null;

        int responseCode = SoapConstants.INTERNAL_ERROR;
        String responseMessage = SoapConstants.FAILURE;
        try {
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);

            genericDataDTO = cmsClientService.changeTopUpSubscription(request, mvnoId, token);
            responseMessage = genericDataDTO.getResponseMessage();
            log.debug("Response message from CMS client service: {}", responseMessage);

            if (responseMessage.contains("Plan already expired")) {
                responseCode = SoapConstants.NOT_FOUND;
                responseMessage = "Plan already expired";
                log.warn("Plan already expired for This Id: {}", request.getTopUpSubscriptionId());

            } else if (responseMessage.contains("Invalid subscription status received")) {
                responseCode = SoapConstants.INPUT_MISSING_CODE;
                responseMessage = "Invalid subscription status received";
                log.warn("Invalid subscription status received for Subscriber: {}", request.getSubscriberId());

            } else if (Objects.nonNull(genericDataDTO.getData())) {
                log.debug("Data received from CMS client service: {}", genericDataDTO.getData());
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                responseEntity = objectMapper.convertValue(genericDataDTO.getData(), WsChangeTopUpSubscriptionSoapResponse.class);
                log.debug("Converted response entity: {}", responseEntity);

                if (responseEntity != null) {
                    responseEntity.setTopUpStatus(subscriptionStatusValue(Integer.valueOf(request.getSubscriptionStatusValue())));
                    long endTime = parseEndTime(responseEntity.getEndTime().toString());
                    log.debug("Parsed end time: {}", endTime);
                    WsChangeTopUpSubscriptionResponse.Return.TopUpSubscriptions
                            responseData = new WsChangeTopUpSubscriptionResponse.Return.TopUpSubscriptions();
                    responseData.setEndTime(endTime);
                    responseData.setSubscriberIdentity(responseEntity.getSubscriberIdentity());
                    responseData.setTopUpId(responseEntity.getTopUpId().toString());
                    responseData.setTopUpName(responseEntity.getTopUpName());
                    responseData.setTopUpStatus(responseEntity.getTopUpStatus());
                    responseData.setTopUpSubscriptionId(responseEntity.getTopUpSubscriptionId().toString());
                    responseData.setUsageResetTime(endTime);

                    responseCode = SoapConstants.SUCCESS_CODE;
                    responseMessage = SoapConstants.SUCCESS;
                    genericDataDTO.setData(responseData);
                    log.info("Successfully processed Change Top-Up Subscription request for SubscriberId: {},topUpSubscriptionId: {} subscriptionStatusValue: {}", request.getSubscriberId(), request.getTopUpSubscriptionId(), request.getSubscriptionStatusValue());
                }
            }
        } catch (Exception e) {
            log.error("Error processing Change Top-Up Subscription request", e);
            responseCode = SoapConstants.INTERNAL_ERROR;
            responseMessage = SoapConstants.FAILURE;

        }

        genericDataDTO.setResponseCode(responseCode);
        genericDataDTO.setResponseMessage(responseMessage);
        genericResponse.setData(genericDataDTO);
        log.info("Completed handleChangeSubscribeTopUp with response code: {} and message: {}", responseCode, responseMessage);
        return genericResponse;
    }

    private long parseEndTime(String endTimeString) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(endTimeString);
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception e) {
            log.error("Error parsing end time: {}", endTimeString, e);
            return 0;
        }
    }

    public String subscriptionStatusValue(Integer status) {
        log.debug("Converting subscription status value: {}", status);
        switch (status) {
            case 0:
                return "Subscribed";
            case 1:
                return "Start Scheduled";
            case 2:
                return "Active";
            case 3:
                return "Expiry Scheduled";
            case 4:
                return "Expired";
            case 5:
                return "Unsubscribed";
            case 6:
                return "Approval Pending";
            case 7:
                return "Rejected";
            default:
                return "Unknown";
        }
    }
}