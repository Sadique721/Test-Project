package com.savbill.integrationsystem.SOAPService.WsGetUserSessions;


import com.savbill.integrationsystem.SOAPService.GetUserUsageSummary.GetUserSessionresponseDto;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;

import com.savbill.integrationsystem.generated.newwsgetsessionbyip.GetUserSessionResponse;
import com.savbill.integrationsystem.generated.wsgetsessionsbyip.WsGetUserSession;
import com.savbill.integrationsystem.generated.wsgetsessionsbyip.WsGetUserSessionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Date;

@Slf4j
@Endpoint
public class WsGetUserSessionsService {

    @Autowired
    public RadiusClientService radiusClientService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsGetUserSession")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newwsgetsessionbyip.WsGetUserSessionResponse getUserSessionResponse(@RequestPayload WsGetUserSession request, MessageContext messageContext) throws SOAPException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("Starting method: getUserSessionResponse At:{}", new Date(startTime));
        com.savbill.integrationsystem.generated.newwsgetsessionbyip.WsGetUserSessionResponse response = null;
        com.savbill.integrationsystem.generated.newwsgetsessionbyip.WsGetUserSessionResponse getUserSessionResponse = new com.savbill.integrationsystem.generated.newwsgetsessionbyip.WsGetUserSessionResponse();
        GetUserSessionResponse getUserSession = new GetUserSessionResponse();
//        GetUserSessionResponse response =
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();

