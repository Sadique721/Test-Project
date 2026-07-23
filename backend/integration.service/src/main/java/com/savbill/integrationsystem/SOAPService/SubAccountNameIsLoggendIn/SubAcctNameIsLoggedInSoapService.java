package com.savbill.integrationsystem.SOAPService.SubAccountNameIsLoggendIn;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
public class SubAcctNameIsLoggedInSoapService {
    @Autowired
    RadiusClientService radiusClientService;

    public DOMSource handleSubAcctLoggedInRequest(@RequestPayload SubAcctNameIsLogged request, MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Starting handleSubAcctLoggedInRequest At: {}", new Date(startTime));

        try {
            Long mvnoId = SoapConstants.MVNOID;
            String username = request.getString_1().trim();
            log.debug("Processing request for username: {} with mvnoId: {}", username, mvnoId);

            if (username == null || StringUtils.isEmpty(username)) {
                log.warn("Empty or null username received in request");
                return generateSubAcctNameIsLoggedOnSOAP11ExceptionResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Username is Empty or Null",
                        "ecaaa1", messageContext
                );
            } else {
                log.debug("Calling radiusClientService.getSubAcctNameIsLoggedIn for username: {}", username);
                GenericDataDTO genericDataDTO = radiusClientService.getSubAcctNameIsLoggedIn(username, mvnoId);
                log.debug("Successfully retrieved in:{}MS login status for username: {}, response: {}", System.currentTimeMillis()-startTime,username, genericDataDTO.getResponseMessage());
                return generateSubAcctNameIsLoggedOnSOAP11SuccessResponse("subAcctNameIsLoggedOnResponse", genericDataDTO.getResponseMessage(), messageContext);
            }
        } catch (RuntimeException e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            log.error("Runtime exception occurred while processing request: {}", exceptionMessage, e);
            return generateSubAcctNameIsLoggedOnSOAP11ExceptionResponse(
                    "generalException",
                    "SQLException",
                    exceptionMessage,
                    "ecaaa1", messageContext
            );
        } catch (Exception e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            log.error("Exception occurred while processing request: {}", exceptionMessage, e);
            return generateSubAcctNameIsLoggedOnSOAP11ExceptionResponse(
                    "generalException",
                    "Exception",
                    exceptionMessage,
                    "ecaaa1", messageContext
            );
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("handleSubAcctLoggedInRequest completed in {} ms", (endTime - startTime));
        }
    }


    public static DOMSource getSuccessResponse(String getResp, String email) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        String namespaceNs1 = "http://npm.redback.com";
        String namespaceSoapEnv = "http://schemas.xmlsoap.org/soap/envelope/";
        String namespaceSoapEnc = "http://schemas.xmlsoap.org/soap/encoding/";
        String namespaceXsi = "http://www.w3.org/2001/XMLSchema-instance";
        String namespaceXsd = "http://www.w3.org/2001/XMLSchema";
        envelope.addNamespaceDeclaration("ns1", namespaceNs1);
        envelope.addNamespaceDeclaration("soapenv", namespaceSoapEnv);
        envelope.addNamespaceDeclaration("soapenc", namespaceSoapEnc);
        envelope.addNamespaceDeclaration("xsi", namespaceXsi);
        envelope.addNamespaceDeclaration("xsd", namespaceXsd);


        SOAPBody body = envelope.getBody();

        SOAPElement getSubAcctNameResponse = body.addChildElement(getResp, "ns1", "http://npm.redback.com");
        getSubAcctNameResponse.setEncodingStyle(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        SOAPElement result = getSubAcctNameResponse.addChildElement("result");
        result.setAttribute("href", "#id0");


        // Create multiRef element
        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;

        // Add attributes in the desired order
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle",
                SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        Document documentmuli = multiRefElement.getOwnerDocument();
        org.w3c.dom.Text textNode = documentmuli.createTextNode(email);
        multiRefElement.appendChild(textNode);

        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
//
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DocumentFragment as a DOMSource
        return new DOMSource(fragment);
    }

    /**
     * Creates a SOAP 1.1 success response with custom response data and success status.
     *
     * @param setFaultChildName Custom data to include in the response.
     * @param isSuccess         Indicates success status ("true" or "false").
     * @param messageContext    Optional MessageContext to update with the response.
     * @return DOMSource      SOAP success response as a DOMSource.
     * @throws SOAPException If an error occurs during message creation.
     */
    public static DOMSource generateSubAcctNameIsLoggedOnSOAP11SuccessResponse(String setFaultChildName, String isSuccessOrNot, MessageContext messageContext) throws SOAPException {
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
            header.detachNode(); // Remove the header if any
        }

        // Add Fault Element
        SOAPElement faultElement = body.addChildElement(setFaultChildName, "ns1", "http://npm.redback.com");
        faultElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");

        // Add result element to the fault element
        SOAPElement result = faultElement.addChildElement("result");
        result.addAttribute(new QName("href"), "#id0");

        // Add multiRef element for additional data (often used for complex types or references)
        SOAPElement multiRefElement = body.addChildElement("multiRef");

        // Add attributes in the desired order
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi"), "xsd:boolean");

        // Add required namespaces for multiRef
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");

        // Set the value of multiRef to "true"
        if (isSuccessOrNot.equalsIgnoreCase(SoapConstants.USER_NOT_AVAILABLE)) {
            multiRefElement.addTextNode("false");
        } else {
            multiRefElement.addTextNode(isSuccessOrNot);
        }

        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // If the MessageContext is provided, update the response
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

    /**
     * Creates a SOAP 1.1 fault response with custom fault code, exception details, and host info.
     *
     * @param faultCode        Fault code to include in the response (e.g., "Server").
     * @param exceptionName    Name of the exception (e.g., "CustomException").
     * @param exceptionMessage Exception message describing the error.
     * @param host             Host where the fault occurred.
     * @param messageContext   Optional MessageContext to update with the response.
     * @return DOMSource       SOAP fault response as a DOMSource.
     * @throws SOAPException If an error occurs during message creation.
     */
    public static DOMSource generateSubAcctNameIsLoggedOnSOAP11ExceptionResponse(String faultCodeString, String exceptionNameString,
                                                                                 String exceptionMessage, String host, MessageContext
                                                                                         messageContext) throws SOAPException {
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

        // Set the envelope prefix to "soapenv"
        envelope.setPrefix("soapenv");

        // Set up the Body and remove any pre-existing header if present
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode(); // Remove the header if it exists
        }

        // Add the Fault element to the SOAP body
        SOAPElement faultElement = body.addChildElement("Fault", "soapenv");

        // Add the fault code to the fault element
        SOAPElement faultcode = faultElement.addChildElement("faultcode");
        faultcode.addTextNode("soapenv:Server." + faultCodeString);

        // Add the faultstring element (typically provides a message, left empty here)
        SOAPElement faultstring = faultElement.addChildElement("faultstring");
        faultstring.addTextNode(""); // You can customize this message if needed

        // Add the Detail element, which contains more specific error information
        SOAPElement detail = faultElement.addChildElement("detail");

        // Add the SQLException reference (this example uses a custom namespace)
        SOAPElement sqlException = detail.addChildElement(exceptionNameString, "ns1", "http://npm.redback.com");
        sqlException.addAttribute(new QName("href"), "#id0");

        // Add exception name element with a namespace (usefully indicates the class name of the exception)
        SOAPElement exceptionName = detail.addChildElement("exceptionName", "ns2", "http://xml.apache.org/axis/");
        exceptionName.addTextNode("com.redback.npm." + exceptionNameString);

        // Add hostname element indicating where the exception occurred
        SOAPElement hostname = detail.addChildElement("hostname", "ns3", "http://xml.apache.org/axis/");
        hostname.addTextNode(host);

        // Add a multiRef element (for serialization or further references)
        SOAPElement multiRefElement = body.addChildElement("multiRef");

        // Set attributes for multiRef to indicate a specific type and reference
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "ns4:" + exceptionNameString);

        // Add required namespaces for multiRef
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com");

        // Add the exception message element with the exception message
        SOAPElement message = multiRefElement.addChildElement("message");
        message.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi"), "xsd:string");
        message.addTextNode(exceptionMessage);

        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // If the MessageContext is provided, update the response with the generated SOAP message
        if (messageContext != null) {
            SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
            updateResponse.setSaajMessage(soapMessage);
            updateResponse.getSaajMessage().saveChanges();
        }

        // Convert the SOAP body to DOMSource for the response
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();

        // Clone child nodes from the body and append them to the fragment
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the response as a DOMSource
        return new DOMSource(fragment);
    }

}
