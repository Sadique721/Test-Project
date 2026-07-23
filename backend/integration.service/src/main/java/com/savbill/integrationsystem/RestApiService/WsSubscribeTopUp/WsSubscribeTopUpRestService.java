package com.savbill.integrationsystem.RestApiService.WsSubscribeTopUp;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOn;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOnResponse;
import com.savbill.integrationsystem.generated.wssubscribetopup.WsSubscribeTopUp;
import com.savbill.integrationsystem.generated.wssubscribetopup.WsSubscribeTopUpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

@Slf4j
@Service
public class WsSubscribeTopUpRestService {
    @Autowired
    private CmsClientService cmsClientService;

    @Autowired
    private JwtUtil jwtUtil;

    public GenericResponse<Object> handleSubscribeTopUp(WsSubscribeTopUp request) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Starting handleSubscribeTopUp for user: {} AT:{}", request.getSubscriberId(),new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;
        try {
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);

            log.debug("Call CmsClient To SubscriberTopUp:{}",request.getSubscriberId());
            GenericDataDTO responseEntity = cmsClientService.wsSubscribeTopUp(request, mvnoId, token);
            log.debug("Received response AT:{}MS from CMS client service: {}", System.currentTimeMillis()-startTime,responseEntity.getResponseMessage());


            if (responseEntity.getResponseMessage().equalsIgnoreCase("Customer not available")) {
                responseCode = SoapConstants.INTERNAL_ERROR;
                responseMessage = SoapConstants.CUSTOMER_NOT_AVAILABLE;
                log.warn("Customer not available Input User: {},ResponseCode: {}", request.getSubscriberId(), responseCode);
            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("planData not available")) {
                responseCode = SoapConstants.INTERNAL_ERROR;
                responseMessage = SoapConstants.PLAN_DATA_NOT_AVAILABLE;
                log.warn("Plan data not available for topUpPackageName: {}", request.getTopUpPackageName());
            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("Expiry date can not be less than start date!")) {
                responseCode = SoapConstants.INPUT_MISSING_CODE;
                responseMessage = SoapConstants.ENDDATE_CANNOT_BE_LESS_THEN_STARTDATE;
                log.warn("Invalid date selection for topUpPackageName: {}", request.getTopUpPackageName());
            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("OK")) {
                responseCode = SoapConstants.SUCCESS_CODE;
                responseMessage = SoapConstants.SUCCESS;
                log.info("Successfully processed subscription top-up for Customer:{} With TopUpPackageName: {},ResponseCode: {}", request.getSubscriberId(), request.getTopUpPackageName(), responseCode);
                Object data = responseEntity.getData();
                WsSubscribeTopUpResponse.Return.TopUpSubscriptions response = new WsSubscribeTopUpResponse.Return.TopUpSubscriptions();
                if (data instanceof LinkedHashMap) {
                    LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) data;

                    ArrayList<Object> planList = (ArrayList<Object>) dataMap.get("planList");

                    if (planList != null && !planList.isEmpty()) {
                        LinkedHashMap<String, Object> lastPlanMap = (LinkedHashMap<String, Object>) planList.get(planList.size() - 1);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
                        LocalDateTime localStartDateTime = LocalDateTime.parse(lastPlanMap.get("startDate").toString(), formatter);
                        long startDate = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        LocalDateTime localEndDateTime = LocalDateTime.parse(lastPlanMap.get("endDate").toString(), formatter);
                        long endtDate = localEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        Integer planIdInteger = (Integer) lastPlanMap.get("planId");

                        response.setStartTime(startDate);
                        response.setEndTime(endtDate);
                        response.setSubscriberIdentity(String.valueOf(dataMap.get("username")));
                        response.setTopUpId(planIdInteger.longValue());
                        response.setTopUpName((String) lastPlanMap.get("planName"));
                        response.setTopUpStatus((String) lastPlanMap.get("custPlanStatus"));
                        response.setTopUpSubscriptionId(dataMap.get("custPackagId").toString());
                        response.setUsageResetTime(endtDate);
                        log.info("Successfully built response for Customer: {}", request.getSubscriberId());
                        genericDataDTO.setData(response);

                    }
                }
            }

        } catch (Exception e) {

            responseMessage = SoapConstants.FAILURE;
            responseCode = SoapConstants.INTERNAL_ERROR;
            log.error("Exception occurred while handling subscription top-up: ", e);
        }
        genericDataDTO.setResponseCode(responseCode);
        genericDataDTO.setResponseMessage(responseMessage);
        genericResponse.setData(genericDataDTO);
        log.info("Returning generic response with code: {} and message: {}", responseCode, responseMessage);
        return genericResponse;
    }
}
