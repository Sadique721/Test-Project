package com.savbill.integrationsystem.RestApiService.wsGetBalance;

import com.savbill.integrationsystem.SOAPService.wsGetBalance.GetBalanceDto;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.wsGetBalance.GetBalanceRadiusDTO;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wsgetbalance.WsBalanceEnquiryErrorResponse;
import com.savbill.integrationsystem.generated.wsgetbalance.WsBalanceEnquiryResponse;
import com.savbill.integrationsystem.generated.wsgetbalance.WsGetBalance;
import com.savbill.integrationsystem.generated.wsgetbalance.WsGetBalanceResponse;
import com.savbill.integrationsystem.utility.CommonUtilityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class WsGetBalanceServiceRestController {

    @Autowired
    public RadiusClientService radiusClientService;

    @Autowired
    public CommonUtilityService commonUtilityService;

    @PostMapping("/getBalance")
    public List<WsBalanceEnquiryResponse> getWsGetBalanceResponse(@RequestBody WsGetBalance request) {
        WsGetBalanceResponse res = new WsGetBalanceResponse();
        List<WsBalanceEnquiryResponse> successResponseList = new ArrayList<>();
        WsBalanceEnquiryResponse.GetBalance getBalancee = new WsBalanceEnquiryResponse.GetBalance();
        String userName = request.getSubscriberId().trim();
        String packageName = request.getPackageName().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        getBalancee.setRequestId(requestId);

        log.info("Received request for getBalance with subscriberId: {}, packageName: {}, requestId: {}", userName, packageName, requestId);
        try {
            if (userName == null || userName.isEmpty()) {
                log.warn("SubscriberId is null or empty.");
                WsBalanceEnquiryResponse successResponse = new WsBalanceEnquiryResponse();
                successResponse.setGetBalance(new WsBalanceEnquiryResponse.GetBalance());
                WsBalanceEnquiryResponse.GetBalance getBalance = successResponse.getGetBalance();
                getBalance = successResponse.getGetBalance();
                getBalance.setResponseCode(SoapConstants.EMPTY);
                getBalance.setResponseMessage(SoapConstants.INPUT_MISSING);
                getBalance.setRequestId(requestId);
                successResponseList.add(successResponse);
            } else {
                Long mvnoId = SoapConstants.MVNOID;
                GetBalanceRadiusDTO getBalanceRadiusDTO = new GetBalanceRadiusDTO();
                if (Objects.nonNull(packageName) && !packageName.isEmpty()) {
                    getBalanceRadiusDTO.setPlanName(packageName);
                }
                if (Objects.nonNull(userName) && !userName.isEmpty()) {
                    getBalanceRadiusDTO.setSubscriberId(userName);
                }
                if (request.getPackageId() != null && Objects.nonNull(request.getPackageId())) {
                    getBalanceRadiusDTO.setPlanId(request.getPackageId().toString());
                }
                getBalanceRadiusDTO.setMvnoId(SoapConstants.MVNOID);

                log.debug("Calling RadiusClientService GetBalanceApiList with parameters: {}", getBalanceRadiusDTO);
                GenericDataDTO genericDataDTOList = radiusClientService.GetBalanceApiList(getBalanceRadiusDTO);
                List<GetBalanceDto> dataList =
                        new ObjectMapper().readValue(
                                new ObjectMapper()
                                        .writerWithDefaultPrettyPrinter()
                                        .writeValueAsString(genericDataDTOList.getDataList()),
                                new TypeReference<List<GetBalanceDto>>() {
                                }
                        );

                if (dataList != null && !dataList.isEmpty()) {
                    log.info("Successfully retrieved balance data for subscriberId: {}", userName);
                    for (GetBalanceDto dataMessage : dataList) {
                        WsBalanceEnquiryResponse successResponse = new WsBalanceEnquiryResponse();
                        successResponse.setGetBalance(new WsBalanceEnquiryResponse.GetBalance());
                        WsBalanceEnquiryResponse.GetBalance getBalance = successResponse.getGetBalance();

                        getBalance.setResponseCode(SoapConstants.SUCCESS_CODE);
                        getBalance.setResponseMessage(SoapConstants.SUCCESS);
                        getBalance.setRequestId(request.getRequestId() != null && !request.getRequestId().trim().isEmpty() ? request.getRequestId() : "?");
                        getBalance.setParameter1("");
                        getBalance.setParameter2("");

                        WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo subscriptionInfo = new WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo();
                        subscriptionInfo.setPackageId(dataMessage.getPlanId().toString());
                        subscriptionInfo.setPackageName(dataMessage.getPlanName());
                        String packageType = "BASE";
                        if (dataMessage.getPlanType().equalsIgnoreCase("Bandwidthbooster")) {
                            packageType = "ADDON";
                        } else if (dataMessage.getPlanType().equalsIgnoreCase("Volume Booster")) {
                            packageType = "SPARETOPUP";
                        }
                        subscriptionInfo.setPackageType(packageType);
                        subscriptionInfo.setCarryForword(0);

                        WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances quotaProfileBalances = new WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances();

                        quotaProfileBalances.setQuotaProfileId(dataMessage.getPlanId().toString());
                        quotaProfileBalances.setQuotaProfileName(dataMessage.getUsageQuotaType() + "_QP");

                        WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance allServiceBalance = new WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance();
                        allServiceBalance.setAggregationKey("Billing Cycle");

                        WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.Balance balance = new WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.Balance();
                        balance.setDownloadOctets("-1");
                        balance.setTime("-1");
                        balance.setTotalOctetsLong(commonUtilityService.calCulateBytes(dataMessage.getTotalQuota() - (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume()), dataMessage.getQuotaUnit()));
                        balance.setUploadOctets("-1");
                        allServiceBalance.setBalance(balance);

                        WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.CurretUsage currentUsage = new WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.CurretUsage();
                        currentUsage.setDownloadOctets(dataMessage.getDownloadQuota());
                        currentUsage.setTime(dataMessage.getCurrentSessionUsageTime().toString());
                        currentUsage.setTotalOctetsLong(commonUtilityService.calCulateBytes(dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume(), dataMessage.getQuotaUnit()));
                        currentUsage.setUploadOctets(dataMessage.getUploadQuota());
                        allServiceBalance.setCurretUsage(currentUsage);

                        WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.HSQLimit hsqLimit = new WsBalanceEnquiryResponse.GetBalance.SubscriptionInfo.QuotaProfileBalances.AllServiceBalance.HSQLimit();
                        hsqLimit.setDownloadOctets("-1");
                        hsqLimit.setTime("-1");
                        hsqLimit.setTotalOctetsLong(commonUtilityService.calCulateBytesLong(dataMessage.getTotalQuotaLong(), dataMessage.getQuotaUnit()));
                        hsqLimit.setUploadOctets("-1");
                        allServiceBalance.setHSQLimit(hsqLimit);

                        allServiceBalance.setServiceId(dataMessage.getPlanName());
                        allServiceBalance.setServiceName("All-Service");

                        quotaProfileBalances.setAllServiceBalance(allServiceBalance);
                        subscriptionInfo.setQuotaProfileBalances(quotaProfileBalances);
                        getBalance.setSubscriptionInfo(subscriptionInfo);
                        res.setWsBalanceEnquiryResponse(successResponse);
                        successResponseList.add(successResponse);
                    }

                } else {
                    log.warn("This : {} Subscriber Not Found ", userName);
                    WsBalanceEnquiryResponse successResponse = new WsBalanceEnquiryResponse();
                    successResponse.setGetBalance(new WsBalanceEnquiryResponse.GetBalance());
                    WsBalanceEnquiryResponse.GetBalance getBalance = successResponse.getGetBalance();
                    getBalance = successResponse.getGetBalance();
                    getBalance.setResponseCode(404);
                    getBalance.setResponseMessage("NOT FOUND. Reason: Subscriber not found with subscriber " + userName);
                    getBalance.setRequestId(request.getRequestId() != null && !request.getRequestId().trim().isEmpty() ? request.getRequestId() : "?");
                    successResponseList.add(successResponse);
                }
                return successResponseList;
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for subscriberId: {} message: {}", userName, e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            log.error("Null pointer exception occurred for subscriberId: {} message: {}", userName, e.getMessage());
            e.printStackTrace();
        }
        return successResponseList;
    }
}