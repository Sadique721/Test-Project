package com.savbill.integrationsystem.SOAPService.logoffSubSessionsService;

import com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.soap.SOAPException;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Slf4j
@Endpoint
public class LogoffSubSessionsEndpoint {
    @Autowired
    private RadiusClientService radiusClientService;

    @Autowired
    private JwtUtil jwtUtil;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "logoffSubSessions")
    @ResponsePayload
    public DOMSource getLogoffSubSessions(@RequestPayload LogoffSubSessions request, MessageContext messageContext) throws SOAPException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("Starting getLogoffSubSessions At: {}", new Date(startTime));

        String username = request.getString_1().trim();
        log.debug("Processing logoff request for username: {}", username);

        try {
            if (username == null || username.isEmpty()) {
                log.warn("Request parameter is null or empty");
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Input UserName is empty or null",
                        "ecaaa1", messageContext
                );
            }

            log.debug("Calling radiusClientService.LoggOffSubSessions for username: {}", username);
            GenericDataDTO radiusResponse = radiusClientService.LoggOffSubSessions(username, SoapConstants.MVNOID);
            log.debug("Integration Received Response in:{}Ms, Response:{}", System.currentTimeMillis() - startTime, radiusResponse.getResponseMessage());
            if (radiusResponse != null && radiusResponse.getData() instanceof Map) {
                Map<String, Object> radiusData = (Map<String, Object>) radiusResponse.getData();
                String userNameFromResponse = (String) radiusData.get("userName");

                if (userNameFromResponse == null && !username.equals(userNameFromResponse)) {
                    log.warn("Username mismatch or not found in session. Request: {}, Response: {}",
                            username, userNameFromResponse);
                    return CustomResponseGenerator.createSOAP11FaultResponse(
                            "generalException",
                            "InvalidIPAddressException",
                            "Input Username is UNKNOWN.",
                            "ecaaa2", messageContext
                    );
                }
            } else {
                log.warn("No active session data found for user: {}", username);
                return CustomResponseGenerator.createSOAP11FaultResponse(
                        "generalException",
                        "InvalidIPAddressException",
                        "Input Username is UNKNOWN.",
                        "ecaaa2", messageContext
                );
            }

            log.info("Successfully processed logoff request for username: {}", username);
            return CustomResponseGenerator.generateSoap11LogoffSubSessionsResponse("logoffSubSessionsResponse", messageContext);

        } catch (Exception e) {
            log.error("Exception occurred while processing logoff request for username: {}", username, e);
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "generalException",
                    "SQLException",
                    "SQL Exception",
                    "ecaaa1", messageContext
            );
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("getLogoffSubSessions completed in {} ms for username: {}",
                    (endTime - startTime), username);
        }
    }


//    public static DOMSource generateSOAPExceptionResponse(String faultCodeString, String exceptionNameString, String exceptionMessage, String host, MessageContext messageContext) throws SOAPException, IOException {
//        // Create a SOAP 1.1 Message factory
//        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
//        SOAPMessage soapMessage = factory.createMessage();
//        SOAPPart soapPart = soapMessage.getSOAPPart();
//        SOAPEnvelope envelope = soapPart.getEnvelope();
//
//        // Remove default SOAP namespaces and declare custom namespaces
//        envelope.removeNamespaceDeclaration("SOAP-ENV");
//        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
//        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
//        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
//
//        envelope.setPrefix("soapenv");
//
//        // Set the Body and Header
//        SOAPBody body = envelope.getBody();
//        body.setPrefix("soapenv");
//        SOAPHeader header = envelope.getHeader();
//        if (header != null) {
//            header.detachNode(); // Remove the header if any
//        }
//
//        // Add Fault Element
//        SOAPElement faultElement = body.addChildElement("Fault", "soapenv");
//        SOAPElement faultcode = faultElement.addChildElement("faultcode");
//        faultcode.addTextNode("soapenv:Server." + faultCodeString);
//
//        SOAPElement faultstring = faultElement.addChildElement("faultstring");
//        faultstring.addTextNode("");
//
//        // Add Detail element
//        SOAPElement detail = faultElement.addChildElement("detail");
//
//        // Add SQLException reference
//        SOAPElement sqlException = detail.addChildElement("SQLException","ns1", "http://npm.redback.com"); // Only prefix, no URI here
////        sqlException.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns1", "http://xml.apache.org/axis/");
//        sqlException.addAttribute(new QName("href"), "#id0");
//
//        // Add exceptionName element
//        SOAPElement exceptionName = detail.addChildElement("exceptionName", "ns2", "http://xml.apache.org/axis/");
////        exceptionName.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns2", "http://xml.apache.org/axis/");
//        exceptionName.addTextNode("com.redback.npm." + exceptionNameString);
//
//        // Add hostname element
//        SOAPElement hostname = detail.addChildElement("hostname", "ns3", "http://xml.apache.org/axis/");
////        hostname.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", "http://xml.apache.org/axis/");
//        hostname.addTextNode(host);
//
//        // Add multiRef element
//        SOAPElement multiRefElement = body.addChildElement("multiRef");
////        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;
//
//        // Add attributes in the desired order
//        multiRefElement.setAttribute("id", "id0");
//        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
//        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
//        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "ns4:" + exceptionNameString);
//
//        // Add required namespaces for multiRef
//        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
//        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com");
//
//        // Add message element with the exception message
//        SOAPElement message = multiRefElement.addChildElement("message");
//        message.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi"), "xsd:string");
//        message.addTextNode(exceptionMessage);
//
//        // Save changes to the SOAP message
//        soapMessage.saveChanges();
//
//        // If the MessageContext is provided, update the response
//        if (messageContext != null) {
//            SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
//            updateResponse.setSaajMessage(soapMessage);
//            updateResponse.getSaajMessage().saveChanges();
//        }
//
//        // Convert the SOAP body to DOMSource for the response
//        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
//        DocumentFragment fragment = document.createDocumentFragment();
//        NodeList childNodes = body.getChildNodes();
//        for (int i = 0; i < childNodes.getLength(); i++) {
//            fragment.appendChild(childNodes.item(i).cloneNode(true));
//        }
//
//        // Return the response as a DOMSource
//        return new DOMSource(fragment);
//    }
}
