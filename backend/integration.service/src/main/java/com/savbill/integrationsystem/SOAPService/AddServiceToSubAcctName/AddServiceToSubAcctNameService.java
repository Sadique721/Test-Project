package com.savbill.integrationsystem.SOAPService.AddServiceToSubAcctName;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccount;
import com.savbill.integrationsystem.generated.addservicetosubacctname.AddServiceToSubAcctName;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class AddServiceToSubAcctNameService {

    @Autowired
    private CmsClientService cmsClientService;

    @Autowired
    private JwtUtil jwtUtil;

    public DOMSource handleAddServiceToSubAcctRequest(AddServiceToSubAcctName request, MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Starting handleAddServiceToSubAcctRequest with username: {} At:{}", request.getString1().getValue(), new Date(startTime));

        try {
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);
            WsAddServiceToAccount wsAddServiceToAccount = new WsAddServiceToAccount();
            String userName = request.getString1().getValue().trim();
            // Validate input UserName
            if (userName.isEmpty()) {
                log.warn("Username validation failed: empty or null value");
                return getExceptionInResponse(
                        messageContext,
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Username is Empty or Null",
                        "ecaaa1"
                );
            }
            // Validate input ServiceId
            if (request.getString2().getValue().isEmpty()) {
                log.warn("ServiceId validation failed: empty or null value");
                return getExceptionInResponse(
                        messageContext,
                        "generalException",
                        "InvalidServiceSubscriptionException",
                        "Service ID is Empty or Null",
                        "ecaaa1"
                );
            }
            if (request.getString2().getValue() == "SUSPENDUSER") {
                log.info("handleAddServiceToSubAcctRequest completed IN:{}MS", System.currentTimeMillis() - startTime);
                return generateRauthSessionsSOAPResponse(messageContext);

            }

            if (userName != null && request.getString2().getValue().trim() != null) {
                wsAddServiceToAccount.setUserName(userName);
                wsAddServiceToAccount.setServiceId(request.getString2().getValue().trim());
                log.debug("Calling CMS client service with username: {} and serviceId: {}", userName, request.getString2().getValue().trim());
                ResponseEntity<?> responseEntity = cmsClientService.AddServiceToAccountAccount(wsAddServiceToAccount, mvnoId, token);
                Object responseData = responseEntity.getBody();
                log.debug("Integration Received Response IN:{}MS, responseData:{}", System.currentTimeMillis() - startTime, responseData);
                if (responseData instanceof LinkedHashMap) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> responseMap = (Map<String, Object>) responseData;
                    if (responseMap.containsKey("message") && responseMap.containsValue("Username Not available")) {
                        log.warn("Username not available in SPR table");
                        return getExceptionInResponse(
                                messageContext,
                                "generalException",
                                "InvalidSubscriberAccountException",
                                "Not Updated Record in SPR table due to Technical Issue",
                                "ecaaa1"
                        );
                    } else if (responseMap.containsKey("message") && responseMap.containsValue("ServiceId Not available")) {
                        log.warn("ServiceId not available in system");
                        return getExceptionInResponse(
                                messageContext,
                                "generalException",
                                "InvalidServiceSubscriptionException",
                                "Service ID is not available in System",
                                "ecaaa1"
                        );
                    } else if (responseMap.get("deActivateResponse") != null) {
                        log.info("Processing deActivateResponse");
                        return generateRauthSessionsSOAPResponse(messageContext);
                    }
                }
            }
            log.info("handleAddServiceToSubAcctRequest completed IN:{}MS", System.currentTimeMillis() - startTime);
            return null;

        } catch (SOAPException e) {
            log.error("SOAPException occurred: {}", e.getMessage(), e);
            return getExceptionInResponse(
                    messageContext,
                    e.getClass().getSimpleName(),
                    "SQLException",
                    e.getMessage(),
                    "ecaaa1"
            );
        } catch (Exception e) {
            log.error("Unexpected exception occurred: {}", e.getMessage(), e);
            return getExceptionInResponse(
                    messageContext,
                    e.getClass().getSimpleName(),
                    "Exception",
                    e.getMessage(),
                    "ecaaa1"
            );
        } finally {
            log.info("handleAddServiceToSubAcctRequest completed IN:{}MS", System.currentTimeMillis() - startTime);
        }
    }


    public DOMSource generateRauthSessionsSOAPResponse(MessageContext messageContext) throws SOAPException, IOException {

        SOAPMessage soapMessage = generateSuccessResponse();
        SaajSoapMessage response = (SaajSoapMessage) messageContext.getResponse();
        response.setSaajMessage(soapMessage);
        response.getSaajMessage().saveChanges();

        SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    public static SOAPMessage generateSuccessResponse() throws SOAPException {
        // Create a SOAP message with SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();

        // Remove default SOAP namespaces and add custom ones
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        // Set prefix for envelope and body
        envelope.setPrefix("soapenv");
        envelope.getBody().setPrefix("soapenv");

        // Remove header if it exists
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Create SOAP body and add the desired response element
        SOAPBody body = envelope.getBody();
        SOAPElement responseElement = body.addChildElement("addServiceToSubAcctNameResponse", "ns1", "http://npm.redback.com");
        responseElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");


        // Convert SOAPMessage to DOMSource
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();

        soapMessage.saveChanges(); // Save changes to the SOAP message

        return soapMessage;
    }

    public DOMSource getExceptionInResponse(MessageContext messageContext, String faultCode, String exceptionName, String errorMessage, String hostname) throws SOAPException {
        // Initialize the fragment to null to avoid returning an uninitialized value
        DocumentFragment fragment = null;

        try {
            // Generate the SOAPMessage exception response
            SOAPMessage soapMessage = getExceptionInResponse(faultCode, exceptionName, errorMessage, hostname);

            // Set the response in the message context as a SaajSoapMessage
            SaajSoapMessage response = (SaajSoapMessage) messageContext.getResponse();
            response.setSaajMessage(soapMessage);
            soapMessage.saveChanges();  // Saving changes for the SOAP message

            // Extract SOAP body and prepare DOMSource
            SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();
            Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();

            // Initialize the DocumentFragment
            fragment = document.createDocumentFragment();

            NodeList childNodes = body.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                fragment.appendChild(childNodes.item(i).cloneNode(true));
            }
        } catch (SOAPException | IOException e) {
            // Handle SOAPException and IOException
            e.printStackTrace();
            // Optionally, create a default SOAPMessage or return an empty DOMSource
        }

        if (fragment == null) {
            try {
                // Create a default empty Document using DocumentBuilderFactory
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.newDocument(); // Create a new empty document
                fragment = document.createDocumentFragment(); // Create a document fragment
            } catch (Exception e) {
                e.printStackTrace();
                // Optionally handle the error if needed
            }
        }
        // Wrap the fragment into a DOMSource and return
        return new DOMSource(fragment);
    }

    public static SOAPMessage getExceptionInResponse(String faultCodeString, String exceptionNameString, String exceptionMessage, String host) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();

        // Remove default SOAP namespaces and add custom ones
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        // Set prefix for envelope and body
        envelope.setPrefix("soapenv");
        envelope.getBody().setPrefix("soapenv");

        // Remove header if it exists
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

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

// Set the attributes, ensuring no unwanted namespaces are added
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRefElement.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "ns4:" + exceptionNameString);

// Add only the required namespaces (no soapenv here)
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/"); // Third

// Now add the message element
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

        return soapMessage; // Return the SOAPMessage
    }


}
