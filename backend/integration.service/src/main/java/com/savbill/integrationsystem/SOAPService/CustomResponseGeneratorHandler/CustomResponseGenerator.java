package com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler;

import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;

public class CustomResponseGenerator {

    public static Source createCustomResponse(boolean isLoggedOn) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // Important!
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        // Create SOAP Body as the root
        Element soapBody = document.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
        soapBody.setAttribute("xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        soapBody.setAttribute("xmlns:xsi", SoapConstants.XSI_NAMESPACE); // Declare xsi namespace
        soapBody.setAttribute("xmlns:xsd", SoapConstants.XSD_NAMESPACE); // Declare xsd namespace
        soapBody.setAttribute("xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE); // Declare soapenc namespace
        document.appendChild(soapBody);

        // Create Response Element
        Element response = document.createElementNS("http://npm.redback.com", "ns1:subAcctNameIsLoggedOnResponse");
        response.setAttribute("xmlns:ns1", "http://npm.redback.com");
        response.setAttribute("soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        soapBody.appendChild(response);

        // Create Result Element
        Element result = document.createElement("result");
        result.setAttribute("href", "#id0");
        response.appendChild(result);

        // Create MultiRef Element
        Element multiRef = document.createElement("multiRef");
        multiRef.setAttribute("id", "id0");
        multiRef.setAttribute("soapenc:root", "0");
        multiRef.setAttribute("soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRef.setAttribute("xsi:type", "xsd:boolean"); // Uses xsi prefix
        multiRef.setTextContent(String.valueOf(isLoggedOn));
        soapBody.appendChild(multiRef);

        return new DOMSource(document);
    }


    public static DOMSource getExceptionInResponse(String faultCodeString, String exceptionNameString, String exceptionMessage, String host) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Declare the required namespaces explicitly at the envelope level
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/encoding/");
        envelope.addNamespaceDeclaration("soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        // Create SOAP body
        SOAPBody body = envelope.getBody();
        SOAPFault fault = body.addFault();

        // Set fault code
        fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server." + faultCodeString, "soapenv"));

        // Set empty fault string
        fault.setFaultString("");

        // Create detail element
        Detail detail = fault.addDetail();

        // Add InvalidIPAddressException element
        DetailEntry invalidIPAddressException = detail.addDetailEntry(
                new QName("http://npm.redback.com", exceptionNameString, "ns1"));
        invalidIPAddressException.addAttribute(new QName("href"), "#id0");

        // Add exceptionName element
        DetailEntry exceptionName = detail.addDetailEntry(
                new QName("http://xml.apache.org/axis/", "exceptionName", "ns2"));
        exceptionName.addTextNode("com.redback.npm." + exceptionNameString);

        // Add hostname element
        DetailEntry hostname = detail.addDetailEntry(
                new QName("http://xml.apache.org/axis/", "hostname", "ns3"));
        hostname.addTextNode(host);

        // Manually create the multiRef element
        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;

        // Add attributes in the desired order
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle",
                SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRefElement.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "ns4:"+exceptionNameString);

        // Add namespaces in the required order
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        // Add the message element
        SOAPElement message = multiRef.addChildElement("message");
        message.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
        message.addTextNode(exceptionMessage);

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




    public static DOMSource getSuccessResponse(String getResp, String email) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");


        SOAPBody body = envelope.getBody();

        SOAPElement getSubAcctNameResponse = body.addChildElement(getResp, "ns1", "http://npm.redback.com");
        getSubAcctNameResponse.setEncodingStyle(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        SOAPElement result = getSubAcctNameResponse.addChildElement("result");
        result.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
        result.addTextNode(email);

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

//    public static <responseData> DOMSource genrateLogoffSubSessionsSuccessResponse(String responseData)throws SOAPException, ParserConfigurationException {
//        MessageFactory factory = MessageFactory.newInstance();
//        SOAPMessage soapMessage = factory.createMessage();
//        SOAPPart soapPart = soapMessage.getSOAPPart();
//        SOAPEnvelope envelope = soapPart.getEnvelope();
//
//        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
//        String namespaceNs1 = "http://npm.redback.com";
//        String namespaceSoapEnv = "http://schemas.xmlsoap.org/soap/envelope/";
//        String namespaceSoapEnc = "http://schemas.xmlsoap.org/soap/encoding/";
//        String namespaceXsi = "http://www.w3.org/2001/XMLSchema-instance";
//        String namespaceXsd = "http://www.w3.org/2001/XMLSchema";
//
//        envelope.addNamespaceDeclaration("ns1", namespaceNs1);
//        envelope.addNamespaceDeclaration("soapenv", namespaceSoapEnv);
//        envelope.addNamespaceDeclaration("soapenc", namespaceSoapEnc);
//        envelope.addNamespaceDeclaration("xsi", namespaceXsi);
//        envelope.addNamespaceDeclaration("xsd", namespaceXsd);
//
//        SOAPBody body = envelope.getBody();
//
//        // Create the main response element
//        SOAPElement responseElement = body.addChildElement("logoffSubSessionsResponse", "ns1");
//        responseElement.setEncodingStyle(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
//
//        // Add the result element with href attribute
//        SOAPElement resultElement = responseElement.addChildElement("result");
//        resultElement.setAttribute("href", "#id0");
//
//
//        // Create multiRef element
//        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
//        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;
//
//        // Add attributes in the desired order
//        multiRefElement.setAttribute("id", "id0");
//        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
//        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle",
//                SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
////        multiRefElement.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "ns4:
////        ");
//
//        // Add namespaces in the required order
////        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com");
////        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
//
//        // Add the message element
//        SOAPElement message = multiRef.addChildElement("message");
//        message.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:boolean");
//        message.addTextNode("true");
//
//
//        soapMessage.saveChanges();
//        // Save and print the SOAP Message
//        Document document = body.getOwnerDocument();
//        DocumentFragment fragment = document.createDocumentFragment();
////
//        NodeList childNodes = body.getChildNodes();
//        for (int i = 0; i < childNodes.getLength(); i++) {
//            fragment.appendChild(childNodes.item(i).cloneNode(true));
//        }
//
//        return new DOMSource(fragment);
//    }

    /**
     * Generates a SOAP 1.1 response for a logoff sub-sessions operation.
     * This method creates a SOAP message with a custom namespace, sets up the SOAP Body and Fault
     * element, and returns the response as a DOMSource object.
     *
     * @param responseData The response data to be added to the SOAP body, usually representing an operation result.
     * @param messageContext The MessageContext containing the response, if any, to be updated.
     *                      This can be null if no update is needed.
     * @return A DOMSource representing the SOAP 1.1 response message.
     * @throws SOAPException If there is an error in creating or processing the SOAP message.
     */
    public static DOMSource generateSoap11LogoffSubSessionsResponse(String responseData, MessageContext messageContext) throws SOAPException {
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
        SOAPElement faultElement = body.addChildElement(responseData, "ns1", "http://npm.redback.com");
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
        multiRefElement.addTextNode("true");

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
     * Creates a SOAP fault response message based on the provided fault information.
     * This method generates a SOAP message containing the fault code, exception name,
     * exception message, and additional details such as the hostname.
     *
     * @param faultCodeString  The fault code (e.g., "Server.InvalidRequest").
     * @param exceptionNameString  The name of the exception (e.g., "MyCustomException").
     * @param exceptionMessage  A descriptive message for the exception.
     * @param host  The hostname where the exception occurred.
     * @param messageContext  The MessageContext to update with the generated SOAP response.
     * @return DOMSource  A DOMSource representing the SOAP fault response body.
     * @throws SOAPException  If there is an issue generating the SOAP message.
     * @throws IOException  If there is an issue with I/O operations during message creation.
     */
    public static DOMSource createSOAP11FaultResponse(
            String faultCodeString,
            String exceptionNameString,
            String exceptionMessage,
            String host,
            MessageContext messageContext
    ) throws SOAPException, IOException {
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

    public static String getSafeText(String text) {
        return text != null ? text : "";
    }

    public static String getSafeNumber(Integer number) {
        return number != null ? String.valueOf(number) : "";
    }
    public static String getSafeNumberDouble(Double number) {
        return number != null ? String.valueOf(number) : "";
    }
    public static String getSafeNumberLong(Long number) {
        return number != null ? String.valueOf(number) : "";
    }
    public static String getSafeBoolean(Boolean bool) {
        return bool != null ? String.valueOf(bool) : "";
    }

}
