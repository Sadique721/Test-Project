package com.savbill.integrationsystem.SOAPService.GetAccocuntDetails;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.getaccountdetails.GetAccountDetails;
import com.savbill.integrationsystem.generated.getaccountdetails.GetAccountDetailsResponse;
import com.savbill.integrationsystem.generated.getaccountdetails.WsGetAccountDetails;
import com.savbill.integrationsystem.generated.getaccountdetails.WsGetAccountDetailsResponse;
import com.savbill.integrationsystem.generated.newgetaccountdetails.SubscriberMapEntry;
import com.savbill.integrationsystem.utility.CommonUtilityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Endpoint
public class GetAccountDetailsEndpoint {

    @Autowired
    public RadiusClientService radiusClientService;

    @Autowired
    public CommonUtilityService commonUtilityService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsGetAccountDetails")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newgetaccountdetails.WsGetAccountDetailsResponse getWsAddAccountDetailsResponse(@RequestPayload WsGetAccountDetails request, MessageContext messageContext) throws SOAPException, IOException {
        List<WsGetAccountDetailsResponse> response = null;
        try {
            response = getWsAddAccountDetails(request);
            return setProperties(response);
//            return generateGetAccountDetailsSOAP11SuccessResponse(response, messageContext);
        } catch (Exception e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
//            return generateGetAccountDetailsSOAP11ExceptionResponse(response.get(0), messageContext);
            return setProperties(response);
        }
    }


    public List<WsGetAccountDetailsResponse> getWsAddAccountDetails(WsGetAccountDetails request) {
        long startTime = System.currentTimeMillis();
        log.info("Starting method getWsAddAccountDetails for username: {} AT:{}", request.getUserName(), new Date(startTime));
        List<WsGetAccountDetailsResponse> accountDetailsResponseList = new ArrayList<>();
        String userName = request.getUserName();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        try {
            if (userName == null || userName.trim().isEmpty()) {
                WsGetAccountDetailsResponse response = new WsGetAccountDetailsResponse();
                WsGetAccountDetailsResponse.GetAccountDetails responseDetails = new WsGetAccountDetailsResponse.GetAccountDetails();
                responseDetails.setRequestId(requestId);
                responseDetails.setResponeCode(401);
                responseDetails.setResponseMessage("Input user name is Empty or Null.");
                response.setGetAccountDetails(responseDetails);
                log.warn("Input userName is Null or empty");
                accountDetailsResponseList.add(response);
            } else {
                Long mvnoId = SoapConstants.MVNOID;
                // Assuming GetAccountDetailsApi returns a List of GetAccountDetailsSoapResponseDto
                log.debug("Calling RadiusClient for Account Details: {}", userName);
                GenericDataDTO genericDataDTO = radiusClientService.GetAccountDetailsApi(userName, mvnoId);
                log.debug("Integration Received Account Details From radius In: {}ms", System.currentTimeMillis() - startTime);
                List<GetAccountDetailsSoapResponseDto> dataList =
                        new ObjectMapper().readValue(
                                new ObjectMapper()
                                        .writerWithDefaultPrettyPrinter()
                                        .writeValueAsString(genericDataDTO.getDataList()),
                                new TypeReference<List<GetAccountDetailsSoapResponseDto>>() {
                                }
                        );
                // Check if dataList is not empty
                if (dataList != null && !dataList.isEmpty()) {
                    log.debug("Found {} account details records for username: {}", dataList.size(), userName);
                    // Iterate over the dataList
                    log.debug("Processing account details for username: {},", userName);
                    WsGetAccountDetailsResponse getAccountDetailsResponse = new WsGetAccountDetailsResponse();
                    WsGetAccountDetailsResponse.GetAccountDetails response = new WsGetAccountDetailsResponse.GetAccountDetails();

                    List<WsGetAccountDetailsResponse.GetAccountDetails.Item> items = new ArrayList<>();

                    // Add items to the list
                    WsGetAccountDetailsResponse.GetAccountDetails.Item idItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    idItem.setKey("ID");
                    idItem.setValue(dataList.get(0).getId().toString());
                    items.add(idItem);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item policyItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    policyItem.setKey(SoapConstants.CONCURRENTLOGINPOLICY);
                    policyItem.setValue(dataList.get(0).getMaxconcurrentsession() != null ? dataList.get(0).getMaxconcurrentsession().toString() : "");
                    items.add(policyItem);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item additionalPolicy = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    additionalPolicy.setKey(SoapConstants.ADDITIONALPOLICY);
                    additionalPolicy.setValue(dataList.get(0).getBillday() != null ? formatNumericResponse(dataList.get(0).getBillday().toString()) : "");
                    items.add(additionalPolicy);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item status = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    status.setKey(SoapConstants.CUSTOMERSTATUS);
                    String currentStatus = dataList.get(0).getStatus();
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
                    param1Item.setValue(dataList.get(0).getFramedIp() != null ? dataList.get(0).getFramedIp() : "");
                    items.add(param1Item);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item geoLocationItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    geoLocationItem.setKey("GEOLOCATION");
                    geoLocationItem.setValue(dataList.get(0).getVlan_id() != null ? dataList.get(0).getVlan_id() : "");
                    items.add(geoLocationItem);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item param2Item = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    param2Item.setKey("PARAM2");
                    param2Item.setValue(dataList.get(0).getFramedIPNetmask() != null ? dataList.get(0).getFramedIPNetmask() : "");
                    items.add(param2Item);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item param3Item = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    param3Item.setKey("PARAM3");
                    param3Item.setValue(dataList.get(0).getFramedroute() != null ? dataList.get(0).getFramedroute() : "");
                    items.add(param3Item);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item param4Item = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    param4Item.setKey("PARAM4");
                    param4Item.setValue(dataList.get(0).getNasPortId() != null && !dataList.get(0).getNasPortId().isEmpty() ? "0:92=\"[" + dataList.get(0).getNasPortId() + "]\"" : "");
                    items.add(param4Item);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item param6Item = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    param6Item.setKey("PARAM6");
                    param6Item.setValue(dataList.get(0).getGatewayIP() != null ? dataList.get(0).getGatewayIP() : "");
                    items.add(param6Item);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item groupNameItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    groupNameItem.setKey("GROUPNAME");
                    groupNameItem.setValue(dataList.get(0).getFramedIpv6Address() != null ? dataList.get(0).getFramedIpv6Address() : "");
                    items.add(groupNameItem);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item callingStationIdItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    callingStationIdItem.setKey("CALLINGSTATIONID");
                    callingStationIdItem.setValue(dataList.get(0).getCallingStationId() != null ? dataList.get(0).getCallingStationId() : "");
                    items.add(callingStationIdItem);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item replyItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    replyItem.setKey("CUSTOMERREPLYITEM");
                    replyItem.setValue(dataList.get(0).getDelegatedprefix() != null && !dataList.get(0).getDelegatedprefix().isEmpty() ? "0:123=" + dataList.get(0).getDelegatedprefix() : "");
                    items.add(replyItem);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item macValidationItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    macValidationItem.setKey("MACVALIDATION");
                    String macValidation = "";
                    if (dataList.get(0).isMacValidation()) {
                        macValidation = "Y";
                    } else {
                        macValidation = "N";
                    }
                    macValidationItem.setValue(macValidation);
                    items.add(macValidationItem);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item cuiItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    cuiItem.setKey("CUI");
                    cuiItem.setValue(dataList.get(0).getAcctno() != null ? dataList.get(0).getAcctno() : "");
                    items.add(cuiItem);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item msisdnItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    msisdnItem.setKey("MSISDN");
                    msisdnItem.setValue(dataList.get(0).getMobile() != null ? dataList.get(0).getMobile() : "");
                    items.add(msisdnItem);

                    WsGetAccountDetailsResponse.GetAccountDetails.Item emailItem = new WsGetAccountDetailsResponse.GetAccountDetails.Item();
                    emailItem.setKey("CUSTOMERALTEMAILID");
                    emailItem.setValue(dataList.get(0).getEmail() != null ? dataList.get(0).getEmail() : "");
                    items.add(emailItem);

                    // Set response details
                    response.setRequestId(requestId);
                    response.setResponeCode(200);
                    response.setResponseMessage(SoapConstants.SUCCESS);
                    response.getItem().addAll(items);
                    response.setServiceId(dataList.get(0).getPlanname());
                    response.setUserName(userName);
                    response.setPassword(dataList.get(0).getPassword());

                    // Add the populated response to the result list
                    getAccountDetailsResponse.setGetAccountDetails(response);
                    accountDetailsResponseList.add(getAccountDetailsResponse);
                    log.debug("Successfully processed :{} details for username: {}", dataList.size(), userName);
                } else {
                    // Handle the case when dataList is empty
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
            // Handle any exceptions and add default response
            log.error("Error processing account details for username: {}, Error: {}", userName, e.getMessage(), e);
            WsGetAccountDetailsResponse response = new WsGetAccountDetailsResponse();
            WsGetAccountDetailsResponse.GetAccountDetails responseDetails = new WsGetAccountDetailsResponse.GetAccountDetails();
            responseDetails.setRequestId(requestId);
            responseDetails.setResponeCode(503);
            responseDetails.setResponseMessage("Failure: " + e.getMessage());
            response.setGetAccountDetails(responseDetails);
            accountDetailsResponseList.add(response);
        }

        // Return the list of responses
        log.info("Method getWsAddAccountDetails completed IN:{}MS for username: {}",
                System.currentTimeMillis() - startTime, userName);
        return accountDetailsResponseList;
    }

//    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "GetAccountDetails")
//    @ResponsePayload
//    public com.savbill.integrationsystem.generated.getaccountdetails1.WsGetAccountDetailsResponse getWsAddAccountDetailsResponse(@RequestPayload GetAccountDetails request, MessageContext messageContext) throws SOAPException, IOException {
//        com.savbill.integrationsystem.generated.getaccountdetails1.WsGetAccountDetailsResponse resp;
//        List<GetAccountDetailsResponse> response = null;
//        try {
//            response = getAddAccountDetails(request);
//            resp = setProperties(response);
//            return resp;
//        } catch (Exception e) {
//            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";

    /// /            return generateGetAccountDetailsSOAP11ExceptionResponse1(response.get(0), messageContext);
//            resp = setProperties(response);
//            return resp;
//        }
//    }
    private com.savbill.integrationsystem.generated.newgetaccountdetails.WsGetAccountDetailsResponse setProperties(List<WsGetAccountDetailsResponse> response) {
        com.savbill.integrationsystem.generated.newgetaccountdetails.WsGetAccountDetailsResponse resp = new com.savbill.integrationsystem.generated.newgetaccountdetails.WsGetAccountDetailsResponse();
        com.savbill.integrationsystem.generated.newgetaccountdetails.GetAccountDetailsResponse accDetailsType = new com.savbill.integrationsystem.generated.newgetaccountdetails.GetAccountDetailsResponse();

        for (WsGetAccountDetailsResponse getAccountDetailsResponse : response) {

            accDetailsType.setRequestId(getAccountDetailsResponse.getGetAccountDetails().getRequestId());
            accDetailsType.setResponeCode(getAccountDetailsResponse.getGetAccountDetails().getResponeCode());
            accDetailsType.setResponseMessage(getAccountDetailsResponse.getGetAccountDetails().getResponseMessage());
            if (accDetailsType.getResponeCode() == 200) {
                String ID = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(0).getValue());
                accDetailsType.setPassword(getAccountDetailsResponse.getGetAccountDetails().getPassword());
                accDetailsType.setServiceId(getAccountDetailsResponse.getGetAccountDetails().getServiceId());
                accDetailsType.setUserName(getAccountDetailsResponse.getGetAccountDetails().getUserName());

                if (!ID.isEmpty() && ID != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("ID");
                    entry.setValue(ID);
                    accDetailsType.getItem().add(entry);
                }

                String CONCURRENTLOGINPOLICY = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(1).getValue());
                if (!CONCURRENTLOGINPOLICY.isEmpty() && CONCURRENTLOGINPOLICY != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("CONCURRENTLOGINPOLICY");
                    entry.setValue(CONCURRENTLOGINPOLICY);
                    accDetailsType.getItem().add(entry);
                }
                String ADDITIONALPOLICY = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(2).getValue());
                if (!ADDITIONALPOLICY.isEmpty() && ADDITIONALPOLICY != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("ADDITIONALPOLICY");
                    entry.setValue(ADDITIONALPOLICY);
                    accDetailsType.getItem().add(entry);
                }
                String CUSTOMERSTATUS = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(3).getValue());
                if (!CUSTOMERSTATUS.isEmpty() && CUSTOMERSTATUS != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("CUSTOMERSTATUS");
                    entry.setValue(CUSTOMERSTATUS);
                    accDetailsType.getItem().add(entry);
                }

                String param1 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(4).getValue());
                if (!param1.isEmpty() && param1 != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("PARAM1");
                    entry.setValue(param1);
                    accDetailsType.getItem().add(entry);
                }


                String GEOLOCATION = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(5).getValue());
                if (!GEOLOCATION.isEmpty() && GEOLOCATION != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("GEOLOCATION");
                    entry.setValue(GEOLOCATION);
                    accDetailsType.getItem().add(entry);
                }

                String PARAM2 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(6).getValue());
                if (!PARAM2.isEmpty() && PARAM2 != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("PARAM2");
                    entry.setValue(PARAM2);
                    accDetailsType.getItem().add(entry);
                }

                String PARAM3 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(7).getValue());
                if (!PARAM3.isEmpty() && PARAM3 != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("PARAM3");
                    entry.setValue(PARAM3);
                    accDetailsType.getItem().add(entry);
                }


                String PARAM4 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(8).getValue());
                if (!PARAM4.isEmpty() && PARAM4 != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("PARAM4");
                    entry.setValue(PARAM4);
                    accDetailsType.getItem().add(entry);
                }

                String PARAM6 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(9).getValue());
                if (!PARAM6.isEmpty() && PARAM6 != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("PARAM6");
                    entry.setValue(PARAM6);
                    accDetailsType.getItem().add(entry);
                }

                String GROUPNAME = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(10).getValue());
                if (!GROUPNAME.isEmpty() && GROUPNAME != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("GROUPNAME");
                    entry.setValue(GROUPNAME);
                    accDetailsType.getItem().add(entry);
                }

                String CALLINGSTATIONID = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(11).getValue());
                if (!CALLINGSTATIONID.isEmpty() && CALLINGSTATIONID != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("CALLINGSTATIONID");
                    entry.setValue(CALLINGSTATIONID);
                    accDetailsType.getItem().add(entry);
                }

                String CUSTOMERREPLYITEM = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(12).getValue());
                if (!CUSTOMERREPLYITEM.isEmpty() && CUSTOMERREPLYITEM != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("CUSTOMERREPLYITEM");
                    entry.setValue(CUSTOMERREPLYITEM);
                    accDetailsType.getItem().add(entry);
                }

                String MACVALIDATION = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(13).getValue());
                if (!MACVALIDATION.isEmpty() && MACVALIDATION != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("MACVALIDATION");
                    entry.setValue(MACVALIDATION);
                    accDetailsType.getItem().add(entry);
                }

                String CUI = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(14).getValue());
                if (!CUI.isEmpty() && CUI != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("CUI");
                    entry.setValue(CUI);
                    accDetailsType.getItem().add(entry);
                }

                String MSISDN = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(15).getValue());
                if (!MSISDN.isEmpty() && MSISDN != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("MSISDN");
                    entry.setValue(MSISDN);
                    accDetailsType.getItem().add(entry);
                }

                String CUSTOMERALTEMAILID = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(16).getValue());
                if (!CUSTOMERALTEMAILID.isEmpty() && CUSTOMERALTEMAILID != null) {
                    SubscriberMapEntry entry = new SubscriberMapEntry();
                    entry.setKey("CUSTOMERALTEMAILID");
                    entry.setValue(CUSTOMERALTEMAILID);
                    accDetailsType.getItem().add(entry);
                }
            }
        }
        resp.setGetAccountDetails(accDetailsType);
        return resp;
    }


    public List<GetAccountDetailsResponse> getAddAccountDetails(GetAccountDetails request) {
        List<GetAccountDetailsResponse> accountDetailsResponseList = new ArrayList<>();
        String userName = request.getUserName().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        long startTime = System.currentTimeMillis();

        try {
            if (userName == null || userName.isEmpty()) {
                GetAccountDetailsResponse response = new GetAccountDetailsResponse();
                GetAccountDetailsResponse.GetAccountDetails responseDetails = new GetAccountDetailsResponse.GetAccountDetails();
                responseDetails.setRequestId(requestId);
                responseDetails.setResponeCode(401);
                responseDetails.setResponseMessage("Input user name is Empty or Null.");
                response.setGetAccountDetails(responseDetails);
                accountDetailsResponseList.add(response);
            } else {
                Long mvnoId = SoapConstants.MVNOID;
                // Assuming GetAccountDetailsApi returns a List of GetAccountDetailsSoapResponseDto
                GenericDataDTO genericDataDTO = radiusClientService.GetAccountDetailsApi(userName, mvnoId);
                List<GetAccountDetailsSoapResponseDto> dataList =
                        new ObjectMapper().readValue(
                                new ObjectMapper()
                                        .writerWithDefaultPrettyPrinter()
                                        .writeValueAsString(genericDataDTO.getDataList()),
                                new TypeReference<List<GetAccountDetailsSoapResponseDto>>() {
                                }
                        );
                // Check if dataList is not empty
                if (dataList != null && !dataList.isEmpty()) {
                    // Iterate over the dataList
                    for (GetAccountDetailsSoapResponseDto dataMessage : dataList) {
                        GetAccountDetailsResponse getAccountDetailsResponse = new GetAccountDetailsResponse();
                        GetAccountDetailsResponse.GetAccountDetails response = new GetAccountDetailsResponse.GetAccountDetails();

                        List<GetAccountDetailsResponse.GetAccountDetails.Item> items = new ArrayList<>();

                        // Add items to the list
                        GetAccountDetailsResponse.GetAccountDetails.Item idItem = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        idItem.setKey("ID");
                        idItem.setValue(dataMessage.getId().toString());
                        items.add(idItem);

                        GetAccountDetailsResponse.GetAccountDetails.Item policyItem = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        policyItem.setKey(SoapConstants.CONCURRENTLOGINPOLICY);
                        policyItem.setValue(dataMessage.getMaxconcurrentsession() != null ? dataMessage.getMaxconcurrentsession().toString() : "");
                        items.add(policyItem);

                        GetAccountDetailsResponse.GetAccountDetails.Item additionalPolicy = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        additionalPolicy.setKey(SoapConstants.ADDITIONALPOLICY);
                        additionalPolicy.setValue(dataMessage.getBillday() != null ? formatNumericResponse(dataMessage.getBillday().toString()) : "");
                        items.add(additionalPolicy);

                        GetAccountDetailsResponse.GetAccountDetails.Item status = new GetAccountDetailsResponse.GetAccountDetails.Item();
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

                        GetAccountDetailsResponse.GetAccountDetails.Item param1Item = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        param1Item.setKey("PARAM1");
                        param1Item.setValue(dataMessage.getFramedIp() != null ? dataMessage.getFramedIp() : "");
                        items.add(param1Item);

                        GetAccountDetailsResponse.GetAccountDetails.Item geoLocationItem = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        geoLocationItem.setKey("GEOLOCATION");
                        geoLocationItem.setValue(dataMessage.getVlan_id() != null ? dataMessage.getVlan_id() : "");
                        items.add(geoLocationItem);

                        GetAccountDetailsResponse.GetAccountDetails.Item param2Item = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        param2Item.setKey("PARAM2");
                        param2Item.setValue(dataMessage.getFramedIPNetmask() != null ? dataMessage.getFramedIPNetmask() : "");
                        items.add(param2Item);

                        GetAccountDetailsResponse.GetAccountDetails.Item param3Item = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        param3Item.setKey("PARAM3");
                        param3Item.setValue(dataMessage.getFramedroute() != null ? dataMessage.getFramedroute() : "");
                        items.add(param3Item);

                        GetAccountDetailsResponse.GetAccountDetails.Item param4Item = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        param4Item.setKey("PARAM4");
                        param4Item.setValue(dataMessage.getNasPortId() != null && !dataMessage.getNasPortId().isEmpty() ? "0:92=\"[" + dataMessage.getNasPortId() + "]\"" : "");
                        items.add(param4Item);

                        GetAccountDetailsResponse.GetAccountDetails.Item param6Item = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        param6Item.setKey("PARAM6");
                        param6Item.setValue(dataMessage.getGatewayIP() != null ? dataMessage.getGatewayIP() : "");
                        items.add(param6Item);

                        GetAccountDetailsResponse.GetAccountDetails.Item groupNameItem = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        groupNameItem.setKey("GROUPNAME");
                        groupNameItem.setValue(dataMessage.getFramedIpv6Address() != null ? dataMessage.getFramedIpv6Address() : "");
                        items.add(groupNameItem);

                        GetAccountDetailsResponse.GetAccountDetails.Item callingStationIdItem = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        callingStationIdItem.setKey("CALLINGSTATIONID");
                        callingStationIdItem.setValue(dataMessage.getCallingStationId() != null ? dataMessage.getCallingStationId() : "");
                        items.add(callingStationIdItem);

                        GetAccountDetailsResponse.GetAccountDetails.Item replyItem = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        replyItem.setKey("CUSTOMERREPLYITEM");
                        replyItem.setValue(dataMessage.getDelegatedprefix() != null && !dataMessage.getDelegatedprefix().isEmpty() ? "0:123=" + dataMessage.getDelegatedprefix() : "");
                        items.add(replyItem);

                        GetAccountDetailsResponse.GetAccountDetails.Item macValidationItem = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        macValidationItem.setKey("MACVALIDATION");
                        String macValidation = "";
                        if (dataMessage.isMacValidation()) {
                            macValidation = "Y";
                        } else {
                            macValidation = "N";
                        }
                        macValidationItem.setValue(macValidation);
                        items.add(macValidationItem);

                        GetAccountDetailsResponse.GetAccountDetails.Item cuiItem = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        cuiItem.setKey("CUI");
                        cuiItem.setValue(dataMessage.getAcctno() != null ? dataMessage.getAcctno() : "");
                        items.add(cuiItem);

                        GetAccountDetailsResponse.GetAccountDetails.Item msisdnItem = new GetAccountDetailsResponse.GetAccountDetails.Item();
                        msisdnItem.setKey("MSISDN");
                        msisdnItem.setValue(dataMessage.getMobile() != null ? dataMessage.getMobile() : "");
                        items.add(msisdnItem);

                        GetAccountDetailsResponse.GetAccountDetails.Item emailItem = new GetAccountDetailsResponse.GetAccountDetails.Item();
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
                    // Handle the case when dataList is empty
                    GetAccountDetailsResponse response = new GetAccountDetailsResponse();
                    GetAccountDetailsResponse.GetAccountDetails responseDetails = new GetAccountDetailsResponse.GetAccountDetails();
                    responseDetails.setRequestId(requestId);
                    responseDetails.setResponeCode(503);
                    responseDetails.setResponseMessage("Username is not available in SPR Table via Product API[findByUserIdentity]");
                    response.setGetAccountDetails(responseDetails);
                    accountDetailsResponseList.add(response);
                }
            }
        } catch (Exception e) {
            // Handle any exceptions and add default response
            GetAccountDetailsResponse response = new GetAccountDetailsResponse();
            GetAccountDetailsResponse.GetAccountDetails responseDetails = new GetAccountDetailsResponse.GetAccountDetails();
            responseDetails.setRequestId(requestId);
            responseDetails.setResponeCode(HttpStatus.EXPECTATION_FAILED.value());
            responseDetails.setResponseMessage("Failure: " + e.getMessage());
            response.setGetAccountDetails(responseDetails);
            accountDetailsResponseList.add(response);
        }

        log.info("Method getWsAddAccountDetails completed in {}ms for username: {}, records found: {}",
                System.currentTimeMillis() - startTime, userName, accountDetailsResponseList.size());
        return accountDetailsResponseList;
    }

    public DOMSource generateGetAccountDetailsSOAP11SuccessResponse1(List<GetAccountDetailsResponse> response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Add the main response element with custom namespaces
        SOAPElement responseElement = body.addChildElement("wsGetAccountDetailsResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement getAccountDetails = responseElement.addChildElement("GetAccountDetails");
        // Add the item elements with keys and values
        String ID = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(0).getValue());
        if (!ID.isEmpty() && ID != null)
            addItem(getAccountDetails, "ID", ID);

        String CONCURRENTLOGINPOLICY = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(1).getValue());
        if (!CONCURRENTLOGINPOLICY.isEmpty() && CONCURRENTLOGINPOLICY != null)
            addItem(getAccountDetails, "CONCURRENTLOGINPOLICY", CONCURRENTLOGINPOLICY);

        String ADDITIONALPOLICY = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(2).getValue());
        if (!ADDITIONALPOLICY.isEmpty() && ADDITIONALPOLICY != null)
            addItem(getAccountDetails, "ADDITIONALPOLICY", ADDITIONALPOLICY);

        String CUSTOMERSTATUS = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(3).getValue());
        if (!CUSTOMERSTATUS.isEmpty() && CUSTOMERSTATUS != null)
            addItem(getAccountDetails, "CUSTOMERSTATUS", CUSTOMERSTATUS);

        String param1 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(4).getValue());
        if (!param1.isEmpty() && param1 != null)
            addItem(getAccountDetails, "PARAM1", param1);

        String GEOLOCATION = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(5).getValue());
        if (!GEOLOCATION.isEmpty() && GEOLOCATION != null)
            addItem(getAccountDetails, "GEOLOCATION", GEOLOCATION);

        String PARAM2 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(6).getValue());
        if (!PARAM2.isEmpty() && PARAM2 != null)
            addItem(getAccountDetails, "PARAM2", PARAM2);

        String PARAM3 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(7).getValue());
        if (!PARAM3.isEmpty() && PARAM3 != null)
            addItem(getAccountDetails, "PARAM3", PARAM3);

        String PARAM4 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(8).getValue());
        if (!PARAM4.isEmpty() && PARAM4 != null)
            addItem(getAccountDetails, "PARAM4", PARAM4);

        String PARAM6 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(9).getValue());
        if (!PARAM6.isEmpty() && PARAM6 != null)
            addItem(getAccountDetails, "PARAM6", PARAM6);

        String GROUPNAME = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(10).getValue());
        if (!GROUPNAME.isEmpty() && GROUPNAME != null)
            addItem(getAccountDetails, "GROUPNAME", GROUPNAME);

        String CALLINGSTATIONID = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(11).getValue());
        if (!CALLINGSTATIONID.isEmpty() && CALLINGSTATIONID != null)
            addItem(getAccountDetails, "CALLINGSTATIONID", CALLINGSTATIONID);

        String CUSTOMERREPLYITEM = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(12).getValue());
        if (!CUSTOMERREPLYITEM.isEmpty() && CUSTOMERREPLYITEM != null)
            addItem(getAccountDetails, "CUSTOMERREPLYITEM", CUSTOMERREPLYITEM);

        String MACVALIDATION = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(13).getValue());
        if (!MACVALIDATION.isEmpty() && MACVALIDATION != null)
            addItem(getAccountDetails, "MACVALIDATION", MACVALIDATION);

        String CUI = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(14).getValue());
        if (!CUI.isEmpty() && CUI != null)
            addItem(getAccountDetails, "CUI", CUI);

        String MSISDN = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(15).getValue());
        if (!MSISDN.isEmpty() && MSISDN != null)
            addItem(getAccountDetails, "MSISDN", MSISDN);

        String CUSTOMERALTEMAILID = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(16).getValue());
        if (!CUSTOMERALTEMAILID.isEmpty() && CUSTOMERALTEMAILID != null)
            addItem(getAccountDetails, "CUSTOMERALTEMAILID", CUSTOMERALTEMAILID);

        // Add the remaining fields
        getAccountDetails.addChildElement("password").addTextNode(response.get(0).getGetAccountDetails().getPassword());
        getAccountDetails.addChildElement("requestId").addTextNode(response.get(0).getGetAccountDetails().getRequestId());
        getAccountDetails.addChildElement("responeCode").addTextNode(String.valueOf(response.get(0).getGetAccountDetails().getResponeCode()));
        getAccountDetails.addChildElement("responseMessage").addTextNode(response.get(0).getGetAccountDetails().getResponseMessage());
        for (GetAccountDetailsResponse dto : response) {
            getAccountDetails.addChildElement("serviceId").addTextNode(dto.getGetAccountDetails().getServiceId());
        }
        getAccountDetails.addChildElement("userName").addTextNode(response.get(0).getGetAccountDetails().getUserName());

        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    public DOMSource generateGetAccountDetailsSOAP11ExceptionResponse1(GetAccountDetailsResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement responseElement = body.addChildElement("wsGetAccountDetailsResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");


        SOAPElement getAccountDetails = responseElement.addChildElement("GetAccountDetails");
        getAccountDetails.addChildElement("requestId").addTextNode(response.getGetAccountDetails().getRequestId() != null ? response.getGetAccountDetails().getRequestId() : "?");
        getAccountDetails.addChildElement("responeCode").addTextNode(String.valueOf(response.getGetAccountDetails().getResponeCode()));
        getAccountDetails.addChildElement("responseMessage").addTextNode(response.getGetAccountDetails().getResponseMessage());

        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    /*
    public DOMSource generateGetWsAccountDetailsSOAPResponse(WsGetAccountDetailsResponse response) throws SOAPException, ParserConfigurationException {
        // Create a SOAP Message factory and message
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Add namespace declarations
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("ns2", "http://api.act.com/");

        SOAPBody body = envelope.getBody();

        // Create the response element
        SOAPElement responseElement = body.addChildElement("wsGetAccountDetailsResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");
        SOAPElement soapElement = responseElement.addChildElement("GetAccountDetails");

        // Extract the GetAccountDetails from the response
        WsGetAccountDetailsResponse.GetAccountDetails accountDetails = response.getGetAccountDetails();
        if (accountDetails != null) {
            if(accountDetails.getItem()!=null && !accountDetails.getItem().isEmpty()) {
                for (WsGetAccountDetailsResponse.GetAccountDetails.Item item : accountDetails.getItem()) {
                    SOAPElement itemElement = soapElement.addChildElement("item");
                    itemElement.addChildElement("key").addTextNode(getSafeText(item.getKey()));
                    itemElement.addChildElement("value").addTextNode(getSafeText(item.getValue()));
                }
            }
            if(accountDetails.getResponeCode()==200){
                soapElement.addChildElement("password").addTextNode(getSafeText(accountDetails.getPassword()));
            }
            soapElement.addChildElement("requestId").addTextNode(getSafeText(accountDetails.getRequestId()));
            soapElement.addChildElement("responeCode").addTextNode(getSafeNumber(accountDetails.getResponeCode()));
            soapElement.addChildElement("responseMessage").addTextNode(getSafeText(accountDetails.getResponseMessage()));
            if(accountDetails.getResponeCode()==200){
                soapElement.addChildElement("serviceId").addTextNode(getSafeText(accountDetails.getServiceId()));
                soapElement.addChildElement("userName").addTextNode(getSafeText(accountDetails.getUserName()));
            }
        }

        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // Convert SOAP message to DOMSource
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for the constructed XML
        return new DOMSource(fragment);
    }
     */

    /**
     * Creates a SOAP 1.1 success response for GetAccountDetails.
     *
     * @param response       The response object containing account details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateGetAccountDetailsSOAP11SuccessResponse(List<WsGetAccountDetailsResponse> response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Add the main response element with custom namespaces
        SOAPElement responseElement = body.addChildElement("wsGetAccountDetailsResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement getAccountDetails = responseElement.addChildElement("GetAccountDetails");
        // Add the item elements with keys and values
        String ID = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(0).getValue());
        if (!ID.isEmpty() && ID != null)
            addItem(getAccountDetails, "ID", ID);

        String CONCURRENTLOGINPOLICY = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(1).getValue());
        if (!CONCURRENTLOGINPOLICY.isEmpty() && CONCURRENTLOGINPOLICY != null)
            addItem(getAccountDetails, "CONCURRENTLOGINPOLICY", CONCURRENTLOGINPOLICY);

        String ADDITIONALPOLICY = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(2).getValue());
        if (!ADDITIONALPOLICY.isEmpty() && ADDITIONALPOLICY != null)
            addItem(getAccountDetails, "ADDITIONALPOLICY", ADDITIONALPOLICY);

        String CUSTOMERSTATUS = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(3).getValue());
        if (!CUSTOMERSTATUS.isEmpty() && CUSTOMERSTATUS != null)
            addItem(getAccountDetails, "CUSTOMERSTATUS", CUSTOMERSTATUS);

        String param1 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(4).getValue());
        if (!param1.isEmpty() && param1 != null)
            addItem(getAccountDetails, "PARAM1", param1);

        String GEOLOCATION = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(5).getValue());
        if (!GEOLOCATION.isEmpty() && GEOLOCATION != null)
            addItem(getAccountDetails, "GEOLOCATION", GEOLOCATION);

        String PARAM2 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(6).getValue());
        if (!PARAM2.isEmpty() && PARAM2 != null)
            addItem(getAccountDetails, "PARAM2", PARAM2);

        String PARAM3 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(7).getValue());
        if (!PARAM3.isEmpty() && PARAM3 != null)
            addItem(getAccountDetails, "PARAM3", PARAM3);

        String PARAM4 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(8).getValue());
        if (!PARAM4.isEmpty() && PARAM4 != null)
            addItem(getAccountDetails, "PARAM4", PARAM4);

        String PARAM6 = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(9).getValue());
        if (!PARAM6.isEmpty() && PARAM6 != null)
            addItem(getAccountDetails, "PARAM6", PARAM6);

        String GROUPNAME = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(10).getValue());
        if (!GROUPNAME.isEmpty() && GROUPNAME != null)
            addItem(getAccountDetails, "GROUPNAME", GROUPNAME);

        String CALLINGSTATIONID = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(11).getValue());
        if (!CALLINGSTATIONID.isEmpty() && CALLINGSTATIONID != null)
            addItem(getAccountDetails, "CALLINGSTATIONID", CALLINGSTATIONID);

        String CUSTOMERREPLYITEM = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(12).getValue());
        if (!CUSTOMERREPLYITEM.isEmpty() && CUSTOMERREPLYITEM != null)
            addItem(getAccountDetails, "CUSTOMERREPLYITEM", CUSTOMERREPLYITEM);

        String MACVALIDATION = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(13).getValue());
        if (!MACVALIDATION.isEmpty() && MACVALIDATION != null)
            addItem(getAccountDetails, "MACVALIDATION", MACVALIDATION);

        String CUI = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(14).getValue());
        if (!CUI.isEmpty() && CUI != null)
            addItem(getAccountDetails, "CUI", CUI);

        String MSISDN = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(15).getValue());
        if (!MSISDN.isEmpty() && MSISDN != null)
            addItem(getAccountDetails, "MSISDN", MSISDN);

        String CUSTOMERALTEMAILID = String.valueOf(response.get(0).getGetAccountDetails().getItem().get(16).getValue());
        if (!CUSTOMERALTEMAILID.isEmpty() && CUSTOMERALTEMAILID != null)
            addItem(getAccountDetails, "CUSTOMERALTEMAILID", CUSTOMERALTEMAILID);

        // Add the remaining fields
        getAccountDetails.addChildElement("password").addTextNode(response.get(0).getGetAccountDetails().getPassword());
        getAccountDetails.addChildElement("requestId").addTextNode(response.get(0).getGetAccountDetails().getRequestId());
        getAccountDetails.addChildElement("responeCode").addTextNode(String.valueOf(response.get(0).getGetAccountDetails().getResponeCode()));
        getAccountDetails.addChildElement("responseMessage").addTextNode(response.get(0).getGetAccountDetails().getResponseMessage());
        for (WsGetAccountDetailsResponse dto : response) {
            getAccountDetails.addChildElement("serviceId").addTextNode(dto.getGetAccountDetails().getServiceId());
        }
        getAccountDetails.addChildElement("userName").addTextNode(response.get(0).getGetAccountDetails().getUserName());

        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    // Helper method to add item elements
    private void addItem(SOAPElement parent, String key, String value) throws SOAPException {
        SOAPElement item = parent.addChildElement("item");
        item.addChildElement("key").addTextNode(key);
        item.addChildElement("value").addTextNode(value);
    }

    /**
     * Creates a SOAP 1.1 exception response for GetAccountDetails with error details.
     *
     * @param response       The response object containing account details (unused in exception case).
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP exception response.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateGetAccountDetailsSOAP11ExceptionResponse(WsGetAccountDetailsResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement responseElement = body.addChildElement("wsGetAccountDetailsResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");


        SOAPElement getAccountDetails = responseElement.addChildElement("GetAccountDetails");
        getAccountDetails.addChildElement("requestId").addTextNode(response.getGetAccountDetails().getRequestId() != null ? response.getGetAccountDetails().getRequestId() : "?");
        getAccountDetails.addChildElement("responeCode").addTextNode(String.valueOf(response.getGetAccountDetails().getResponeCode()));
        getAccountDetails.addChildElement("responseMessage").addTextNode(response.getGetAccountDetails().getResponseMessage());

        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    public String formatNumericResponse(String response) {
        // Parse the response into a number
        try {
            int number = Integer.parseInt(response.trim());

            // Check the number and format accordingly
            if (number >= 0 && number <= 9) {
                return "0" + number; // Add leading zero for single-digit numbers
            } else {
                return String.valueOf(number); // Return as-is for numbers >= 10
            }
        } catch (NumberFormatException e) {
            // Handle cases where the response is not a valid numeric value
            throw new IllegalArgumentException("Invalid numeric response: " + response);
        }
    }


}
