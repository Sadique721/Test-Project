package com.savbill.integrationsystem.SOAPService.GetUserUsageSummary;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.wsGetBalance.GetBalanceDto;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.getuserusagesummary.WsGetUserUsageSummary;
import com.savbill.integrationsystem.generated.getuserusagesummary.WsGetUserUsageSummaryResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GetUserUsageSummaryService {

    @Autowired
    public RadiusClientService radiusClientService;

    private static final long KB_TO_BYTES = 1024L;
    private static final long MB_TO_BYTES = KB_TO_BYTES * 1024L;
    private static final long GB_TO_BYTES = MB_TO_BYTES * 1024L;

    public List<WsGetUserUsageSummaryResponse> getUserUsageSummary(WsGetUserUsageSummary request) {
        List<WsGetUserUsageSummaryResponse> wsGetUserUsageSummaryResponseList = new ArrayList<>();
        String userName = request.getSubscriberId().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        long startTime = System.currentTimeMillis();
        log.info("Starting method: getUserUsageSummary At:{}", new Date(startTime));
        if (userName == null || userName.isEmpty()) {
            WsGetUserUsageSummaryResponse wsGetUserUsageSummaryResponse = new WsGetUserUsageSummaryResponse();
            WsGetUserUsageSummaryResponse.GetUserUsageSummary response = new WsGetUserUsageSummaryResponse.GetUserUsageSummary();

            response.setRequestId(requestId);
            response.setResponseCode(SoapConstants.EMPTY);
            response.setResponseMessage(SoapConstants.Input_Username_is_Empty_or_null);
            wsGetUserUsageSummaryResponse.setGetUserUsageSummary(response);
            wsGetUserUsageSummaryResponseList.add(wsGetUserUsageSummaryResponse);
            log.info("Input userName is null or empty");
            return wsGetUserUsageSummaryResponseList;
        }

        try {
            String subscriberId = userName;
            Long mvnoId = SoapConstants.MVNOID;
            log.debug("Call Radius Client For Fetch usage summary for subscriber: {}", subscriberId);
            GenericDataDTO genericDataDTO = radiusClientService.GetUserUsageSummery(subscriberId, mvnoId);
            log.debug("Integration Received Response In:{}Ms usage summary for subscriber: {},With Response:{},DataSize:{}", System.currentTimeMillis() - startTime, subscriberId, genericDataDTO.getResponseMessage(),genericDataDTO.getDataList().size());


            if (genericDataDTO.getResponseCode() == 503) {
                WsGetUserUsageSummaryResponse wsGetUserUsageSummaryResponse = new WsGetUserUsageSummaryResponse();
                WsGetUserUsageSummaryResponse.GetUserUsageSummary response = new WsGetUserUsageSummaryResponse.GetUserUsageSummary();

                response.setResponseCode(SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE);
                response.setRequestId(requestId);
                response.setResponseMessage(genericDataDTO.getResponseMessage());
                wsGetUserUsageSummaryResponse.setGetUserUsageSummary(response);
                wsGetUserUsageSummaryResponseList.add(wsGetUserUsageSummaryResponse);
                log.warn("User not available in the customer table: {}", subscriberId);
                return wsGetUserUsageSummaryResponseList;
            }

            log.info("Received user usage data successfully for subscriber: {}", subscriberId);
            List<GetBalanceDto> dataMessageList = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(
                            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getDataList()),
                            new TypeReference<List<GetBalanceDto>>() {
                            });

            for (GetBalanceDto dataMessage : dataMessageList) {
                WsGetUserUsageSummaryResponse wsGetUserUsageSummaryResponse = new WsGetUserUsageSummaryResponse();
                WsGetUserUsageSummaryResponse.GetUserUsageSummary response = new WsGetUserUsageSummaryResponse.GetUserUsageSummary();

                long byteLimit = calCulateBytes(dataMessage.getTotalQuota(), dataMessage.getQuotaUnit());
                long byteRemaining = calCulateBytes(dataMessage.getTotalQuota() - (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume()), dataMessage.getQuotaUnit());
                long byteUsed = calCulateBytes(dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume(), dataMessage.getQuotaUnit());
                long defaultValue = 0L;

                response.setRequestId(requestId);
                response.setResponseCode(SoapConstants.SUCCESS_CODE);
                response.setResponseMessage(SoapConstants.SUCCESS);
                response.setAggregateBytesLimitLong(byteLimit);
                response.setAggregateBytesRemainingLong(byteRemaining);
                response.setAggregateBytesUsedLong(byteUsed);

                response.setInBytesLimitLong(defaultValue);
                response.setInBytesRemainingLong(defaultValue);
                response.setInBytesUsedLong(Long.valueOf(dataMessage.getUploadQuota()));

                response.setOutBytesLimitLong(defaultValue);
                response.setOutBytesRemainingLong(defaultValue);
                response.setOutBytesUsedLong(Long.valueOf(dataMessage.getDownloadQuota()));

                response.setPackageCode(dataMessage.getPlanName());
                response.setPackageType(dataMessage.getUsageQuotaType());

                response.setQodBytesLimitLong(defaultValue);
                response.setQodBytesRemainingLong(defaultValue);
                response.setQodBytesUsedLong(defaultValue);

                wsGetUserUsageSummaryResponse.setGetUserUsageSummary(response);
                wsGetUserUsageSummaryResponseList.add(wsGetUserUsageSummaryResponse);
                log.debug("Processed data for package: {}, used bytes: {}, remaining bytes: {}",
                        dataMessage.getPlanName(), byteUsed, byteRemaining);
            }

        } catch (Exception e) {
            WsGetUserUsageSummaryResponse wsGetUserUsageSummaryResponse = new WsGetUserUsageSummaryResponse();
            WsGetUserUsageSummaryResponse.GetUserUsageSummary response = new WsGetUserUsageSummaryResponse.GetUserUsageSummary();

            response.setResponseCode(SoapConstants.INTERNAL_ERROR);
            response.setRequestId(requestId);
            response.setResponseMessage("An error occurred while processing the request: " + e.getMessage());
            wsGetUserUsageSummaryResponse.setGetUserUsageSummary(response);
            wsGetUserUsageSummaryResponseList.add(wsGetUserUsageSummaryResponse);
            log.error("Error occurred while fetching usage summary for subscriber: {}", userName, e);
        }

        return wsGetUserUsageSummaryResponseList;
    }

    public long calCulateBytes(Double quota, String quotaUnit) {
        switch (quotaUnit.toUpperCase()) {
            case "KB":
                return (long) (quota * 1024);
            case "MB":
                return (long) (quota * MB_TO_BYTES);
            case "GB":
                return (long) (quota * GB_TO_BYTES);
            default:
                log.warn("Unknown quota unit: {}. Returning 0 bytes.", quotaUnit);
                return 0l;
        }
    }
}
