package com.savbill.integrationsystem.SOAPService.RemoveService;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccount;
import com.savbill.integrationsystem.generated.removeservice.RemoveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.Map;

@Service
public class RemoveServiceSoapService {

    @Autowired
    private CmsClientService cmsClientService;

    @Autowired
    private JwtUtil jwtUtil;
    private final Logger log = LoggerFactory.getLogger(RemoveServiceSoapService.class);
    public DOMSource handleRemoveServiceRequest(RemoveService request, MessageContext messageContext) throws SOAPException, IOException {
        log.info("Received request to remove service with UserName: {} and Status: {}",request.getString1(), request.getString2());
        try {
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);

            // Validate input UserName
            if (request.getString1().isEmpty()) {
                log.warn("Username is empty or null");
                return generateInvalidIpAndOthersSOAP11ErrorResponse(
                        "generalException",
                        "InvalidSubscriberAccountException",
                        "Username is Empty or Null",
                        "ecaaa1",messageContext
                );
            }
            // Validate input ServiceId
            if (request.getString2().isEmpty()) {
                log.warn("Status is empty or null");
                return generateInvalidIpAndOthersSOAP11ErrorResponse(
                        "generalException",
                        "InvalidServiceSubscriptionException",
                        "Service ID is Empty or Null",
                        "ecaaa1",messageContext
                );
            }
            if (!request.getString1().isEmpty() && !request.getString2().isEmpty()) {
                log.info("Calling CMS client to remove service request: {}", request);
                long startTime = System.currentTimeMillis();
                ResponseEntity<?> responseEntity = cmsClientService.removeService(request, mvnoId, token);
                long endTime = System.currentTimeMillis();
                log.info("CMS client call completed in {} ms responseEntity: {}",(endTime - startTime),responseEntity);
                Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
                if (responseBody != null && "not available".equals(responseBody.get("Failure"))) {
                    log.warn("Record not updated in SPR table due to technical issue");
                    return generateInvalidIpAndOthersSOAP11ErrorResponse(
                            "InvalidServiceException",
                            "InvalidServiceException",
                            "Not Updated Record in SPR table for Status due to technical Issue",
                            "ecaaa1",messageContext
                    );
                }

                // Check if the status code is OK (200)
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    log.info("Status removed successfully for UserName: {} and Status: {}", request.getString1(), request.getString2());
                    // Check if responseBody has a "status" and it's 200
                    if (responseBody != null && responseBody.get("status") != null
                            && (Integer) responseBody.get("status") == 200) {
                        return generateRemoveServiceSOAPSuccessResponse(messageContext);
                    }
                }

            }
        }
        catch (SOAPException e) {
            log.error("SOAPException encountered: {}", e.getMessage(), e);
            return generateInvalidIpAndOthersSOAP11ErrorResponse(
                    e.getClass().getSimpleName(),
                    "SQLException",
                    e.getMessage(),
                    "ecaaa1",messageContext
            );
        }catch (Exception e) {
            log.error("Unexpected exception encountered: {}", e.getMessage(), e);
            return generateInvalidIpAndOthersSOAP11ErrorResponse(
                    e.getClass().getSimpleName(),
                    "RemoteException",
                    e.getMessage(),
                    "ecaaa1",messageContext
            );
        }
        return null;

    }

    public static DOMSource generateSuccessResponse() throws SOAPException {
        // Create a SOAP message
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Define namespaces
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        // Create SOAP Body
        SOAPBody body = envelope.getBody();

        // Add `addServiceToSubAcctNameResponse` element
        SOAPElement responseElement = body.addChildElement("addServiceToSubAcctNameResponse", "ns1", "http://npm.redback.com");
        responseElement.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

        // Convert SOAPMessage to DOMSource
        Document document = body.getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    /**
     * Generates a SOAP 1.1 success response for a "remove service" operation.
     *
     * This method creates and returns a SOAP message with the appropriate namespaces and a success response
     * indicating the removal of a service. The response is returned as a `DOMSource` representing the SOAP body.
     *
     * @param messageContext The `MessageContext` used to update the response with the generated SOAP message.
     * @return DOMSource The generated SOAP response body.
     * @throws SOAPException If an error occurs during the creation or manipulation of the SOAP message.
     */
    public DOMSource generateRemoveServiceSOAPSuccessResponse(MessageContext messageContext) throws SOAPException {
        // Create the SOAP Message
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove default namespace (SOAP-ENV) and add required namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.setPrefix("soapenv");

        // Create the SOAP body
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");

        // Remove any existing Fault
        if (body.getFault() != null) {
            body.removeChild(body.getFault());
        }
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        // Create the removeServiceResponse element under ns1 namespace
        SOAPElement removeServiceResponse = body.addChildElement("removeServiceResponse", "ns1", "http://npm.redback.com");
        removeServiceResponse.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");

        // Save the changes to the SOAP message
        soapMessage.saveChanges();

        // Set the response message in the context
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Extract the body content and create a DOMSource for the response
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the SOAP response as a DOMSource
        return new DOMSource(fragment);
    }


    /**
     * Generates a SOAP 1.1 error response for invalid IP and other related errors.
     *
     * This method constructs a SOAP message with the provided fault code, exception name, exception message,
     * and host, forming a detailed error response. The response is generated under the "Fault" element and
     * includes additional information such as exception name, message, and hostname.
     *
     * @param faultCodeString The fault code string to be included in the error response.
     * @param exceptionNameString The name of the exception to be referenced in the response.
     * @param exceptionMessage The message to be included with the exception.
     * @param host The hostname where the error occurred.
     * @param messageContext The `MessageContext` used to update the response with the generated SOAP message.
     *
     * @return DOMSource The generated SOAP error response body.
     * @throws SOAPException If an error occurs during the creation or manipulation of the SOAP message.
     */
    public static DOMSource generateInvalidIpAndOthersSOAP11ErrorResponse(
            String faultCodeString, String exceptionNameString, String exceptionMessage,
            String host, MessageContext messageContext) throws SOAPException {

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

        // Add InvalidSubscriberAccountException reference
        SOAPElement invalidSubscriberException = detail.addChildElement(exceptionNameString, "ns1", "http://npm.redback.com");
        invalidSubscriberException.addAttribute(new javax.xml.namespace.QName("href"), "#id0");

        // Add exceptionName element
        SOAPElement exceptionName = detail.addChildElement("exceptionName", "ns2", "http://xml.apache.org/axis/");
        exceptionName.addTextNode("com.redback.npm." + exceptionNameString);

        // Add hostname element
        SOAPElement hostname = detail.addChildElement("hostname", "ns3", "http://xml.apache.org/axis/");
        hostname.addTextNode(host);

        // Add multiRef element
        SOAPElement multiRefElement = body.addChildElement("multiRef");

        // Set attributes in the exact order required
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/encoding/", "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
        multiRefElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "ns4:"+exceptionNameString);

        // Add xmlns:soapenc first
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        // Add xmlns:ns4 next
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns4", "http://npm.redback.com");

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
