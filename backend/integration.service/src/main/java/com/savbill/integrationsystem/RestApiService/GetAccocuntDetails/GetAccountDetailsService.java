package com.savbill.integrationsystem.RestApiService.GetAccocuntDetails;

import com.savbill.integrationsystem.SOAPService.GetAccocuntDetails.GetAccountDetailsSoapResponseDto;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.getaccountdetails.WsGetAccountDetails;
import com.savbill.integrationsystem.generated.getaccountdetails.WsGetAccountDetailsResponse;
import com.savbill.integrationsystem.utility.CommonUtilityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GetAccountDetailsService {
    @Autowired
    public RadiusClientService radiusClientService;

    @Autowired
    public CommonUtilityService commonUtilityService;

    public List<WsGetAccountDetailsResponse> getWsAddAccountDetails(WsGetAccountDetails request) {
        List<WsGetAccountDetailsResponse> accountDetailsResponseList = new ArrayList<>();
        String userName = request.getUserName().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();

        log.info("Processing request for username: {}, requestId: {}", userName, requestId);

        try {
            if (userName == null || userName.isEmpty()) {
                log.warn("Username is null or empty for requestId: {}", requestId);
                WsGetAccountDetailsResponse response = new WsGetAccountDetailsResponse();
                WsGetAccountDetailsResponse.GetAccountDetails responseDetails = new WsGetAccountDetailsResponse.GetAccountDetails();
                responseDetails.setRequestId(requestId);
                responseDetails.setResponeCode(503);
                responseDetails.setResponseMessage("Username is not available in SPR Table via Product API[findByUserIdentity]");
                response.setGetAccountDetails(responseDetails);
                accountDetailsResponseList.add(response);
            } else {
                Long mvnoId = SoapConstants.MVNOID;
                log.debug("call..RadiusClient to Fetch account details for username: {}, mvnoId: {}", userName, mvnoId);
                GenericDataDTO genericDataDTO = radiusClientService.GetAccountDetailsApi(userName, mvnoId);
                List<GetAccountDetailsSoapResponseDto> dataList =
                        new ObjectMapper().readValue(
                                new ObjectMapper()
                                        .writerWithDefaultPrettyPrinter()
                                        .writeValueAsString(genericDataDTO.getDataList()),
                                new TypeReference<List<GetAccountDetailsSoapResponseDto>>() {
                                }
                        );

                log.debug("Retrieved {} account details for username: {}", dataList.size(), userName);
                if (dataList != null && !dataList.isEmpty()) {
                    for (GetAccountDetailsSoapResponseDto dataMessage : dataList) {
                        WsGetAccountDetailsResponse getAccountDetailsResponse = new WsGetAccountDetailsResponse();
                        WsGetAccountDetailsResponse.GetAccountDetails response = new WsGetAccountDetailsResponse.GetAccountDetails();

                        List<WsGetAccountDetailsResponse.GetAccountDetails.Item> items = new ArrayList<>();

                        // Add items to the list
                        WsGetAccountDetailsResponse.GetAccountDetails.Item idItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        idItem.setKey("ID");
                        idItem.setValue(dataMessage.getId().toString());
                        items.add(idItem);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item policyItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        policyItem.setKey(SoapConstants.CONCURRENTLOGINPOLICY);
                        policyItem.setValue(dataMessage.getMaxconcurrentsession() != null ? dataMessage.getMaxconcurrentsession().toString() : "");
                        items.add(policyItem);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item additionalPolicy = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        additionalPolicy.setKey(SoapConstants.ADDITIONALPOLICY);
                        additionalPolicy.setValue(dataMessage.getBillday() != null ? formatNumericResponse(dataMessage.getBillday().toString()) : "");
                        items.add(additionalPolicy);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item status = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        status.setKey(SoapConstants.CUSTOMERSTATUS);
                        String currentStatus = dataMessage.getStatus();
                        if (currentStatus.equalsIgnoreCase("active")) {
                            currentStatus = "Y";
                        } else if (currentStatus.equalsIgnoreCase("inactive")) {
                            currentStatus = "N";
                        } else if (currentStatus.equalsIgnoreCase("suspend")) {
                            currentStatus = "SUSPEND";
                        }
                        status.setValue(currentStatus);
                        items.add(status);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item param1Item = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        param1Item.setKey("PARAM1");
                        param1Item.setValue(dataMessage.getFramedIp() != null ? dataMessage.getFramedIp() : "");
                        items.add(param1Item);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item geoLocationItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        geoLocationItem.setKey("GEOLOCATION");
                        geoLocationItem.setValue(dataMessage.getVlan_id() != null ? dataMessage.getVlan_id() : "");
                        items.add(geoLocationItem);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item param2Item = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        param2Item.setKey("PARAM2");
                        param2Item.setValue(dataMessage.getFramedIPNetmask() != null ? dataMessage.getFramedIPNetmask() : "");
                        items.add(param2Item);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item param3Item = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        param3Item.setKey("PARAM3");
                        param3Item.setValue(dataMessage.getFramedroute() != null ? dataMessage.getFramedroute() : "");
                        items.add(param3Item);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item param4Item = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        param4Item.setKey("PARAM4");
                        param4Item.setValue(dataMessage.getNasPortId() != null && !dataMessage.getNasPortId().isEmpty() ? "0:92=\"[" + dataMessage.getNasPortId() + "]\"" : "");
                        items.add(param4Item);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item param6Item = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        param6Item.setKey("PARAM6");
                        param6Item.setValue(dataMessage.getGatewayIP() != null ? dataMessage.getGatewayIP() : "");
                        items.add(param6Item);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item groupNameItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        groupNameItem.setKey("GROUPNAME");
                        groupNameItem.setValue(dataMessage.getFramedIpv6Address() != null ? dataMessage.getFramedIpv6Address() : "");
                        items.add(groupNameItem);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item callingStationIdItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        callingStationIdItem.setKey("CALLINGSTATIONID");
                        callingStationIdItem.setValue(dataMessage.getCallingStationId() != null ? dataMessage.getCallingStationId() : "");
                        items.add(callingStationIdItem);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item replyItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        replyItem.setKey("CUSTOMERREPLYITEM");
                        replyItem.setValue(dataMessage.getDelegatedprefix() != null && !dataMessage.getDelegatedprefix().isEmpty() ? "0:123=" + dataMessage.getDelegatedprefix() : "");
                        items.add(replyItem);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item macValidationItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        macValidationItem.setKey("MACVALIDATION");
                        String macValidation = "";
                        if (dataMessage.isMacValidation()) {
                            macValidation = "Y";
                        } else {
                            macValidation = "N";
                        }
                        macValidationItem.setValue(macValidation);
                        items.add(macValidationItem);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item cuiItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        cuiItem.setKey("CUI");
                        cuiItem.setValue(dataMessage.getAcctno() != null ? dataMessage.getAcctno() : "");
                        items.add(cuiItem);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item msisdnItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        msisdnItem.setKey("MSISDN");
                        msisdnItem.setValue(dataMessage.getMobile() != null ? dataMessage.getMobile() : "");
                        items.add(msisdnItem);

                        WsGetAccountDetailsResponse.GetAccountDetails.Item emailItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                        emailItem.setKey("CUSTOMERALTEMAILID");
                        emailItem.setValue(dataMessage.getEmail() != null ? dataMessage.getEmail() : "");
                        items.add(emailItem);

                        // Set response details
                        response.setRequestId(requestId);
                        response.setResponeCode(200);
                        response.setResponseMessage(SoapConstants.SUCCESS);
                        response.getItem().addAll(items);
                        response.setServiceId(dataMessage.getPlanname());
                        response.setUserName(userName);
                        response.setPassword(dataMessage.getPassword());

                        // Add the populated response to the result list
                        getAccountDetailsResponse.setGetAccountDetails(response);
                        accountDetailsResponseList.add(getAccountDetailsResponse);
                    }
                } else {
                    log.warn("No account details found for username: {}", userName);
                    WsGetAccountDetailsResponse response = new WsGetAccountDetailsResponse();
                    WsGetAccountDetailsResponse.GetAccountDetails responseDetails = new WsGetAccountDetailsResponse.GetAccountDetails();
                    responseDetails.setRequestId(requestId);
                    responseDetails.setResponeCode(503);
                    responseDetails.setResponseMessage("Username is not available in SPR Table via Product API[findByUserIdentity]");
                    response.setGetAccountDetails(responseDetails);
                    accountDetailsResponseList.add(response);
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while processing request for username: {}, requestId: {}", userName, requestId, e);
            WsGetAccountDetailsResponse response = new WsGetAccountDetailsResponse();
            WsGetAccountDetailsResponse.GetAccountDetails responseDetails = new WsGetAccountDetailsResponse.GetAccountDetails();
            responseDetails.setRequestId(requestId);
            responseDetails.setResponeCode(HttpStatus.EXPECTATION_FAILED.value());
            responseDetails.setResponseMessage("Failure: " + e.getMessage());
            response.setGetAccountDetails(responseDetails);
            accountDetailsResponseList.add(response);
        }

        log.info("Returning {} account details for username: {}", accountDetailsResponseList.size(), userName);
        return accountDetailsResponseList;
    }

    public String formatNumericResponse(String response) {
        try {
            int number = Integer.parseInt(response.trim());
            if (number >= 0 && number <= 9) {
                return "0" + number;
            } else {
                return String.valueOf(number);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid numeric response: {}", response, e);
            throw new IllegalArgumentException("Invalid numeric response: " + response);
        }
    }
}