package com.savbill.integrationsystem.SOAPService.subSessionIsLoggedOn;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClient;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;

import java.util.Date;
import java.util.concurrent.TimeoutException;

import com.savbill.integrationsystem.generated.subsessionisloggedon.SubSessionIsLoggedOn;
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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Endpoint
public class WsSubSessionIsLoggedOnEndPoint {
    @Autowired
    private RadiusClient radiusClient;
    @Autowired
    private RadiusClientService radiusClientService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SubsessionIdLoggedOnService subsessionIdLoggedOnService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "subSessionIsLoggedOn")
    @ResponsePayload
    public DOMSource WsSubSessionIsLoggedOnEndPoint(@RequestPayload SubSessionIsLoggedOn request, MessageContext messageContext) throws Exception {
        long methodStartTime = System.currentTimeMillis();
        log.info("Starting WsSubSessionIsLoggedOnEndPoint At: {}", new Date(methodStartTime));

        // Validate input
        /**
         * Check one more condition of sql Exception I have created response for sql error, use by requirement
         */
        log.info("Received request to check session login status for IP Address: {}", request.getString1());
        String ipAddress = request.getString1().trim();

        if (ipAddress == null || ipAddress.isEmpty()) {
            log.warn("Empty or null IP address received in request");
            log.info("WsSubSessionIsLoggedOnEndPoint completed in {} ms for IP: {}",
                    (System.currentTimeMillis() - methodStartTime), ipAddress);
            return generateInvalidIPAddressSOAP11ErrorResponse(
                    "generalException",
                    "InvalidIPAddressException",
                    "Input IP Address is Empty or Null",
                    "ecaaa1", messageContext
            );
        }

        try {
            if (!isValidIPAddress(ipAddress)) {
                log.info("Invalid login session for given IP Address: {}", ipAddress);
                return generateInvalidIPAddressSOAP11ErrorResponse(
                        "generalException",
                        "InvalidIPAddressException",
                        "Invalid Login session for given IP address",
                        "ecaaa1", messageContext
                );
            }
            log.debug("Call Radius Client To Check live user for IP: {}", ipAddress);
            GenericDataDTO checkLiveuseer = radiusClientService.CheckLiveUser(ipAddress, SoapConstants.MVNOID);
            log.debug("Radius client service call completed in {} ms , Liveuser: {}", (System.currentTimeMillis() - methodStartTime), checkLiveuseer);


            log.debug("Call Radius Client for Getting user session for IP: {}", ipAddress);
            GenericDataDTO userSession = radiusClientService.GetUserSessionApi(ipAddress, SoapConstants.MVNOID);
            Long endTime = System.currentTimeMillis();
            log.debug("Radius client service call completed in {} ms , userSession Response: {}", (endTime - methodStartTime), userSession);

            if (userSession.getData() instanceof Map) {
                log.debug("Processing user session map for IP: {}", ipAddress);
                Map<String, Object> attributes = (Map<String, Object>) userSession.getData();
                if (attributes.entrySet().stream()
                        .anyMatch(entry -> entry.getKey().equalsIgnoreCase("knownUser") && Boolean.FALSE.equals(entry.getValue()))) {
                   /* return generateInvalidIPAddressSOAP11ErrorResponse(
                            "generalException",
                            "InvalidIPAddressException",
                            SoapConstants.IP_NOT_AVAILABLE_IN_SESSION_OR_UNKOWN,
                            "ecaaa1",messageContext
                    );*/
                    log.info("Unknown user detected for IP: {}", ipAddress);
                    return generateSubSessionIsLoggedInOrNotSOAP11SuccessResponse(messageContext, false);
                }
                log.debug("Checking user session validity for IP: {}", ipAddress);
                boolean isUserSessionValid = subsessionIdLoggedOnService.checkUserSession(attributes, ipAddress);

                if (isUserSessionValid) {
                    log.debug("Validating user session in radius client for IP: {}", ipAddress);
                    GenericDataDTO radiusCheck = radiusClientService.checkUserSessionInRadiusClient(ipAddress, SoapConstants.MVNOID);
                    if (radiusCheck.getData() != null && "true".equalsIgnoreCase(radiusCheck.getData().toString())) {
                        log.warn("Invalid session found in radius client for IP Address: {}", ipAddress);
                        return generateInvalidIPAddressSOAP11ErrorResponse(
                                "generalException",
                                "InvalidIPAddressException",
                                radiusCheck.getResponseMessage(),
                                "ecaaa1", messageContext
                        );
                    }
                    log.info("Valid user session confirmed for IP: {}", ipAddress);
                    return generateSubSessionIsLoggedInOrNotSOAP11SuccessResponse(messageContext, isUserSessionValid);
                }
                log.info("User session is not valid for IP Address: {}", ipAddress);
                return generateSubSessionIsLoggedInOrNotSOAP11SuccessResponse(messageContext, isUserSessionValid);
            } else {
                if (checkLiveuseer != null &&
                        checkLiveuseer.getResponseMessage() != null &&
                        checkLiveuseer.getResponseMessage().equalsIgnoreCase("IP is not available in session table")) {
                    log.warn("IP not found in session table: {}", ipAddress);
                    return generateInvalidIPAddressSOAP11ErrorResponse(
                            "generalException",
                            "InvalidIPAddressException",
                            "IP is not available in session table",
                            "ecaaa1", messageContext
                    );
                }
                //                if (checkLiveuseer.getResponseCode() == SoapConstants.NOT_FOUND) {
                log.info("Returning false for session status, IP: {}", ipAddress);
                return generateSubSessionIsLoggedInOrNotSOAP11SuccessResponse(messageContext, false);
//                }

            }
//            return getExceptionInResponse(
//                    "generalException",
//                    "InvalidIPAddressException",
//                    "IP is not available in session table",
//                    "ecaaa1"
//            );
        } catch (TimeoutException e) {
            log.error("Timeout occurred while processing request for IP Address: {}", ipAddress, e);
            return generateInvalidIPAddressSOAP11ErrorResponse(
                    "generalException",
                    "SQLException",
                    "Timeout occurred while processing the request",
                    "ecaaa1", messageContext
            );
        } catch (RuntimeException e) {
            log.error("Unexpected error occurred while processing request for IP Address: {}", ipAddress, e);
            return generateInvalidIPAddressSOAP11ErrorResponse(
                    "generalException",
                    "SQLException",
                    "An unexpected error occurred",
                    "ecaaa1", messageContext
            );
        } finally {
            long methodEndTime = System.currentTimeMillis();
            log.info("WsSubSessionIsLoggedOnEndPoint completed in {} ms for IP: {}",
                    (methodEndTime - methodStartTime), ipAddress);
        }
    }


    public DOMSource getSubSessionIsLoggedOnSuccess(Boolean booleanExpression) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPHeader header = envelope.getHeader();
