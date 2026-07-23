package com.savbill.integrationsystem.SOAPService.GetSubscriberSession;

import com.savbill.integrationsystem.SOAPService.GetUserUsageSummary.GetUserSessionresponseDto;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.getsubscribersession.GetSubscriberSession;
import com.savbill.integrationsystem.generated.wsgetsessionsbyip.WsGetUserSessionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
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
public class GetSubscriberSessionSoapService {

    @Autowired
    private RadiusClientService radiusClientService;

    public DOMSource handleSubscriberSessionRequest(GetSubscriberSession request, MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Starting handleSubscriberSessionRequest At: {}", new Date(startTime));

        GetUserSessionresponseDto dataMessage = null;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Long mvnoId = SoapConstants.MVNOID;
            String ipAddress = request.getString1().trim();

            // Validate input IP address
            if (ipAddress == null || StringUtils.isEmpty(ipAddress)) {
                log.error("Session lookup failed: IP address is empty or null");
                return getExceptionInResponse(
                        "generalException",
                        "InvalidIPAddressException",
                        "Input IP Address is Empty or Null",
                        "ecpcrf2", messageContext
                );
            }

            // Fetch session data from the external service
            if (!ipAddress.isEmpty()) {
                log.debug("Call Radius Client Fetching session data for IP address: {}", ipAddress);
                genericDataDTO = radiusClientService.getUserSessionsTimeZ(ipAddress, mvnoId);
                log.debug("Integration Received Response In:{}MS,Response:{}", System.currentTimeMillis() - startTime, genericDataDTO.getData());
                dataMessage = new ObjectMapper().readValue(
                        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(genericDataDTO.getData()),
                        GetUserSessionresponseDto.class
                );
            }

            // Check if data is available for the provided IP address
            if (genericDataDTO.getData() == null) {
                log.error("Session lookup failed: No data found for IP address: {}", ipAddress);
                return getExceptionInResponse(
                        "generalException",
                        "InvalidIPAddressException",
                        "Input IP Address not found in Session Table",
                        "ecpcrf2", messageContext
                );
            }

            log.info("Successfully retrieved session data for IP: {}", ipAddress);
            return getSuccessResponse(dataMessage, messageContext);

        } catch (Exception e) {
            log.error("Unexpected error while processing session request", e);
            return getExceptionInResponse(
                    e.getClass().getSimpleName(),
                    "Exception",
                    e.getMessage(),
                    "ecaaa1", messageContext
            );
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("handleSubscriberSessionRequest method completed in {} ms", executionTime);
        }
    }


    public static DOMSource getSuccessResponse(GetUserSessionresponseDto dataMessage, MessageContext messageContext) throws SOAPException, IOException {
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
        // Create the response structure with a result
        SOAPElement getSubAcctNameResponse = body.addChildElement(new QName("", "ns1:getSubscriberSessionResponse"));
        getSubAcctNameResponse.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");

        getSubAcctNameResponse.setAttribute("xmlns:ns1", "http://npm.redback.com");
//        getSubAcctNameResponse.setEncodingStyle(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        SOAPElement result = getSubAcctNameResponse.addChildElement("result");
        result.addAttribute(new QName("href"), "#id0");


        SOAPElement multiRef = body.addChildElement(new QName("multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;

        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "ns2:SubscriberSession");


//        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + "ns2", "http://npm.redback.com");
        multiRefElement.setAttribute("xmlns:ns2", "http://npm.redback.com");


        // Add Extra Fields to the SOAP Response
        addExtraField(multiRef, "circuitType", "");
        addExtraField(multiRef, "nasId", dataMessage.getNasPortId());
        addExtraField(multiRef, "nasType", dataMessage.getNasPortType());
        addExtraField(multiRef, "sessionId", dataMessage.getAcctSessionId());
        SOAPElement startDate = multiRef.addChildElement(new QName("", "startTime"));
        startDate.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:dateTime");
        startDate.addTextNode(dataMessage.getCreatedDateString());
        addExtraField(multiRef, "NASPortId", dataMessage.getNasPortId() != null ? dataMessage.getNasPortId() : "");
        addExtraField(multiRef, "NASPortType", dataMessage.getNasPortType() != null ? dataMessage.getNasPortType() : "");
        addExtraField(multiRef, "acctSessionId", dataMessage.getAcctSessionId() != null ? dataMessage.getAcctSessionId() : "");
        addExtraField(multiRef, "callingStationId", dataMessage.getCallingStationId() != null ? dataMessage.getCallingStationId() : "");
        addExtraField(multiRef, "context", "");
        SOAPElement delegatedIpv6Prefixes = multiRef.addChildElement("delegatedIpv6Prefixes");
        delegatedIpv6Prefixes.addAttribute(new QName("href"), "#id1");

        SOAPElement framedIpv6Prefixes = multiRef.addChildElement("framedIpv6Prefixes");
        framedIpv6Prefixes.addAttribute(new QName("href"), "#id2");

        addExtraField(multiRef, "macAddress", dataMessage.getCallingStationId());
        addExtraField(multiRef, "medium", "");
        addExtraField(multiRef, "sessionIp", dataMessage.getFramedIpAddress());
        // Optionally add subscriberAccount for the first multiRef

        if ("ns2:getSubscriberSessionResponse".equals("ns2:getSubscriberSessionResponse") && dataMessage.getUserName() != null) {
            SOAPElement accountElement = multiRef.addChildElement("subscriberAccount");
            accountElement.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi"), "xsd:string");
            accountElement.addTextNode(dataMessage.getUserName());
        }

        // Add MultiRef Elements for delegated and framed prefixes
        addMultiRefElement(body, "id2", "ns3:arrayList", null, "ns3");
        addMultiRefElement(body, "id1", "ns4:arrayList", null, "ns4");

        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) messageContext.getResponse();
        saajSoapMessage.setSaajMessage(soapMessage);
        saajSoapMessage.getSaajMessage().saveChanges();

        // Return the DocumentFragment as a DOMSource
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    private static void addExtraField(SOAPElement parentElement, String fieldName, String value) throws SOAPException {
        SOAPElement extraField = parentElement.addChildElement(fieldName);
        extraField.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
        extraField.addTextNode(value != null ? value : "");
    }


    // Helper method to create and add multiRef elements
    private static void addMultiRefElement(SOAPBody body, String id, String xsiType, String subscriberAccount, String nsPrefix) throws SOAPException {
        // Create the multiRef element
        SOAPElement multiRef = body.addChildElement(new QName("multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;

        // Add required attributes
        multiRefElement.setAttribute("id", id);
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/"); // Third
        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", xsiType);

        // Dynamically assign namespace based on the passed nsPrefix
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + nsPrefix, "http://java.sun.com/jax-rpc-ri/internal");

        // Optionally add subscriberAccount for the first multiRef
        if ("ns2:SubscriberSession".equals(xsiType) && subscriberAccount != null) {
            SOAPElement accountElement = multiRef.addChildElement("subscriberAccount");
            accountElement.addAttribute(new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi"), "xsd:string");
            accountElement.addTextNode(subscriberAccount);
        }

        // Optionally add collection element for arrayList or other types
        if (xsiType.contains("arrayList")) {
            SOAPElement collectionElement = multiRef.addChildElement("collection");
            collectionElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:nil", "true");
            collectionElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", xsiType.replace("arrayList", "collection"));
        }
    }

    public static DOMSource getExceptionInResponse(String faultCodeString, String exceptionNameString, String exceptionMessage, String host, MessageContext messageContext) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove default SOAP namespaces and declare custom namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");

        // Declare the required namespaces explicitly at the envelope level
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        envelope.setPrefix("soapenv");
        // Create SOAP body
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        // Set the Body and Header
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        SOAPFault fault = body.addFault();

        // Set fault code
        fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server." + faultCodeString, "soapenv"));
        fault.setAttribute("xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
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
        multiRefElement.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "ns4:" + exceptionNameString);

        // Add namespaces in the required order
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");

        // Add the message element
        SOAPElement message = multiRef.addChildElement("message");
        message.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
        message.addTextNode(exceptionMessage);

        // Save changes and verify structure
        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) messageContext.getResponse();
        saajSoapMessage.setSaajMessage(soapMessage);
        saajSoapMessage.getSaajMessage().saveChanges();
        // Convert body to DOMSource for return
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }
        return new DOMSource(fragment);
    }


}
