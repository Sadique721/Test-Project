package com.savbill.integrationsystem.SOAPService.SubscriberAccount;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.getsubscriberaccountxml.GetSubscriberAccountXML;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;


/**
 * This class acts as a SOAP endpoint for handling subscriber account-related requests.
 * It processes incoming SOAP requests, generates dynamic responses, and handles exceptions in a SOAP-compliant format.
 */
@Slf4j
@Endpoint
public class SubscriberAccountEndpoint {

    /**
     * Handles SOAP requests for subscriber account information.
     *
     * @param requestEnvelope the incoming SOAP request payload
     * @return a {@link DOMSource} containing the SOAP response
     * @throws SOAPException                if a SOAP error occurs
     * @throws ParserConfigurationException if a parser configuration error occurs
     * @throws TransformerException         if a transformation error occurs
     * @throws IOException                  if an I/O error occurs
     */
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    public CmsClient cmsClient;
    @Autowired
    private RadiusClientService radiusClientService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "getSubscriberAccountXML")
    @ResponsePayload
    public DOMSource handleSubscriberAccountRequest(@RequestPayload GetSubscriberAccountXML requestEnvelope, MessageContext messageContext) throws SOAPException, ParserConfigurationException, TransformerException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("Starting handleSubscriberAccountRequest with At: {}", new Date(startTime));

        DOMSource source = new DOMSource();
        String userName = requestEnvelope.getString1();
        userName = userName.toLowerCase().trim();
        log.debug("Processing request for username: {}", userName);

        try {
            if (userName == null || userName.isEmpty()) {
                log.warn("Empty or null username received in request");
                return getExceptionInResponse("generalException",
                        "InvalidSubscriberAccountException",
                        "Input Username is Empty or Null",
                        "ecaaa1", messageContext);
            }

            log.debug("Calling radiusClientService.getSubscriberAccountDetails for username: {}", userName);
            GenericDataDTO genericDataDTO = radiusClientService.getSubscriberAccountDetails(userName, SoapConstants.MVNOID);
            log.debug("Integration Received Response in:{}Ms,Response:{} ", System.currentTimeMillis() - startTime, genericDataDTO.getData());

            if (genericDataDTO.getResponseCode() == 503) {
                log.error("unavailable (503) for username: {}, message: {}", userName, genericDataDTO.getResponseMessage());
                return getExceptionInResponse("generalException", "InvalidSubscriberAccountException ", genericDataDTO.getResponseMessage(), "ecaaa1", messageContext);
            }
            if (Objects.nonNull(genericDataDTO.getData())) {
                log.debug("Processing subscriber account details for username: {}", userName);
                GetSubscriberAccountDetailsDTO dto = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().writeValueAsString(genericDataDTO.getData()), GetSubscriberAccountDetailsDTO.class);
                if (Objects.nonNull(dto)) {
                    log.info("Successfully retrieved subscriber account details for username: {}", userName);
                    source = generateSubscriberAccountResponse(dto, messageContext);
                }
            } else {
                log.warn("Username not found in SPR Table: {}", userName);
                return getExceptionInResponse("generalException", "InvalidSubscriberAccountException ", "Username is not available in SPR Table", "ecaaa1", messageContext);
            }
        } catch (FeignException e) {
            log.debug("FeignException occurred while processing username: {}", userName);
            ObjectMapper objectMapper = new ObjectMapper();
            String message = "";
            int status = 404;
            try {
                String errorMessage = e.contentUTF8();
                JsonNode jsonNode = objectMapper.readTree(errorMessage);
                message = jsonNode.get("msg").asText();
                status = jsonNode.get("status").asInt();
                log.error("Feign error details - status: {}, message: {}", status, message);
                if (Objects.nonNull(message)) {
                    return getExceptionInResponse(
                            "generalException",
                            "InvalidSubscriberAccountException",
                            message,
                            "ecaaa1",
                            messageContext
                    );
                }

            } catch (JsonProcessingException je) {
                log.error("Error processing JSON response for username: {}", userName, je.getMessage());
                je.printStackTrace();
                throw new RuntimeException("Error processing JSON response", je);
            }
            e.printStackTrace();
            return getExceptionInResponse(
                    "generalException",
                    "InvalidSubscriberAccountException",
                    message,
                    "ecaaa1",
                    messageContext
            );
        } catch (ParserConfigurationException e) {
            log.error("Parser configuration error for username: {}", userName, e);
            return getExceptionInResponse("InvalidSubscriberAccountException", "InvalidSubscriberAccountException ", "Not Converted Object in Output XML object due to technical issue.", "ecaaa1", messageContext);
        } catch (SOAPException e) {
            log.error("SOAP exception for username: {}", userName, e);
            return getExceptionInResponse("RemoteException", "RemoteException ", "SubscriberProfileWebServiceException Exception due to technical issue.", "ecaaa1", messageContext);
        } catch (RuntimeException e) {
            log.error("Runtime exception for username: {}", userName, e);
            return getExceptionInResponse("RemoteException", "RemoteException ", "AxisFault Exception due to technical issue", "ecaaa1", messageContext);
        } catch (SQLException e) {
            log.error("SQL exception for username: {}", userName, e);
            return getExceptionInResponse("SQLException", "SQLException ", "SQL Exception", "ecaaa1", messageContext);
        } catch (Exception e) {
            log.error("Unexpected exception for username: {}", userName, e);
            return getExceptionInResponse("Exception", "Exception ", "Exception", "ecaaa1", messageContext);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("handleSubscriberAccountRequest completed in {} ms for username: {}",
                    (endTime - startTime), userName);
        }
        return source;
    }

    //
