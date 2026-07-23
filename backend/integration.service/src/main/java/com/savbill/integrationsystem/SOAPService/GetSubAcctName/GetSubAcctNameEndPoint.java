package com.savbill.integrationsystem.SOAPService.GetSubAcctName;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;

import com.savbill.integrationsystem.generated.getsubacctname.GetSubAcctName;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.micrometer.core.instrument.util.StringUtils;
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

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;

import java.util.Date;

@Slf4j
@Endpoint
public class GetSubAcctNameEndPoint {

    @Autowired
    private RadiusClientService radiusClientService;

    @Autowired
    private JwtUtil jwtUtil;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "getSubAcctName")
    @ResponsePayload
    public DOMSource handleRequest(@RequestPayload GetSubAcctName request, MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Starting handleRequest for getSubAcctName with At: {}", new Date(startTime));

        ObjectMapper objectMapper = new ObjectMapper();
        String responseData = null;
        GenericDataDTO genericDataDTO = null;
        String ipAddress = request.getString1().trim();
        log.debug("Processing request for IP address: {}", ipAddress);

        try {
            if (ipAddress == null || StringUtils.isEmpty(ipAddress)) {
                log.warn("Empty or null IP address received in request");
                return generateGetSubSessionIsLoggedOnSOAP11InvalidIpResponse(
                        "generalException",
                        "InvalidIPAddressException",
                        "Input IP Address is Empty or Null",
                        "ecaaa1", messageContext
                );
            }
            if (!isValidIPAddress(ipAddress)) {
                log.warn("Invalid IP address format: {}", ipAddress);
                return generateGetSubSessionIsLoggedOnSOAP11InvalidIpResponse(
                        "generalException",
                        "InvalidIPAddressException",
                        "Invalid IP Address",
                        "ecaaa1", messageContext
                );
            }
            if (!ipAddress.isEmpty()) {
                log.debug("Calling radiusClientService.GetAccountNameApi for IP: {}", ipAddress);
                genericDataDTO = radiusClientService.GetAccountNameApi(ipAddress, SoapConstants.MVNOID);
                log.debug("Received response from Radius In:{}MS, Response:{}", System.currentTimeMillis() - startTime, genericDataDTO.getData());
                if (genericDataDTO.getData() == null) {
                    log.warn("IP Address not found in Session Table: {}", ipAddress);
                    return generateGetSubSessionIsLoggedOnSOAP11InvalidIpResponse(
                            "generalException",
                            "InvalidIPAddressException",
                            "Input IP Address not found in Session Table",
                            "ecaaa1", messageContext
                    );
                } else if (genericDataDTO.getData() != null && SoapConstants.UNKNOWN_DATA.equals(genericDataDTO.getData())) {    //check if UNKNOWN username
                    log.warn("Unknown username found for IP: {}", ipAddress);
                    responseData = "Invalid IP Address";
                    return generateGetSubSessionIsLoggedOnSOAP11InvalidIpResponse(
                            "generalException",
                            "InvalidIPAddressException",
                            responseData,
                            "ecaaa1", messageContext
                    );
                } else {
                    responseData = genericDataDTO.getData() != null ? genericDataDTO.getData().toString() : null;
                    log.info("Successfully retrieved account name for IP: {}", genericDataDTO.getData());
                    return generateGetSubSessionIsLoggedOnSOAP11SuccessResponse("getSubAcctNameResponse", responseData, messageContext);
                }
            }
            log.warn("Empty IP address after validation: {}", ipAddress);
            return generateGetSubSessionIsLoggedOnSOAP11InvalidIpResponse(
                    "generalException",
                    "InvalidIPAddressException",
                    "Invalid IP Address",
                    "ecaaa1", messageContext
            );

        } catch (FeignException e) {
            log.error("FeignException occurred while processing IP: {}", ipAddress, e);
            return generateGetSubSessionIsLoggedOnSOAP11InvalidIpResponse(
                    e.getClass().getSimpleName(),
                    "RemoteException",
                    e.getMessage(),
                    "ecaaa1", messageContext
            );
        } catch (Exception e) {
            log.error("Unexpected exception occurred while processing IP: {}", ipAddress, e);
            return generateGetSubSessionIsLoggedOnSOAP11InvalidIpResponse(
                    e.getClass().getSimpleName(),
                    "Exception",
                    e.getMessage(),
                    "ecaaa1", messageContext
            );
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("handleRequest completed in {} ms for IP: {}",
                    (endTime - startTime), ipAddress);
        }
    }

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }

    /**
     * Generates a SOAP 1.1 success response for the "GetSubSessionIsLoggedOn" operation.
     * <p>
     * //     * @param childName        The name of the child element to include in the response.
     *
     * @param responseData   The data to populate within the response's result element.
     * @param messageContext MessageContext to update with the generated SOAP message.
     * @return DOMSource       The generated SOAP success response as a DOMSource.
     * @throws SOAPException If an error occurs during SOAP message creation.
     */
    public DOMSource generateGetSubSessionIsLoggedOnSOAP11SuccessResponse(String setChildName, String responseData, MessageContext messageContext) throws SOAPException {
        // Create a SOAP message
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove default SOAP namespace and add custom namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.setPrefix("soapenv");

        // Get the SOAP Body and remove any existing Fault element
        SOAPBody body = envelope.getBody();
        if (body.getFault() != null) {
            body.removeChild(body.getFault());
        }
        body.setPrefix("soapenv");

        // Remove the SOAP Header if it exists
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Create the response structure with a result
        SOAPElement faultElement = body.addChildElement("getSubAcctNameResponse", "ns1", "http://npm.redback.com");
        faultElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");

        // Create the result element and set its value and xsi:type
        SOAPElement result = faultElement.addChildElement("result");
        result.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "xsd:string");
        result.addTextNode(responseData);

        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // Update the response in the MessageContext
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Extract the response document and return it as a DOMSource
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    /**
     * Generates a SOAP 1.1 fault response for invalid IP cases with custom fault details.
     * <p>
     * //     * @param faultCode        The fault code string to include in the SOAP fault response.
     * //     * @param exceptionName    The name of the exception to include in the detail element.
     *
     * @param exceptionMessage The exception message to include in the multiRef element.
     * @param host             The hostname where the exception occurred.
     * @param messageContext   Optional MessageContext to update with the generated SOAP message.
     * @return DOMSource       SOAP fault response as a DOMSource.
     * @throws SOAPException If an error occurs during SOAP message creation.
     */
    public DOMSource generateGetSubSessionIsLoggedOnSOAP11InvalidIpResponse(String faultCodeString, String exceptionNameString, String exceptionMessage, String host, MessageContext messageContext) throws SOAPException {
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
