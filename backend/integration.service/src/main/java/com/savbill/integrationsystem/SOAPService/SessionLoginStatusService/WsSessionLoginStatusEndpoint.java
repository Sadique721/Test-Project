package com.savbill.integrationsystem.SOAPService.SessionLoginStatusService;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.newsessionloginstatus.SessionLoginStatusResponse;
import com.savbill.integrationsystem.generated.wssessionloginstatus.WsSessionLoginStatus;
import com.savbill.integrationsystem.generated.wssessionloginstatus.WsSessionLoginStatusResponse;
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
import java.util.Map;

@Slf4j
@Endpoint
public class WsSessionLoginStatusEndpoint {

    @Autowired
    private RadiusClientService radiusClientService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsSessionLoginStatus")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newsessionloginstatus.WsSessionLoginStatusResponse handleWsSessionLoginStatusResponse(@RequestPayload WsSessionLoginStatus request, MessageContext messageContext) throws SOAPException, IOException {
        try {
            return handleWsSessionLoginStatus(request);
        } catch (Exception e) {
            return handleWsSessionLoginStatus(request);
        }
    }

    public com.savbill.integrationsystem.generated.newsessionloginstatus.WsSessionLoginStatusResponse handleWsSessionLoginStatus(WsSessionLoginStatus request) {
        long startTime = System.currentTimeMillis();
        log.info("Starting method: handleWsSessionLoginStatusResponse At:{}", new Date(startTime));
        com.savbill.integrationsystem.generated.newsessionloginstatus.WsSessionLoginStatusResponse response = new com.savbill.integrationsystem.generated.newsessionloginstatus.WsSessionLoginStatusResponse();
        SessionLoginStatusResponse sessionStatus = new SessionLoginStatusResponse();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        sessionStatus.setRequestId(requestId);

        String ipAddress = request.getIpAddress().trim();

        if (ipAddress == null || ipAddress.isEmpty()) {
            log.warn("IP address is empty or null");
            sessionStatus.setResponeCode(SoapConstants.EMPTY);
            sessionStatus.setResponseMessage("Input Ip Address is Empty or Null.");
            sessionStatus.setResult(false);
            response.setSessionLoginStatus(sessionStatus);
            log.info("Method handleWsSessionLoginStatus completed in {}ms", System.currentTimeMillis() - startTime);
            return response;
        } else {
            try {
                log.debug("Call Radius Client To Fetch session login status for IP: {}", ipAddress);
                GenericDataDTO radiusResponse = radiusClientService.SessionLoginStatus(ipAddress, SoapConstants.MVNOID);
                log.debug("Integration Received Response In:{}MS,session login status for IP: {},Response:{}",
                        System.currentTimeMillis() - startTime, ipAddress, radiusResponse.getData());

                if (radiusResponse != null && radiusResponse.getData() instanceof Map) {
                    log.debug("Processing radius response data");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> radiusData = (Map<String, Object>) radiusResponse.getData();
                    String framedIpAddress = (String) radiusData.get("framedIpAddress");
                    String username = (String) radiusData.get("userName");
                    log.debug("Call Radius Client To Check user status");
                    GenericDataDTO checkKnoewUser = radiusClientService.checkUnKnownUser(ipAddress, SoapConstants.MVNOID);
                    log.debug("Integration Received Ip:{},Status:{}", ipAddress, checkKnoewUser.getData());

                    if (framedIpAddress != null && framedIpAddress.equals(ipAddress)) {
                        if (username.equals(username) && !username.isEmpty() && checkKnoewUser.getResponseMessage().equalsIgnoreCase("SUCCESS")) {
                            log.info("Successfully validated user session for IP:{}", ipAddress);
                            sessionStatus.setResponeCode(SoapConstants.SUCCESS_CODE);
                            sessionStatus.setResponseMessage(checkKnoewUser.getData().toString());
                            sessionStatus.setResult(true);
                            response.setSessionLoginStatus(sessionStatus);
                            log.info("Method handleWsSessionLoginStatus completed in {}ms", System.currentTimeMillis() - startTime);
                            return response;
                        } else {
                            log.warn("Unknown user parameters detected for Ip:{}", ipAddress);
                            sessionStatus.setResponeCode(SoapConstants.UNKNOWN_PARAM);
                            sessionStatus.setResponseMessage(checkKnoewUser.getData().toString());
                            sessionStatus.setResult(false);
                            response.setSessionLoginStatus(sessionStatus);
                            log.info("Method handleWsSessionLoginStatus completed in {}ms", System.currentTimeMillis() - startTime);
                            return response;
                        }
                    } else {
                        log.warn("ipAddress:{} not found in session table", ipAddress);
                        sessionStatus.setResponeCode(SoapConstants.NOT_AVAILABLE);
                        sessionStatus.setResponseMessage("IP is not available in session table");
                        sessionStatus.setResult(false);
                        response.setSessionLoginStatus(sessionStatus);
                        log.info("Method handleWsSessionLoginStatus completed in {}ms", System.currentTimeMillis() - startTime);
                        return response;
                    }
                } else {
                    log.warn("No radius response data available");
                    sessionStatus.setResponeCode(SoapConstants.NOT_AVAILABLE);
                    sessionStatus.setResponseMessage("IP is not available in session table");
                    sessionStatus.setResult(false);
                    response.setSessionLoginStatus(sessionStatus);
                    log.info("Method handleWsSessionLoginStatus completed in {}ms", System.currentTimeMillis() - startTime);
                    return response;
                }
            } catch (Exception e) {
                log.error("Error processing session login status", e);
                sessionStatus.setResponeCode(SoapConstants.INTERNAL_ERROR);
                sessionStatus.setResponseMessage("Internal Server Error: " + e.getMessage());
                sessionStatus.setResult(false);
            }
        }

        response.setSessionLoginStatus(sessionStatus);
        log.info("Method handleWsSessionLoginStatus completed in {}ms", System.currentTimeMillis() - startTime);
        return response;
    }


    /*
    public DOMSource generateSessionLoginStatusSOAPResponse(WsSessionLoginStatusResponse response) throws SOAPException, ParserConfigurationException {
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
        SOAPElement responseElement = body.addChildElement("wsSessionLoginStatusResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add SessionLoginStatus element
        SOAPElement sessionLoginStatusElement = responseElement.addChildElement("SessionLoginStatus");

        // Add child elements to SessionLoginStatus
        if(response.getSessionLoginStatus().getRequestId()==null || response.getSessionLoginStatus().getRequestId().equals("?") || response.getSessionLoginStatus().getRequestId().equals("") || response.getSessionLoginStatus().getRequestId().equals(" ")){
            sessionLoginStatusElement.addChildElement("requestId").addTextNode("?");
        }else {
            sessionLoginStatusElement.addChildElement("requestId").addTextNode(getSafeText(response.getSessionLoginStatus().getRequestId()));
        }
        sessionLoginStatusElement.addChildElement("responeCode").addTextNode(getSafeNumber(response.getSessionLoginStatus().getResponeCode()));
        sessionLoginStatusElement.addChildElement("responseMessage").addTextNode(getSafeText(response.getSessionLoginStatus().getResponseMessage()));
        sessionLoginStatusElement.addChildElement("result").addTextNode(String.valueOf(response.getSessionLoginStatus().isResult()));

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
     * Creates a SOAP 1.1 response for session login status with a success message and additional details.
     * The response indicates a successful login status with a response code of 200 and a message about the session.
     *
     * @param response       The response object containing session login status details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for session login status.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateSessionLoginStatusSAOP11SuccessAndExceptionResponse(WsSessionLoginStatusResponse response, MessageContext messageContext) throws SOAPException {
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

        SOAPElement responseElement = body.addChildElement("wsSessionLoginStatusResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement sessionLoginStatus = responseElement.addChildElement("SessionLoginStatus");
        sessionLoginStatus.addChildElement("requestId").addTextNode(response.getSessionLoginStatus().getRequestId());
        sessionLoginStatus.addChildElement("responeCode").addTextNode(String.valueOf(response.getSessionLoginStatus().getResponeCode()));
        sessionLoginStatus.addChildElement("responseMessage").addTextNode(response.getSessionLoginStatus().getResponseMessage());
        sessionLoginStatus.addChildElement("result").addTextNode(String.valueOf(response.getSessionLoginStatus().isResult()));

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