//    /**
//     * Generates a SOAP response containing subscriber account details wrapped in a CDATA section.
//     *
//     * @param name         the subscriber's name
//     * @param password     the subscriber's password
//     * @param serviceId    the service subscription ID
//     * @param creationDate the account creation date
//     * @param locationLock the location lock information
//     * @return a {@link DOMSource} containing the generated SOAP response
//     * @throws SOAPException                if a SOAP error occurs
//     * @throws ParserConfigurationException if a parser configuration error occurs
//     * @throws TransformerException         if a transformation error occurs
//     */
    public DOMSource generateSubscriberAccountResponse(GetSubscriberAccountDetailsDTO subscriberAccountDetailsDTO, MessageContext messageContext)
            throws SOAPException, ParserConfigurationException, TransformerException {
        // Create a SOAPMessage factory and message
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Add the required namespaces
        envelope.addNamespaceDeclaration("soapenv", "http://www.w3.org/2003/05/soap-envelope");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
//        envelope.addNamespaceDeclaration("ns1", "http://npm.redback.com");
//        envelope.removeNamespaceDeclaration("ns1");
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix("soapenv");
        SOAPBody body = envelope.getBody();
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        body.setPrefix("soapenv");

        // Create the response element in the body with the required namespace
        SOAPElement responseElement = body.addChildElement(new QName("", "ns1:getSubscriberAccountXMLResponse"));

        // Add the 'soapenv:encodingStyle' attribute to the response element
        responseElement.addAttribute(QName.valueOf("soapenv:encodingStyle"), "http://schemas.xmlsoap.org/soap/encoding/");

        // Add the 'xmlns:ns1' namespace to the element
        responseElement.addNamespaceDeclaration("ns1", "http://npm.redback.com");
        responseElement.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        responseElement.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        // Add the 'result' element with CDATA content
        SOAPElement resultElement = responseElement.addChildElement("result");
        resultElement.setAttribute("xsi:type", "xsd:string");

        // Create a Document from the SOAPPart
        Document document = soapPart.getEnvelope().getOwnerDocument();

        // Create the dynamic inner XML structure for SubscriberAccount
        Element subscriberAccount = document.createElement("SubscriberAccount");

        Element activated = document.createElement("Activated");
        String status = changeStatusValue(subscriberAccountDetailsDTO.getCStatus());
        activated.appendChild(document.createTextNode(subscriberAccountDetailsDTO.getCStatus()));
        subscriberAccount.appendChild(activated);

        Element creationDateElement = document.createElement("CreationDate");
        creationDateElement.appendChild(document.createTextNode(subscriberAccountDetailsDTO.getCreationDate()));
        subscriberAccount.appendChild(creationDateElement);

        Element locationLockElement = document.createElement("LocationLock");
        locationLockElement.appendChild(document.createTextNode(subscriberAccountDetailsDTO.getLocationLock() != null && !subscriberAccountDetailsDTO.getLocationLock().isEmpty() ? "0:92=\"[" + subscriberAccountDetailsDTO.getLocationLock() + "]\"" : ""));
        subscriberAccount.appendChild(locationLockElement);

        Element nameElement = document.createElement("Name");
        nameElement.appendChild(document.createTextNode(subscriberAccountDetailsDTO.getCustName()));
        subscriberAccount.appendChild(nameElement);

        Element passwordElement = document.createElement("Password");
        passwordElement.appendChild(document.createTextNode(subscriberAccountDetailsDTO.getPassword()));
        subscriberAccount.appendChild(passwordElement);

        // Create ServiceSubscriptions element and add ServiceSubscription element
        Element serviceSubscriptions = document.createElement("ServiceSubscriptions");
        Element serviceSubscription = document.createElement("ServiceSubscription");
        Element serviceIdElement = document.createElement("ServiceId");
        String planId = subscriberAccountDetailsDTO.getPlanId() != null ? subscriberAccountDetailsDTO.getPlanId() : "";
        serviceIdElement.appendChild(document.createTextNode(planId));
        serviceSubscription.appendChild(serviceIdElement);
        serviceSubscriptions.appendChild(serviceSubscription);
        subscriberAccount.appendChild(serviceSubscriptions);


        // Save changes to the SOAP message
        // Now wrap the entire SubscriberAccount structure in a CDATA section
        StringWriter stringWriter = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 4);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); // Include XML declaration
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(subscriberAccount), new StreamResult(stringWriter));
        String cdataContent = stringWriter.toString().trim();
        CDATASection cdataSection = document.createCDATASection(cdataContent);
        resultElement.appendChild(cdataSection);
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource
        Document documents = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = documents.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for the SOAP message
        return new DOMSource(fragment);
    }

    /**
     * Creates an XML element with the specified tag name and text content.
     *
     * @param document    The XML {@link Document} in which the element will be created.
     * @param tagName     The name of the XML element to be created.
     * @param textContent The text content to be added inside the created element.
     * @return A new {@link Element} containing the specified text content.
     */
    private Element createElementWithText(Document document, String tagName, String textContent) {
        Element element = document.createElement(tagName);
        element.appendChild(document.createTextNode(textContent));
        return element;
    }


    /**
     * Handles SOAP exceptions by creating a detailed SOAP fault message.
     *
     * @param faultCodeString     the fault code
     * @param exceptionNameString the exception name
     * @param exceptionMessage    the exception message
     * @param host                the hostname
     * @return a {@link DOMSource} containing the SOAP fault
     * @throws SOAPException if a SOAP error occurs
     * @throws IOException   if an I/O error occurs
     */
    public DOMSource getExceptionInResponse(String faultCodeString, String exceptionNameString, String exceptionMessage, String host, MessageContext messageContext) throws SOAPException, IOException {
        // Sanitize and trim inputs
        faultCodeString = sanitizeXml(faultCodeString).trim();
        exceptionNameString = sanitizeXml(exceptionNameString).trim();
        exceptionMessage = sanitizeXml(exceptionMessage).trim();
        host = sanitizeXml(host).trim();

        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.setPrefix("soapenv");
        // Declare the required namespaces explicitly at the envelope level
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
//        envelope.addNamespaceDeclaration("soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        // Create SOAP body
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPFault fault = body.addFault();

        // Set fault code
        fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server." + faultCodeString, "soapenv"));

        // Set empty fault string
        fault.setFaultString("");

        // Create detail element
        Detail detail = fault.addDetail();

        // Add InvalidSubscriberAccountException element
        DetailEntry invalidSubscriberAccountException = detail.addDetailEntry(
                new QName("http://npm.redback.com", exceptionNameString, "ns1"));
        invalidSubscriberAccountException.addAttribute(new QName("href"), "#id0");

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

        // Set attributes in the exact order required
        multiRefElement.setAttribute("id", "id0"); // First
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0"); // Second
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/"); // Third
        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "ns4:" + exceptionNameString); // Fourth

        // Add xmlns:soapenc first
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/"); // Fifth
        // Add xmlns:ns4 next
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com"); // Sixth

        // Add the message element
        SOAPElement message = multiRef.addChildElement("message");
        message.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
        message.addTextNode(exceptionMessage);

        // Save changes and verify structure
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();
        // Convert body to DOMSource for return
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }
        return new DOMSource(fragment);
    }

    /**
     * Sanitizes input XML strings by removing invalid characters.
     *
     * @param input the input string
     * @return the sanitized string
     */
    public static String sanitizeXml(String input) {
        if (input == null) {
            return null;
        }
        // Replace invalid XML characters
        return input.replaceAll("[^\\x20-\\x7E]", "");
    }

    public String changeStatusValue(String status) {
        if (status != null && status.equalsIgnoreCase("Active")) {
            return "Y";
        } else {
            return "N";
        }
    }


}
