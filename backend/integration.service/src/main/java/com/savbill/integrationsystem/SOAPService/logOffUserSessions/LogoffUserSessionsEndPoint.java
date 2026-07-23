package com.savbill.integrationsystem.SOAPService.logOffUserSessions;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.logoffusersessions.WsLogoffUserSessions;
import com.savbill.integrationsystem.generated.logoffusersessions.WsLogoffUserSessionsResponse;
import com.savbill.integrationsystem.generated.newlogoffusersessions.LogoffUserSessionsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.Objects;

@Slf4j
@Endpoint
public class LogoffUserSessionsEndPoint {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RadiusClientService radiusClientService;
    @Autowired
    private JwtUtil jwtUtil;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsLogoffUserSessions")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newlogoffusersessions.WsLogoffUserSessionsResponse getWsLogOffUserSessionResponse(@RequestPayload WsLogoffUserSessions request, MessageContext messageContext) throws SOAPException, IOException {
        try {
            return getWsLogOffUserSession(request);
        } catch (Exception e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            return getWsLogOffUserSession(request);
        }
    }

    public com.savbill.integrationsystem.generated.newlogoffusersessions.WsLogoffUserSessionsResponse getWsLogOffUserSession(@RequestPayload WsLogoffUserSessions request) {
        com.savbill.integrationsystem.generated.newlogoffusersessions.WsLogoffUserSessionsResponse wsLogoffUserSessionsResponse = new com.savbill.integrationsystem.generated.newlogoffusersessions.WsLogoffUserSessionsResponse();
        LogoffUserSessionsResponse logoffUserSessionsResponse = new LogoffUserSessionsResponse();

        long startTime = System.currentTimeMillis();
        log.info("Starting method: getWsLogOffUserSession At:{}", new Date(startTime));

        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        Integer responsecode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = "FAILURE";
        Boolean result = false;
        Long mvnoId = SoapConstants.MVNOID;
        String token = jwtUtil.generateJwtToken(mvnoId);
        logoffUserSessionsResponse.setRequestId(requestId);
        String userName = request.getUserName().trim();

        if (userName == null || userName.isEmpty()) {
            log.warn("Username is empty or null for requestId: {}", requestId);
            responsecode = SoapConstants.EMPTY;
            responseMessage = "Input UserName is Empty or Null.";
            logoffUserSessionsResponse.setResponeCode(responsecode);
            logoffUserSessionsResponse.setResponseMessage(responseMessage);
            logoffUserSessionsResponse.setRequestId(requestId);
            wsLogoffUserSessionsResponse.setLogoffUserSessions(logoffUserSessionsResponse);
            log.info("Method getWsLogOffUserSession completed in: {}MS for username: {}", System.currentTimeMillis() - startTime, request.getUserName());
            return wsLogoffUserSessionsResponse;
        }

        try {
            userName = userName.toLowerCase().trim();
            log.debug("Call Radius Client To Check login status for user: {} with requestId: {}", userName, requestId);
            GenericDataDTO genericDataDTO = radiusClientService.getLiveUserLoginStatus(userName, SoapConstants.MVNOID);
            log.debug("Integration Received in: {}ms Response: {}", System.currentTimeMillis() - startTime, genericDataDTO.getData());

            if (genericDataDTO.getData() instanceof Map) {
                Map<String, Object> dataMessage = (Map<String, Object>) genericDataDTO.getData();
                if (Objects.nonNull(dataMessage)) {
                    String cdrId = dataMessage.get("cdrID").toString();
                    if (!cdrId.isEmpty()) {
                        log.debug("Attempting to log off session for cdrId: {}", cdrId);
                        ResponseEntity<?> responseEntity = radiusClientService.logOffUserSession(Long.parseLong(cdrId), SoapConstants.MVNOID, token);
                        log.debug("Integration Received in: {}ms Response: {}", System.currentTimeMillis() - startTime, responseEntity.getBody());

                        if (responseEntity.getStatusCode().value() == HttpStatus.OK.value()) {
                            responsecode = SoapConstants.SUCCESS_CODE;
                            responseMessage = "LOGOUT session successfully";
                            result = true;
                            log.info("Successfully logged off session for user: {} ", userName);
                        } else {
                            responsecode = SoapConstants.SUCCESS_CODE;
                            responseMessage = "LOGOUT not happend due to some Techincal issue";
                            result = false;
                            log.warn("Failed to log off session for user: {} ", userName);
                        }
                    }
                } else {
                    responsecode = SoapConstants.SUCCESS_CODE;
                    responseMessage = "LOGOUT not happend due to some Techincal issue";
                    result = false;
                    log.warn("Data message is null for user: {}", userName);
                }
            } else {
                responsecode = SoapConstants.NOT_PRESENT;
                responseMessage = "Input Username session is not Online";
                result = false;
                log.info("No active session found for user: {} ", userName);
            }

            logoffUserSessionsResponse.setRequestId(requestId);
            logoffUserSessionsResponse.setResponseMessage(responseMessage);
            logoffUserSessionsResponse.setResponeCode(responsecode); // Fixed typo: 'responeCode' to 'responseCode'
            logoffUserSessionsResponse.setResult(result);
            wsLogoffUserSessionsResponse.setLogoffUserSessions(logoffUserSessionsResponse);
        } catch (Exception e) {
            logoffUserSessionsResponse.setRequestId(requestId);
            logoffUserSessionsResponse.setResponseMessage(responseMessage);
            logoffUserSessionsResponse.setResponeCode(responsecode);
            logoffUserSessionsResponse.setResult(result);
            wsLogoffUserSessionsResponse.setLogoffUserSessions(logoffUserSessionsResponse);
            log.error("Error processing logout request for user: {} with requestId: {}. Error: {}", userName, requestId, e.getMessage(), e);
        }
        log.info("Method getWsLogOffUserSession completed in: {}MS for username: {}", System.currentTimeMillis() - startTime, request.getUserName());
        return wsLogoffUserSessionsResponse;
    }

    /*
    public DOMSource generateLogoffUserSessionsSOAPResponse(WsLogoffUserSessionsResponse response) throws SOAPException, ParserConfigurationException {
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
        SOAPElement responseElement = body.addChildElement("wsLogoffUserSessionsResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add LogoffUserSessions element
        SOAPElement logoffUserSessionsElement = responseElement.addChildElement("LogoffUserSessions");

        // Add child elements to LogoffUserSessions
        logoffUserSessionsElement.addChildElement("requestId").addTextNode(getSafeText(response.getRequestId()));
        logoffUserSessionsElement.addChildElement("responeCode").addTextNode(getSafeNumber(response.getResponeCode()));
        logoffUserSessionsElement.addChildElement("responseMessage").addTextNode(getSafeText(response.getResponseMessage()));
        logoffUserSessionsElement.addChildElement("result").addTextNode(getSafeText(String.valueOf(response.isResult())));

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

    // Use this both success and Exception response generator method without interceptor
    // First test for checking sheet response and adjust parameter value by taking from response class

    /**
     * Creates a SOAP 1.1 response for LogoffUserSessions, handling both success and exception cases.
     *
     * @param response       The response object containing logoff user sessions details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for logoff user sessions.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateLogoffUserSessionsSOAP11SuccessAndExceptionResponse(WsLogoffUserSessionsResponse response, MessageContext messageContext) throws SOAPException {
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

        SOAPElement responseElement = body.addChildElement("wsLogoffUserSessionsResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement logoffUserSessions = responseElement.addChildElement("LogoffUserSessions");

        logoffUserSessions.addChildElement("requestId").addTextNode(response.getRequestId());
        logoffUserSessions.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        logoffUserSessions.addChildElement("responseMessage").addTextNode(response.getResponseMessage());
//        logoffUserSessions.addChildElement("result").addTextNode(String.valueOf(response.isResult()));

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
