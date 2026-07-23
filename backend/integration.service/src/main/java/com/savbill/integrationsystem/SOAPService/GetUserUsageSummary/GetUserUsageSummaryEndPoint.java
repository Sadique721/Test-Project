package com.savbill.integrationsystem.SOAPService.GetUserUsageSummary;


import com.savbill.integrationsystem.SOAPService.wsGetBalance.GetBalanceDto;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.getuserusagesummary.GetUserUsageSummary;
import com.savbill.integrationsystem.generated.getuserusagesummary.WsGetUserUsageSummary;
import com.savbill.integrationsystem.generated.getuserusagesummary.WsGetUserUsageSummaryResponse;
import com.savbill.integrationsystem.generated.newgetuserusagesummary.GetUserUsageSummaryResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GetUserUsageSummaryEndPoint {

    @Autowired
    public RadiusClientService radiusClientService;
    private static final long KB_TO_BYTES = 1024L;
    private static final long MB_TO_BYTES = KB_TO_BYTES * 1024L;
    private static final long GB_TO_BYTES = MB_TO_BYTES * 1024L;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsGetUserUsageSummary")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newgetuserusagesummary.WsGetUserUsageSummaryResponse getUserUsageSummaryResponse(@RequestPayload WsGetUserUsageSummary request, MessageContext messageContext) throws SOAPException, IOException {
        com.savbill.integrationsystem.generated.newgetuserusagesummary.WsGetUserUsageSummaryResponse response = null;
        long startTime = System.currentTimeMillis();
        try {
            response = getUserUsageSummary1(request);
            log.info("Method getUserUsageSummary completed in {}ms", System.currentTimeMillis() - startTime);
            if(response.getGetUserUsageSummary().getResponseCode()==SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE){
//                return generateGetUserUsageSummarySOAP11SuccessResponse(response, messageContext);
            }
//            return generateGetUserUsageSummarySOAP11ExceptionResponse(response, messageContext);
        } catch (Exception e) {
            log.info("Method getUserUsageSummary completed in {}ms", System.currentTimeMillis() - startTime);
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
//            return generateGetUserUsageSummarySOAP11SuccessResponse(response, messageContext);
            e.printStackTrace();
        }
        return response;
    }

    public com.savbill.integrationsystem.generated.newgetuserusagesummary.WsGetUserUsageSummaryResponse getUserUsageSummary1(WsGetUserUsageSummary request) {
        com.savbill.integrationsystem.generated.newgetuserusagesummary.WsGetUserUsageSummaryResponse resp = new com.savbill.integrationsystem.generated.newgetuserusagesummary.WsGetUserUsageSummaryResponse();
        String userName = request.getSubscriberId().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        long startTime = System.currentTimeMillis();
        log.info("getUserUsageSummary1 Method Started At:{}",new Date(startTime));
        if (userName == null || userName.isEmpty()) {
            GetUserUsageSummaryResponse summaryResponse = new GetUserUsageSummaryResponse();
            summaryResponse.setRequestId(requestId);
            summaryResponse.setResponseCode(SoapConstants.EMPTY);
            summaryResponse.setResponseMessage("Input SubcriberID is Empty or Null.");
            resp.setGetUserUsageSummary(summaryResponse);
            log.warn("UserName Null Or Empty");
            return resp;
        }

        try {
            String subscriberId = userName;
            Long mvnoId = SoapConstants.MVNOID;
            log.debug("Call Radius Client To Check Usage for:{}",userName);
            GenericDataDTO genericDataDTO = radiusClientService.GetUserUsageSummery(subscriberId, mvnoId);
            log.debug("Integration Received Response iN:{}MS,Response:{}",System.currentTimeMillis()-startTime,genericDataDTO.getDataList());
            if(genericDataDTO.getResponseCode()==503){
                long defaultValue = 0L;
                GetUserUsageSummaryResponse response = new GetUserUsageSummaryResponse();
                log.warn("user Not Available In system:{}",userName);
                response.setResponseCode(SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE);
                response.setRequestId(requestId);
                response.setResponseMessage(genericDataDTO.getResponseMessage());
                resp.setGetUserUsageSummary(response);
                response.setAggregateBytesLimit(defaultValue);
                response.setAggregateBytesRemaining(defaultValue);
                response.setAggregateBytesUsed(defaultValue);

                response.setInBytesLimit(defaultValue);
                response.setInBytesRemaining(defaultValue);
                response.setInBytesUsed(defaultValue);

                response.setOutBytesLimit(defaultValue);
                response.setOutBytesRemaining(defaultValue);
                response.setOutBytesUsed(defaultValue);

                response.setPackageCode("");
                response.setPackageType("");

                response.setQodBytesLimit(defaultValue);
                response.setQodBytesRemaining(defaultValue);
                response.setQodBytesUsed(defaultValue);
                return resp;
            }
            List<GetBalanceDto> dataMessageList = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(
                            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getDataList()),
                            new TypeReference<List<GetBalanceDto>>() {
                            });

            for (GetBalanceDto dataMessage : dataMessageList) {
                GetUserUsageSummaryResponse response = new GetUserUsageSummaryResponse();

                long byteLimit = calCulateBytes(dataMessage.getTotalQuota(), dataMessage.getQuotaUnit());
                long byteRemaining = calCulateBytes(dataMessage.getTotalQuota() - (dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume()), dataMessage.getQuotaUnit());
                long byteUsed = calCulateBytes(dataMessage.getUsedQuota() + dataMessage.getCurrentSessionUsageVolume(), dataMessage.getQuotaUnit());
                long defaultValue = 0L;


                response.setRequestId(requestId);
                response.setResponseCode(SoapConstants.SUCCESS_CODE);
                response.setResponseMessage(SoapConstants.SUCCESS);
                response.setAggregateBytesLimit(byteLimit);
                response.setAggregateBytesRemaining(byteRemaining);
                response.setAggregateBytesUsed(byteUsed);

                response.setInBytesLimit(defaultValue);
                response.setInBytesRemaining(defaultValue);
                response.setInBytesUsed(Long.valueOf(dataMessage.getUploadQuota()));

                response.setOutBytesLimit(defaultValue);
                response.setOutBytesRemaining(defaultValue);
                response.setOutBytesUsed(Long.valueOf(dataMessage.getDownloadQuota()));

                response.setPackageCode(dataMessage.getPlanName());
                response.setPackageType(dataMessage.getUsageQuotaType());

                response.setQodBytesLimit(defaultValue);
                response.setQodBytesRemaining(defaultValue);
                response.setQodBytesUsed(defaultValue);
                resp.setGetUserUsageSummary(response);

            }
            log.info("Successfully Fetched user Usage Summary:{}",userName);

        } catch (Exception e) {
            log.error("Exception occurred While Performing error:{}",e.getMessage(),e);
            GetUserUsageSummaryResponse response = new GetUserUsageSummaryResponse();
            response.setResponseCode(SoapConstants.INTERNAL_ERROR);
            response.setRequestId(requestId);
            response.setResponseMessage("An error occurred while processing the request: " + e.getMessage());
            resp.setGetUserUsageSummary(response);
//            wsGetUserUsageSummaryResponseList.add(wsGetUserUsageSummaryResponse);
        }

        return resp;
    }


    public List<WsGetUserUsageSummaryResponse> getUserUsageSummary(WsGetUserUsageSummary request) {
        List<WsGetUserUsageSummaryResponse> wsGetUserUsageSummaryResponseList = new ArrayList<>();
        String userName = request.getSubscriberId().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        if (userName == null || userName.isEmpty()) {
            WsGetUserUsageSummaryResponse wsGetUserUsageSummaryResponse = new WsGetUserUsageSummaryResponse();
            WsGetUserUsageSummaryResponse.GetUserUsageSummary response = new WsGetUserUsageSummaryResponse.GetUserUsageSummary();

            response.setRequestId(requestId);
            response.setResponseCode(SoapConstants.EMPTY);
            response.setResponseMessage(SoapConstants.Input_Username_is_Empty_or_null);
            wsGetUserUsageSummaryResponse.setGetUserUsageSummary(response);
            wsGetUserUsageSummaryResponseList.add(wsGetUserUsageSummaryResponse);
            return wsGetUserUsageSummaryResponseList;
        }

        try {
            String subscriberId = userName;
            Long mvnoId = SoapConstants.MVNOID;
            GenericDataDTO genericDataDTO = radiusClientService.GetUserUsageSummery(subscriberId, mvnoId);
            if(genericDataDTO.getResponseCode()==503){
                WsGetUserUsageSummaryResponse wsGetUserUsageSummaryResponse = new WsGetUserUsageSummaryResponse();
                WsGetUserUsageSummaryResponse.GetUserUsageSummary response = new WsGetUserUsageSummaryResponse.GetUserUsageSummary();

                response.setResponseCode(SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE);
                response.setRequestId(requestId);
                response.setResponseMessage(genericDataDTO.getResponseMessage());
                wsGetUserUsageSummaryResponse.setGetUserUsageSummary(response);
                wsGetUserUsageSummaryResponseList.add(wsGetUserUsageSummaryResponse);
                return wsGetUserUsageSummaryResponseList;
            }
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
            }

        } catch (Exception e) {
            WsGetUserUsageSummaryResponse wsGetUserUsageSummaryResponse = new WsGetUserUsageSummaryResponse();
            WsGetUserUsageSummaryResponse.GetUserUsageSummary response = new WsGetUserUsageSummaryResponse.GetUserUsageSummary();

            response.setResponseCode(SoapConstants.INTERNAL_ERROR);
            response.setRequestId(requestId);
            response.setResponseMessage("An error occurred while processing the request: " + e.getMessage());
            wsGetUserUsageSummaryResponse.setGetUserUsageSummary(response);
            wsGetUserUsageSummaryResponseList.add(wsGetUserUsageSummaryResponse);
        }

        return wsGetUserUsageSummaryResponseList;
    }

    /*
    public DOMSource generateGetUserUsageSummarySOAPResponse(WsGetUserUsageSummaryResponse response) throws SOAPException, ParserConfigurationException {
        // Create a SOAP Message factory and message
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Add namespace declarations
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("ns2", "http://api.act.com/");
        envelope.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPBody body = envelope.getBody();

        // Create the response element
        SOAPElement responseElement = body.addChildElement("wsGetUserUsageSummaryResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add GetUserUsageSummary element
        SOAPElement getUserUsageSummary = responseElement.addChildElement("GetUserUsageSummary");

        // Add child elements for GetUserUsageSummary
        if(response.getGetUserUsageSummary().getResponseCode()!=500) {
            getUserUsageSummary.addChildElement("aggregateBytesLimit")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getAggregateBytesLimit()));
            getUserUsageSummary.addChildElement("aggregateBytesRemaining")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getAggregateBytesRemaining()));
            getUserUsageSummary.addChildElement("aggregateBytesUsed")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getAggregateBytesUsed()));

            getUserUsageSummary.addChildElement("inBytesLimit")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getInBytesLimit()));
            getUserUsageSummary.addChildElement("inBytesRemaining")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getInBytesRemaining()));
            getUserUsageSummary.addChildElement("inBytesUsed")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getInBytesUsed()));

            getUserUsageSummary.addChildElement("outBytesLimit")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getOutBytesLimit()));
            getUserUsageSummary.addChildElement("outBytesRemaining")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getOutBytesRemaining()));
            getUserUsageSummary.addChildElement("outBytesUsed")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getOutBytesUsed()));

            getUserUsageSummary.addChildElement("packageCode")
                    .addTextNode(getSafeText(response.getGetUserUsageSummary().getPackageCode()));
            getUserUsageSummary.addChildElement("packageType")
                    .addTextNode(getSafeText(response.getGetUserUsageSummary().getPackageType()));

            getUserUsageSummary.addChildElement("qodBytesLimit")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getQodBytesLimit()));
            getUserUsageSummary.addChildElement("qodBytesRemaining")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getQodBytesRemaining()));
            getUserUsageSummary.addChildElement("qodBytesUsed")
                    .addTextNode(getSafeNumberDouble(response.getGetUserUsageSummary().getQodBytesUsed()));
        }
        getUserUsageSummary.addChildElement("requestId")
                .addTextNode(getSafeText(response.getGetUserUsageSummary().getRequestId()));
        getUserUsageSummary.addChildElement("responseCode")
                .addTextNode(getSafeNumber(response.getGetUserUsageSummary().getResponseCode()));
        getUserUsageSummary.addChildElement("responseMessage")
                .addTextNode(getSafeText(response.getGetUserUsageSummary().getResponseMessage()));

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

    // Use this both success and exception response generator method without interceptor
    // First test for checking sheet response and adjust parameter value by taking from response class

    /**
     * Generates a SOAP 1.1 success response message for the GetUserUsageSummary operation.
     * This method creates a SOAP message with a successful response, including user usage summary data,
     * and returns the resulting message as a DOMSource for further processing.
     * <p>
     * //     * @param response       the {@link WsGetUserUsageSummaryResponse} containing the user usage summary data
     *
     * @param messageContext the {@link MessageContext} used to update the response message context with the new SOAP message
     * @return a {@link DOMSource} containing the SOAP response message with the user usage summary details
     * @throws SOAPException if there is an error in creating or processing the SOAP message
     */
    public DOMSource generateGetUserUsageSummarySOAP11SuccessResponse(List<WsGetUserUsageSummaryResponse> response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove the default namespace and add custom namespaces
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
        SOAPElement responseElement = body.addChildElement("wsGetUserUsageSummaryResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add the GetUserUsageSummary element
        SOAPElement getUserUsageSummary = responseElement.addChildElement("GetUserUsageSummary");

        // Add child elements for each field from the XML response
        getUserUsageSummary.addChildElement("requestId").addTextNode(response.get(0).getGetUserUsageSummary().getRequestId() != null && !response.get(0).getGetUserUsageSummary().getRequestId().isEmpty() ? response.get(0).getGetUserUsageSummary().getRequestId() : "?");
        getUserUsageSummary.addChildElement("responseCode").addTextNode(response.get(0).getGetUserUsageSummary().getResponseCode().toString());
        getUserUsageSummary.addChildElement("responseMessage").addTextNode(response.get(0).getGetUserUsageSummary().getResponseMessage());


        // Set the response in the SaajSoapMessage object
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource for further processing
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        // Append all child nodes of the body to the fragment
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for further processing
        return new DOMSource(fragment);
    }

    /**
     * Generates a SOAP 1.1 response message for the GetUserUsageSummary operation indicating an exception.
     * This method creates a SOAP message with an exception response, including error details,
     * and returns the resulting message as a DOMSource for further processing.
     * <p>
     * //     * @param response       the {@link WsGetUserUsageSummaryResponse} containing the user usage summary data or error details
     *
     * @param messageContext the {@link MessageContext} used to update the response message context with the new SOAP message
     * @return a {@link DOMSource} containing the SOAP response message indicating an exception or error
     * @throws SOAPException if there is an error in creating or processing the SOAP message
     */
    public DOMSource generateGetUserUsageSummarySOAP11ExceptionResponse(List<WsGetUserUsageSummaryResponse> dto, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove the default namespace and add custom namespaces
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
        SOAPElement responseElement = body.addChildElement("wsGetUserUsageSummaryResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add the GetUserUsageSummary element
        for (WsGetUserUsageSummaryResponse response : dto) {
            SOAPElement getUserUsageSummary = responseElement.addChildElement("GetUserUsageSummary");
            getUserUsageSummary.addChildElement("aggregateBytesLimit").addTextNode(response.getGetUserUsageSummary().getAggregateBytesLimitLong().toString());
            getUserUsageSummary.addChildElement("aggregateBytesRemaining").addTextNode(response.getGetUserUsageSummary().getAggregateBytesRemainingLong().toString());
            getUserUsageSummary.addChildElement("aggregateBytesUsed").addTextNode(response.getGetUserUsageSummary().getAggregateBytesUsedLong().toString());
            getUserUsageSummary.addChildElement("inBytesLimit").addTextNode(response.getGetUserUsageSummary().getInBytesLimitLong().toString());
            getUserUsageSummary.addChildElement("inBytesRemaining").addTextNode(response.getGetUserUsageSummary().getInBytesRemainingLong().toString());
            getUserUsageSummary.addChildElement("inBytesUsed").addTextNode(response.getGetUserUsageSummary().getInBytesUsedLong().toString());
            getUserUsageSummary.addChildElement("outBytesLimit").addTextNode(response.getGetUserUsageSummary().getOutBytesLimitLong().toString());
            getUserUsageSummary.addChildElement("outBytesRemaining").addTextNode(response.getGetUserUsageSummary().getOutBytesRemainingLong().toString());
            getUserUsageSummary.addChildElement("outBytesUsed").addTextNode(response.getGetUserUsageSummary().getOutBytesUsedLong().toString());
            getUserUsageSummary.addChildElement("packageCode").addTextNode(response.getGetUserUsageSummary().getPackageCode());
            getUserUsageSummary.addChildElement("packageType").addTextNode(response.getGetUserUsageSummary().getPackageType());
            getUserUsageSummary.addChildElement("qodBytesLimit").addTextNode(response.getGetUserUsageSummary().getQodBytesLimitLong().toString());
            getUserUsageSummary.addChildElement("qodBytesRemaining").addTextNode(response.getGetUserUsageSummary().getQodBytesRemainingLong().toString());
            getUserUsageSummary.addChildElement("qodBytesUsed").addTextNode(response.getGetUserUsageSummary().getQodBytesUsedLong().toString());
            getUserUsageSummary.addChildElement("requestId").addTextNode(response.getGetUserUsageSummary().getRequestId() != null && !response.getGetUserUsageSummary().getRequestId().isEmpty() ? response.getGetUserUsageSummary().getRequestId() : "");
            getUserUsageSummary.addChildElement("responseCode").addTextNode(response.getGetUserUsageSummary().getResponseCode().toString());
            getUserUsageSummary.addChildElement("responseMessage").addTextNode(response.getGetUserUsageSummary().getResponseMessage());

        }
        // Add hardcoded values to the SOAP response
        // Set the response in the SaajSoapMessage object
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource for further processing
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        // Append all child nodes of the body to the fragment
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for further processing
        return new DOMSource(fragment);
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
                return 0l;
        }
    }

}
