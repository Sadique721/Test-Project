package com.savbill.integrationsystem.RestApiService.getBalance;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.getBalance.WsGetBalanceRequestDTO;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class GetBalanceControllor {
    @Autowired
    public RadiusClientService radiusClientService;

    @PostMapping("/getBalanceList")
    public GenericDataDTO getWsGetBalanceResponse(@RequestBody GetBalaceDTO request) {
        log.info("Entering getWsGetBalanceResponse method with username: {}", request.getUsername());
        GenericDataDTO genericResponse = new GenericDataDTO();
        String username = request.getUsername().trim();
        try {
            username = username.toLowerCase().trim();
            log.debug("Processing balance request for username: {}", username);

            GenericDataDTO genericDataDTO = radiusClientService.getCustQoutaDetails(username, SoapConstants.MVNOID);
            log.debug("Received response from RadiusClientService for username: {}", username);

            List<WsGetBalanceRequestDTO> arrayList = new ArrayList<>();
            List<WsGetBalanceRequestDTO> dataMessageList = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(
                            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()),
                            new TypeReference<List<WsGetBalanceRequestDTO>>() {
                            });

            for (WsGetBalanceRequestDTO dataMessage : dataMessageList) {
                WsGetBalanceRequestDTO dto = new WsGetBalanceRequestDTO();

                dto.setQuotaProfileName(dataMessage.getQuotaProfileName());
                dto.setBalance(dataMessage.getBalance());
                dto.setTime(dataMessage.getTime());
                dto.setPackageId(dataMessage.getPackageId());
                dto.setPackageName(dataMessage.getPackageName());
                dto.setPackageType(dataMessage.getPackageType());
                dto.setUsageResetTime(dataMessage.getUsageResetTime());
                dto.setServiceName(dataMessage.getServiceName());
                dto.setServiceId(dataMessage.getServiceId());
                dto.setHsqLimit(dataMessage.getHsqLimit());
                dto.setDownloadOctet(dataMessage.getDownloadOctet());
                dto.setQuotaProfileId(dataMessage.getQuotaProfileId());
                dto.setUploadOctet(dataMessage.getUploadOctet());
                dto.setTotalOctet(dataMessage.getTotalOctet());
                dto.setCurrentUsage(dataMessage.getCurrentUsage());
                dto.setSubscriberId(dataMessage.getSubscriberId());

                if (!dataMessage.getPackageType().isEmpty() && (dataMessage.getPackageType().equalsIgnoreCase("SPARETOPUP")
                        || dataMessage.getPackageType().equalsIgnoreCase("ADDON"))) {
                    dto.setEndTime(dataMessage.getEndTime());
                    dto.setAddOnStatus(dataMessage.getAddOnStatus());
                    dto.setAddonSubscriptionId(dataMessage.getAddonSubscriptionId());
                    dto.setStartTime(dataMessage.getStartTime());
                }
                arrayList.add(dto);
            }

            genericResponse.setData(arrayList);
            genericResponse.setResponseCode(SoapConstants.SUCCESS_CODE);
            genericResponse.setResponseMessage(SoapConstants.SUCCESS);
            log.info("Successfully processed balance request for username: {}", username);

            return genericResponse;
        } catch (Exception e) {
            log.error("Error occurred while processing balance request for username: {}", username, e.getMessage());
            genericResponse.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericResponse.setResponseMessage(SoapConstants.FAILURE);
        }
        return genericResponse;
    }
}