//        if (header != null) {
//            header.detachNode();
//        }
//         Declare the required namespaces explicitly at the envelope level
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/encoding/");
        envelope.addNamespaceDeclaration("soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
//        envelope.removeChild(envelope.getHeader());

        // Create SOAP body
        SOAPBody body = envelope.getBody();
        if (body.getFault() != null) {
            body.removeChild(body.getFault());
        }
        SOAPElement sessionIsLogged = body.addChildElement(new QName("", "ns1:subSessionIsLoggedOnResponse"));
        Element element = sessionIsLogged;
        element.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle",
                SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns1", "http://npm.redback.com");
        SOAPElement result = sessionIsLogged.addChildElement("result");
        Element element1 = result;
        element1.setAttribute("href", "#id0");
        // Manually create the multiRef element
//        removeNamespace(sessionIsLogged, "soapenv");

        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;

        // Add attributes in the desired order
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle",
                SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRefElement.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "xsd:boolean");
        // Add namespaces in the required order
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc",
                SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRefElement.setTextContent(String.valueOf(booleanExpression));
        // Add the message element
//        removeNamespace(multiRef, "soapenv");


        // Save changes and verify structure
        soapMessage.saveChanges();

        // Convert body to DOMSource for return
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }
        return new DOMSource(fragment);
    }

    private void removeNamespace(SOAPElement element, String namespacePrefix) {
        try {
            Node node = element;
            if (node instanceof Element) {
                Element domElement = (Element) node;
                domElement.removeAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", namespacePrefix);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }


    private boolean isValidetIPAddress(String ip) {
        // IPv4 regex pattern
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";

        // IPv6 regex pattern (supports shorthand notation)
        String ipv6Pattern = "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$" +       // Full IPv6
                "|^(?:[0-9a-fA-F]{1,4}:){1,7}:$" +                  // Ending with "::"
                "|^:(:[0-9a-fA-F]{1,4}){1,7}$" +                    // Starting with "::"
                "|^(?:[0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}$" +  // "::" in the middle
                "|^::1$|^::$";                                      // Loopback "::1" or "::"

        // Return true if the input matches either IPv4 or IPv6 pattern
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }


    /**
     * Generates a SOAP 1.1 response indicating the login status of a sub-session.
     *
     * @param messageContext The message context containing response information.
     * @return DOMSource containing the generated SOAP response.
     * @throws SOAPException If an error occurs while creating the SOAP message.
     */
    public DOMSource generateSubSessionIsLoggedInOrNotSOAP11SuccessResponse(MessageContext messageContext, boolean isUserSessionValid) throws SOAPException {
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.removeNamespaceDeclaration("SOAP-ENV"); // Remove default SOAP namespace
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.setPrefix("soapenv");
        SOAPBody body = envelope.getBody();
        if (body.getFault() != null) {
            body.removeChild(body.getFault());
        }
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        SOAPElement faultElement = body.addChildElement("subSessionIsLoggedOnResponse", "ns1", "http://npm.redback.com");
        faultElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        SOAPElement result = faultElement.addChildElement("result");
        result.addAttribute(new QName("href"), "#id0");
        SOAPElement multiRefElement = body.addChildElement("multiRef");
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi"), "xsd:boolean");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.addTextNode(String.valueOf(isUserSessionValid));
        soapMessage.saveChanges();
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
     * Generates a SOAP 1.1 error response for an invalid IP address, including exception details.
     *
     * @param faultCodeString     The fault code string representing the error type.
     * @param exceptionNameString The name of the exception being thrown.
     * @param exceptionMessage    The message related to the exception.
     * @param host                The host where the error occurred.
     * @param messageContext      The message context containing the response information.
     * @return DOMSource containing the generated SOAP error response.
     * @throws SOAPException If an error occurs while creating the SOAP message.
     * @throws IOException   If an I/O error occurs during the process.
     */
    public static DOMSource generateInvalidIPAddressSOAP11ErrorResponse(String faultCodeString, String exceptionNameString, String exceptionMessage, String host, MessageContext messageContext) throws SOAPException, IOException {
        // Create a SOAP 1.1 Message factory
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove default SOAP namespaces and declare custom namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        envelope.setPrefix("soapenv");

        // Set the Body and Header
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Add Fault Element
        SOAPElement faultElement = body.addChildElement("Fault", "soapenv");
        SOAPElement faultcode = faultElement.addChildElement("faultcode");
        faultcode.addTextNode("soapenv:Server." + faultCodeString);

        SOAPElement faultstring = faultElement.addChildElement("faultstring");
        faultstring.addTextNode("");

        // Add Detail element
        SOAPElement detail = faultElement.addChildElement("detail");

        // Add SQLException reference
        SOAPElement sqlException = detail.addChildElement(exceptionNameString, "ns1", "http://npm.redback.com");
        sqlException.addAttribute(new QName("href"), "#id0");
        // Add exceptionName element
        SOAPElement exceptionName = detail.addChildElement("exceptionName", "ns2", "http://xml.apache.org/axis/");
        exceptionName.addTextNode("com.redback.npm." + exceptionNameString);

        // Add hostname element
        SOAPElement hostname = detail.addChildElement("hostname", "ns3", "http://xml.apache.org/axis/");
        hostname.addTextNode(host);
        // Add multiRef element
        SOAPElement multiRefElement = body.addChildElement("multiRef");

        // Set attributes in the exact order required
        multiRefElement.setAttribute("id", "id0"); // First
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0"); // Second
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/"); // Third
        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "ns4:" + exceptionNameString); // Fourth

        // Add xmlns:soapenc first
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/"); // Fifth
        // Add xmlns:ns4 next
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com"); // Sixth

        // Add the message child element
        SOAPElement message = multiRefElement.addChildElement("message");
        message.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "xsd:string");
        message.addTextNode(exceptionMessage);

        // Save changes to the SOAP message
        soapMessage.saveChanges();
        if (messageContext != null) {
            SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
            updateResponse.setSaajMessage(soapMessage);
            updateResponse.getSaajMessage().saveChanges();
        }

        // Convert the SOAP body to DOMSource for the response
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the response as a DOMSource
        return new DOMSource(fragment);
    }


}
