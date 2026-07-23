package com.savbill.integrationsystem.SOAPService.removeSubscriberAccount;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wsremovesubscriberaccount.RemoveSubscriberAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

@Slf4j
@Endpoint
public class RemoveSubscriberAccountEndpoint {
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    public CmsClient cmsClient;
    @Autowired
    private RadiusClientService radiusClientService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "removeSubscriberAccount")
    @ResponsePayload
    public DOMSource removeSubscriberaccount(@RequestPayload RemoveSubscriberAccount request, MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Starting removeSubscriberaccount AT:{}", new Date(startTime));

        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String userName = request.getString1().trim();

        if (userName == null || userName.isEmpty()) {
            log.warn("Username validation failed: empty or null value");
            log.info("Completed removeSubscriberaccount execution in {}ms", System.currentTimeMillis() - startTime);
            return generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse("generalException",
                    "InvalidSubscriberAccountException",
                    "Username is Empty or Null",
                    "ecaaa1", messageContext);
        }

        try {
            userName = request.getString1().toLowerCase().trim();
            log.debug("Call Radius Client To Fetch customer details for username: {}", userName);
            GenericDataDTO genericDataDTO = radiusClientService.getCustomerDetails(userName, SoapConstants.MVNOID);
            log.debug("Integration Receive response IN:{}MS,response:{}", System.currentTimeMillis() - startTime, genericDataDTO.getData());

            if (genericDataDTO.getData() != null) {
                Map<String, Object> mapData = (Map<String, Object>) genericDataDTO.getData();
                if (userName.equalsIgnoreCase(mapData.get("username").toString())) {
                    log.info("Call CmsClient: Customer found in system, proceeding with removal for username: {}", userName);
                    ResponseEntity<?> response = cmsClientService.removeSubscriberCustomerStatus(request.getString1(), SoapConstants.MVNOID, token);
                    Map<String, Object> objectMap = (Map<String, Object>) response.getBody();
                    log.debug("Integration Receive response IN:{}MS,response:{}", System.currentTimeMillis() - startTime, objectMap);

                    if (objectMap.get("terminationCheck") != null && objectMap.get("terminationCheck").equals("Success")) {
                        log.info("Successfully removed subscriber account for username: {}. Time taken: {}ms",
                                userName, System.currentTimeMillis() - startTime);
                        return generateRemoveSubscriberAccountSOAP11SuccessResponse("removeSubscriberAccountResponse", "responseData", messageContext);
                    }
                }
            }

            log.warn("Username not found in SPR table: {}", userName);
            return generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse(
                    "generalException",
                    "InvalidSubscriberAccountException",
                    "Input username is not available in SPR table",
                    "ecaaa1", messageContext
            );

        } catch (RuntimeException e) {
            log.error("RuntimeException occurred while processing username {}: {}", userName, e.getMessage(), e);
            return generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse(
                    "RemoteException",
                    "RemoteException",
                    "SubscriberProfileWebServiceException Excpetion due to technical issue",
                    "ecaaa1", messageContext
            );
        } catch (RemoteException e) {
            log.error("RemoteException occurred while processing username {}: {}", userName, e.getMessage(), e);
            return generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse(
                    "RemoteException",
                    "RemoteException",
                    "AxisFault Exception due to technical issue",
                    "ecaaa1", messageContext
            );
        } catch (SQLException e) {
            log.error("SQLException occurred while processing username {}: {}", userName, e.getMessage(), e);
            return generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse(
                    "SQLException",
                    "SQLException",
                    "SQL Exception",
                    "ecaaa1", messageContext
            );
        } catch (Exception e) {
            log.error("Unexpected exception occurred while processing username {}: {}", userName, e.getMessage(), e);
            return generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse(
                    "Exception",
                    "Exception",
                    " Exception",
                    "ecaaa1", messageContext
            );
        } finally {
            log.info("Completed removeSubscriberaccount execution in {}ms", System.currentTimeMillis() - startTime);
        }
    }


    public static DOMSource getExceptionsInResponse(String faultCodeString, String exceptionNameString, String exceptionMessage, String host) throws SOAPException, IOException {
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
        DetailEntry invalidIPAddressException = detail.addDetailEntry(new QName("http://npm.redback.com/", exceptionNameString, "ns1"));
        invalidIPAddressException.addAttribute(new QName("href"), "#id0");

        // Add exceptionName element
        DetailEntry exceptionName = detail.addDetailEntry(new QName("http://xml.apache.org/axis/", "exceptionName", "ns2"));
        exceptionName.addTextNode("com.redback.npm." + exceptionNameString);

        // Add hostname element
        DetailEntry hostname = detail.addDetailEntry(new QName("http://xml.apache.org/axis/", "hostname", "ns3"));
        hostname.addTextNode(host);

        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenv:encodingStyle", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRefElement.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "ns4:InvalidSubscriberAccountException");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com");

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

//        SOAPElement result = getSubAcctNameResponse.addChildElement("result");
//        result.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
//        result.addTextNode(email);

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
     * Generates a SOAP 1.1 success response with custom child element and response data.
     *
     * @param setChildName   The name of the child element to be added to the SOAP body.
     * @param responseData   The data to be included in the child element of the SOAP response.
     * @param messageContext Optional MessageContext to update with the generated SOAP message.
     * @return DOMSource      SOAP success response as a DOMSource.
     * @throws SOAPException If an error occurs during SOAP message creation.
     */
    public DOMSource generateRemoveSubscriberAccountSOAP11SuccessResponse(String setChildName, String responseData, MessageContext messageContext) throws SOAPException {
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
        SOAPElement faultElement = body.addChildElement(setChildName, "ns1", "http://npm.redback.com");
        faultElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");

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
     *
     * @param faultCode        The fault code string to include in the SOAP fault response.
     * @param exceptionName    The name of the exception to include in the detail element.
     * @param exceptionMessage The exception message to include in the multiRef element.
     * @param host             The hostname where the exception occurred.
     * @param messageContext   Optional MessageContext to update with the generated SOAP message.
     * @return DOMSource       SOAP fault response as a DOMSource.
     * @throws SOAPException If an error occurs during SOAP message creation.
     */
    public DOMSource generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse(String faultCodeString, String exceptionNameString, String exceptionMessage, String host, MessageContext messageContext) throws SOAPException {
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
