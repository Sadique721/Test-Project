package com.savbill.integrationsystem.RestApiService.GetSubscriberSession;

import com.savbill.integrationsystem.SOAPService.GetUserUsageSummary.GetUserSessionresponseDto;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.getsubscribersession.GetSubscriberSession;
import com.savbill.integrationsystem.generated.wsgetsessionsbyip.WsGetUserSessionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GetSubscriberSessionRestService {

    @Autowired
    RadiusClientService radiusClientService;

    public GenericDataDTO getSubscriberSession(GetSubscriberSession request) {
        GetUserSessionresponseDto dataMessage = null;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String responseMessage = SoapConstants.FAILURE;
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();

        try {
            Long mvnoId = SoapConstants.MVNOID;
            String ipAddress = request.getString1().trim();
            if (ipAddress == null || StringUtils.isEmpty(ipAddress)) {
                log.warn("Input IP Address is empty or null");
                genericDataDTO.setResponseMessage("Input IP Address is Empty or Null");
                genericDataDTO.setResponseCode(401);
                return genericDataDTO;
            }
            if (!isValidIPAddress(ipAddress)) {
                log.warn("Input IP Address format is invalid: {}", ipAddress);
                genericDataDTO.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID);
                genericDataDTO.setResponseCode(401);
                return genericDataDTO;
            }

            if (!ipAddress.isEmpty()) {
                log.info("Valid IP address detected, calling radius service to fetch user session for IP: {}", ipAddress);
                genericDataDTO = radiusClientService.getUserSessionsTimeZ(ipAddress, mvnoId);
                dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()), GetUserSessionresponseDto.class);
            }

            if (genericDataDTO.getData() == null) {
                log.warn("No session found for IP address: {}", ipAddress);
                genericDataDTO.setResponseMessage("Input IP Address not found in Session Table");
                genericDataDTO.setResponseCode(401);
                return genericDataDTO;
            }
            log.info("Successfully retrieved session data for IP: {}", ipAddress);
            genericDataDTO.setResponseMessage(SoapConstants.SUCCESS);
            genericDataDTO.setResponseCode(SoapConstants.SUCCESS_CODE);
            genericDataDTO.setData(dataMessage);
            return genericDataDTO;
        } catch (Exception e) {
            log.error("Error occurred while fetching subscriber session for IP: {}. Error: {}", request.getString1(), e.getMessage());
            responseMessage = SoapConstants.FAILURE;
            responseCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        genericDataDTO.setResponseCode(responseCode);
        genericDataDTO.setResponseMessage(responseMessage);
        return genericDataDTO;
    }

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        boolean valid = ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
        if (!valid) {
            log.warn("Invalid IP address format: {}", ip);
        }
        return valid;
    }
}