        try {
            String ipAddress = request.getIpAddress().trim();
            if (ipAddress == null || ipAddress.isEmpty()) {
                log.warn("IP address is empty or null");
                getUserSession.setRequestId(requestId);
                getUserSession.setResponeCode(SoapConstants.EMPTY);
                getUserSession.setResponseMessage("Input IpAddress is Empty or Null.");
                getUserSessionResponse.setGetUserSession(getUserSession);
                log.info("Method getUserSessionResponse completed in {}ms", System.currentTimeMillis() - startTime);
                return getUserSessionResponse;
            }
            log.debug("Processing user session for IpAddress:{}", ipAddress);
            response = getUserSession(request.getIpAddress(), requestId);
            if (response.getGetUserSession().getResponeCode() == 407) {
                log.warn("Invalid IP address response code received: 407");
                log.info("Method getUserSessionResponse completed in {}ms", System.currentTimeMillis() - startTime);
                return response;
            }
            log.info("Method getUserSessionResponse completed in {}ms", System.currentTimeMillis() - startTime);
            return response;
        } catch (Exception e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            return response;
        }
    }

    private DOMSource generateGetUserSessionSOAP11SuccessResponseNull(WsGetUserSessionResponse response, MessageContext messageContext) throws Exception {
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

        SOAPElement getUserSessionResponseElement = body.addChildElement("wsGetUserSessionResponse", "ns2", "http://api.act.com/");
        getUserSessionResponseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement getUserSession = getUserSessionResponseElement.addChildElement("GetUserSession");
        getUserSession.addChildElement("requestId")
                .addTextNode(response.getGetUserSession().getRequestId() != null
                        ? response.getGetUserSession().getRequestId()
                        : "");
        getUserSession.addChildElement("responeCode")
                .addTextNode(response.getGetUserSession().getResponeCode() != null
                        ? String.valueOf(response.getGetUserSession().getResponeCode())
                        : "");

        getUserSession.addChildElement("responseMessage")
                .addTextNode(response.getGetUserSession().getResponseMessage() != null
                        ? response.getGetUserSession().getResponseMessage()
                        : "");
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

    public com.savbill.integrationsystem.generated.newwsgetsessionbyip.WsGetUserSessionResponse getUserSession(String getIpAddress, String requestId) {
        long startTime = System.currentTimeMillis();
        log.info("Starting method: getUserSession At:{}", new Date(startTime));

        com.savbill.integrationsystem.generated.newwsgetsessionbyip.WsGetUserSessionResponse response = new com.savbill.integrationsystem.generated.newwsgetsessionbyip.WsGetUserSessionResponse();
        GetUserSessionResponse getUserSession = new GetUserSessionResponse();
        try {
            getIpAddress = getIpAddress.trim();
            if (getIpAddress == null || getIpAddress.isEmpty()) {
                log.warn("IP address is empty or null in getUserSession");
                getUserSession.setRequestId(requestId);
                getUserSession.setResponeCode(SoapConstants.EMPTY);
                getUserSession.setResponseMessage("Input IpAddress is Empty or Null.");
            } else {
                Long mvnoId = SoapConstants.MVNOID;
                log.debug("Call Radius Client To Fetch user session data for Ip:{}", getIpAddress);
                GenericDataDTO genericDataDTO = radiusClientService.GetUserSessionApi(getIpAddress, mvnoId);
                GetUserSessionresponseDto dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()), GetUserSessionresponseDto.class);
                log.debug("Integration Received In:{}MS Response:{}", System.currentTimeMillis() - startTime, genericDataDTO.getData());
                if (getUserSession == null) {
                    response.setGetUserSession(new GetUserSessionResponse());
                }
                if (dataMessage != null) {
                    log.debug("Processing user session data");
                    getUserSession.setAcctSessionId(dataMessage.getAcctSessionId());
                    if (dataMessage.isKnownUser()) {
                        log.debug("Known user detected for ip:{}", getIpAddress);
                        getUserSession.setCallingStationId(dataMessage.getUserName());
                        getUserSession.setSubscriberAccount(dataMessage.getUserName());
                    } else {
                        log.warn("Unknown user detected for ip:{}", getIpAddress);
                        getUserSession.setCallingStationId("UNKNOWN");
                        getUserSession.setSubscriberAccount("UNKNOWN");
                    }
                    getUserSession.setCircuitType(null);
                    getUserSession.setContext(null);
                    getUserSession.setDelegatedIpv6Prefixes(dataMessage.getDelegatedIPv6Prefix());
                    getUserSession.setFramedIpv6Prefixes(dataMessage.getFramedIPv6Prefix());
                    getUserSession.setMacAddress(dataMessage.getCallingStationId());
                    getUserSession.setMedium(null);
                    getUserSession.setNASPortId(dataMessage.getNasPortId());
                    getUserSession.setNASPortType(dataMessage.getNasPortType());
                    getUserSession.setNasId(dataMessage.getNasPortId());
                    getUserSession.setNasType(dataMessage.getNasPortType());
                    getUserSession.setRequestId(requestId);
                    getUserSession.setResponeCode(SoapConstants.SUCCESS_CODE);
                    getUserSession.setResponseMessage(SoapConstants.SUCCESS);
                    getUserSession.setSessionId(dataMessage.getAcctSessionId());
                    getUserSession.setSessionIp(dataMessage.getFramedIpAddress());
                    getUserSession.setStartTime(dataMessage.getCreatedDateString());
                    response.setGetUserSession(getUserSession);
                    log.info("Successfully processed user session data");
                    return response;
                } else {
                    log.warn("No session data found for IP address");
                    getUserSession.setRequestId(requestId);
                    getUserSession.setResponeCode(SoapConstants.NOT_AVAILABLE);
                    getUserSession.setResponseMessage(SoapConstants.NO_RECORD_FOUND_IN_SESSION_TABLE_FOR_GIVEN_IP);
                }
            }
        } catch (Exception e) {
            log.error("Error processing user session", e);
            e.printStackTrace();
            getUserSession.setRequestId(requestId);
            getUserSession.setResponeCode(SoapConstants.INTERNAL_ERROR);
            getUserSession.setResponseMessage("Exception occurred while processing the request ");
            /*response.setResponseCode(SoapConstants.INTERNAL_ERROR);
            response.setResponseMessage("An error occurred while processing the request: " + e.getMessage());*/
        }
        response.setGetUserSession(getUserSession);
        return response;

    }

    /*
    public DOMSource generateUserSessionSOAPResponse(WsGetUserSessionResponse inputResponse) throws SOAPException, ParserConfigurationException {
        // Create a SOAP Message factory and message
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Add namespace declarations
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("ns2", "http://api.act.com/");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");

        SOAPBody body = envelope.getBody();

        // Create the response element
        SOAPElement responseElement = body.addChildElement("wsGetUserSessionResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");
        SOAPElement getUserSessionElement = responseElement.addChildElement("GetUserSession");

        // Set attributes for the response
        if (inputResponse != null && inputResponse.getGetUserSession() != null) {
            WsGetUserSessionResponse.GetUserSession getUserSession = inputResponse.getGetUserSession();
            if(inputResponse.getGetUserSession().getResponeCode()==200) {
                getUserSessionElement.addChildElement("acctSessionId")
                        .addTextNode(getSafeText(getUserSession.getAcctSessionId()));
                getUserSessionElement.addChildElement("callingStationId")
                        .addTextNode(getSafeText(getUserSession.getCallingStationId()));
                getUserSessionElement.addChildElement("circuitType")
                        .addTextNode(getSafeText(getUserSession.getCircuitType()));
                getUserSessionElement.addChildElement("context")
                        .addTextNode(getSafeText(getUserSession.getContext()));
                getUserSessionElement.addChildElement("delegatedIpv6Prefixes")
                        .addTextNode(getSafeText(getUserSession.getDelegatedIpv6Prefixes()));
                getUserSessionElement.addChildElement("framedIpv6Prefixes")
                        .addTextNode(getSafeText(getUserSession.getFramedIpv6Prefixes()));
                getUserSessionElement.addChildElement("macAddress")
                        .addTextNode(getSafeText(getUserSession.getMacAddress()));
                getUserSessionElement.addChildElement("medium")
                        .addTextNode(getSafeText(getUserSession.getMedium()));
                getUserSessionElement.addChildElement("NASPortId")
                        .addTextNode(getSafeText(getUserSession.getNASPortId()));
                getUserSessionElement.addChildElement("NASPortType")
                        .addTextNode(getSafeText(getUserSession.getNASPortType()));
                getUserSessionElement.addChildElement("nasId")
                        .addTextNode(getSafeText(getUserSession.getNasId()));
                getUserSessionElement.addChildElement("nasType")
                        .addTextNode(getSafeText(getUserSession.getNasType()));
                getUserSessionElement.addChildElement("requestId")
                        .addTextNode(getSafeText(getUserSession.getRequestId()));
                getUserSessionElement.addChildElement("responeCode")
                        .addTextNode(getSafeNumber(getUserSession.getResponeCode()));
                getUserSessionElement.addChildElement("responseMessage")
                        .addTextNode(getSafeText(getUserSession.getResponseMessage()));
                getUserSessionElement.addChildElement("sessionId")
                        .addTextNode(getSafeText(getUserSession.getSessionId()));
                getUserSessionElement.addChildElement("sessionIp")
                        .addTextNode(getSafeText(getUserSession.getSessionIp()));
                getUserSessionElement.addChildElement("startTime")
                        .addTextNode(getSafeText(getUserSession.getStartTime()));
                getUserSessionElement.addChildElement("subscriberAccount")
                        .addTextNode(getSafeText(getUserSession.getSubscriberAccount()));
            }else {
                getUserSessionElement.addChildElement("requestId")
                        .addTextNode(getSafeText(getUserSession.getRequestId()));
                getUserSessionElement.addChildElement("responeCode")
                        .addTextNode(getSafeNumber(getUserSession.getResponeCode()));
                getUserSessionElement.addChildElement("responseMessage")
                        .addTextNode(getSafeText(getUserSession.getResponseMessage()));
            }
        } else {
            // Handle the case where no session data is available
            getUserSessionElement.addChildElement("requestId")
                    .addTextNode("1000");
            getUserSessionElement.addChildElement("responseMessage").addTextNode(SoapConstants.NO_RECORD_FOUND_IN_SESSION_TABLE_FOR_GIVEN_IP);
            getUserSessionElement.addChildElement("responeCode").addTextNode(String.valueOf(SoapConstants.NOT_AVAILABLE));
        }

        soapMessage.saveChanges();

        // Convert SOAP Message to DOMSource
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }
     */

    // Use this both success and invalidIp or null response generator method without interceptor
    // First test for checking sheet response and adjust parameter value by taking from response class

    /**
     * Generates a SOAP 1.1 success response for user session details.
     *
     * @param response       The response object containing user session information.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for user session.
     * @throws SOAPException If there is an error creating the SOAP message.
     */
    public DOMSource generateGetUserSessionSOAP11SuccessResponse(WsGetUserSessionResponse response, MessageContext messageContext) throws SOAPException {
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

        SOAPElement getUserSessionResponseElement = body.addChildElement("wsGetUserSessionResponse", "ns2", "http://api.act.com/");
        getUserSessionResponseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement getUserSession = getUserSessionResponseElement.addChildElement("GetUserSession");

        getUserSession.addChildElement("acctSessionId")
                .addTextNode(response.getGetUserSession().getAcctSessionId() != null
                        ? response.getGetUserSession().getAcctSessionId()
                        : "");

        getUserSession.addChildElement("callingStationId")
                .addTextNode(response.getGetUserSession().getCallingStationId() != null
                        ? response.getGetUserSession().getCallingStationId()
                        : "");

        getUserSession.addChildElement("circuitType").addTextNode(""); // Empty string for circuitType
        getUserSession.addChildElement("context").addTextNode(""); // Empty string for context

        getUserSession.addChildElement("delegatedIpv6Prefixes")
                .addTextNode(response.getGetUserSession().getDelegatedIpv6Prefixes() != null
                        ? response.getGetUserSession().getDelegatedIpv6Prefixes()
                        : "");

        getUserSession.addChildElement("framedIpv6Prefixes")
                .addTextNode(response.getGetUserSession().getFramedIpv6Prefixes() != null
                        ? response.getGetUserSession().getFramedIpv6Prefixes()
                        : "");

        getUserSession.addChildElement("macAddress")
                .addTextNode(response.getGetUserSession().getMacAddress() != null
                        ? response.getGetUserSession().getMacAddress()
                        : "");

        getUserSession.addChildElement("medium").addTextNode("");

        getUserSession.addChildElement("NASPortId")
                .addTextNode(response.getGetUserSession().getNASPortId() != null
                        ? response.getGetUserSession().getNASPortId()
                        : "");

        getUserSession.addChildElement("NASPortType")
                .addTextNode(response.getGetUserSession().getNASPortType() != null
                        ? response.getGetUserSession().getNASPortType()
                        : "");

        getUserSession.addChildElement("nasId")
                .addTextNode(response.getGetUserSession().getNasId() != null
                        ? response.getGetUserSession().getNasId()
                        : "");

        getUserSession.addChildElement("nasType")
                .addTextNode(response.getGetUserSession().getNasType() != null
                        ? response.getGetUserSession().getNasType()
                        : "");

        getUserSession.addChildElement("requestId")
                .addTextNode(response.getGetUserSession().getRequestId() != null
                        ? response.getGetUserSession().getRequestId()
                        : "");

        getUserSession.addChildElement("responeCode")
                .addTextNode(response.getGetUserSession().getResponeCode() != null
                        ? String.valueOf(response.getGetUserSession().getResponeCode())
                        : "");

        getUserSession.addChildElement("responseMessage")
                .addTextNode(response.getGetUserSession().getResponseMessage() != null
                        ? response.getGetUserSession().getResponseMessage()
                        : "");

        getUserSession.addChildElement("sessionId")
                .addTextNode(response.getGetUserSession().getSessionId() != null
                        ? response.getGetUserSession().getSessionId()
                        : "");

        getUserSession.addChildElement("sessionIp")
                .addTextNode(response.getGetUserSession().getSessionIp() != null
                        ? response.getGetUserSession().getSessionIp()
                        : "");

        getUserSession.addChildElement("startTime")
                .addTextNode(response.getGetUserSession().getStartTime() != null
                        ? response.getGetUserSession().getStartTime()
                        : "");

        getUserSession.addChildElement("subscriberAccount")
                .addTextNode(response.getGetUserSession().getSubscriberAccount() != null
                        ? response.getGetUserSession().getSubscriberAccount()
                        : "");

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

    /**
     * Generates a SOAP 1.1 response for an invalid IP or null input.
     *
     * @param response       The response object containing user session information.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for the invalid IP or null input.
     * @throws SOAPException If there is an error creating the SOAP message.
     */

    public DOMSource generateGetUserSessionSOAInvalidIpAndNUllResponse(WsGetUserSessionResponse response, MessageContext messageContext) throws SOAPException {
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

        SOAPElement getUserSessionResponseElement = body.addChildElement("wsGetUserSessionResponse", "ns2", "http://api.act.com/");
        getUserSessionResponseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement getUserSession = getUserSessionResponseElement.addChildElement("GetUserSession");

        getUserSession.addChildElement("requestId").addTextNode(response.getGetUserSession().getRequestId());
        getUserSession.addChildElement("responeCode").addTextNode(String.valueOf(response.getGetUserSession().getResponeCode())); // Error code
        getUserSession.addChildElement("responseMessage").addTextNode(response.getGetUserSession().getResponseMessage());

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


}
