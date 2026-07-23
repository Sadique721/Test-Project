package com.savbill.integrationsystem.RestApiService.WsSubscribeAddOn;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOn;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOnResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

@Slf4j
@Service
public class WsSubscribeAddOnRestservice {
    @Autowired
    private CmsClientService cmsClientService;

    @Autowired
    private JwtUtil jwtUtil;

    public GenericResponse<Object> handleSubscribeAddOn(WsSubscribeAddOn request) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;
        long startTime = System.currentTimeMillis();
        log.info("Started handleSubscribeAddOn Method At:{}", new Date(startTime));

        try {
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);

            GenericDataDTO responseEntity = cmsClientService.wsSubscribeAddon(request, mvnoId, token);
            log.debug("Response received IN:{} from CMS client service: {}", System.currentTimeMillis() - startTime, responseEntity.getResponseMessage());
            if (responseEntity.getResponseMessage().equalsIgnoreCase("Customer not available")) {
                responseCode = SoapConstants.INTERNAL_ERROR;
                responseMessage = SoapConstants.CUSTOMER_NOT_AVAILABLE;
                log.warn("Customer: {} is not available In System", request.getSubscriberId());
            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("planData not available")) {
                responseCode = SoapConstants.INTERNAL_ERROR;
                responseMessage = SoapConstants.PLAN_DATA_NOT_AVAILABLE;
                log.warn("Plan data not available for MVNO ID: {}", mvnoId);
            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("Expiry date can not be less than start date!")) {
                responseCode = SoapConstants.INPUT_MISSING_CODE;
                responseMessage = SoapConstants.ENDDATE_CANNOT_BE_LESS_THEN_STARTDATE;
                log.warn("Expiry date: {} is less than start date for AddOnPackageName: {}", request.getEndTime(), request.getAddOnPackageName());
            } else if (responseEntity.getResponseMessage().equalsIgnoreCase("OK")) {
                log.info("Successfully Subscriber:{} AdON:{}", request.getSubscriberId(), request.getAddOnPackageName());
                responseCode = SoapConstants.SUCCESS_CODE;
                responseMessage = SoapConstants.SUCCESS;
                Object data = responseEntity.getData();
                WsSubscribeAddOnResponse.Return.AddOnSubscriptions response = new WsSubscribeAddOnResponse.Return.AddOnSubscriptions();
                if (data instanceof LinkedHashMap) {
                    LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) data;

                    ArrayList<Object> planList = (ArrayList<Object>) dataMap.get("planList");
                    log.debug("Plan list retrieved from response: {}", planList);

                    if (planList != null && !planList.isEmpty()) {
                        LinkedHashMap<String, Object> lastPlanMap = (LinkedHashMap<String, Object>) planList.get(planList.size() - 1);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
                        LocalDateTime localStartDateTime = LocalDateTime.parse(lastPlanMap.get("startDate").toString(), formatter);
                        long startDate = localStartDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        LocalDateTime localEndDateTime = LocalDateTime.parse(lastPlanMap.get("endDate").toString(), formatter);
                        long endtDate = localEndDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        log.debug("Start date: {}, End date: {}", startDate, endtDate);
                        response.setStartTime(String.valueOf(startDate));
                        response.setParameter1("");
                        response.setParameter2("");
                        response.setEndTime(String.valueOf(endtDate));
                        response.setSubscriberIdentity(String.valueOf(dataMap.get("username")));
                        response.setAddOnId(lastPlanMap.get("planId").toString());
                        response.setAddOnName((String) lastPlanMap.get("planName"));
                        response.setAddOnStatus((String) lastPlanMap.get("custPlanStatus"));
                        response.setAddOnSubscriptionId(dataMap.get("custPackagId").toString());
                        response.setUsageResetTime(String.valueOf(endtDate));
                        log.debug("Add-on details set: {}", response);
                        genericDataDTO.setData(response);

                    }
                }
            }

        } catch (Exception e) {
            log.error("Exception occurred while handling subscribe add-on request for MVNO ID: {}", SoapConstants.MVNOID, e);
            responseMessage = SoapConstants.FAILURE;
            responseCode = SoapConstants.INTERNAL_ERROR;
        }
        genericDataDTO.setResponseCode(responseCode);
        genericDataDTO.setResponseMessage(responseMessage);
        genericResponse.setData(genericDataDTO);
        return genericResponse;
    }
}
