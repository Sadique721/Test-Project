package com.savbill.integrationsystem.RestApiService.removeSubscribeAccount;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wsremovesubscriberaccount.RemoveSubscriberAccount;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@Service
public class removeSubscriberserviceSrv {
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    public CmsClient cmsClient;
    @Autowired
    private RadiusClientService radiusClientService;

    public GenericDataDTO removeSubscriberaccount(@RequestPayload RemoveSubscriberAccountDto request) throws Exception {
        log.info("Starting removeSubscriberaccount process for username: {}", request.getUserName());

        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String userName = request.getUserName().trim();
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        if (userName == null || userName.isEmpty()) {
            log.warn("Provided username is empty or null");
            genericDataDTO.setResponseMessage("Username is Empty or Null");
            genericDataDTO.setResponseCode(SoapConstants.EMPTY);
            return genericDataDTO;
        }
        try {
            userName = userName.toLowerCase();
            log.info("Fetching customer details for username: {}", userName);
            GenericDataDTO customerDetails = radiusClientService.getCustomerDetails(userName, SoapConstants.MVNOID);

            if (customerDetails.getData() != null) {
                Map<String, Object> mapData = (Map<String, Object>) customerDetails.getData();
                if (userName.equalsIgnoreCase(mapData.get("username").toString())) {
                    log.info("Customer details found for username: {}. Proceeding to remove subscriber status.", userName);
                    ResponseEntity<?> response = cmsClientService.removeSubscriberCustomerStatus(userName, SoapConstants.MVNOID, token);
                    Map<String, Object> objectMap = (Map<String, Object>) response.getBody();
                    if (objectMap.get("terminationCheck") != null && objectMap.get("terminationCheck").equals("Success")) {
                        log.info("Successfully terminated subscriber account for username: {}", userName);
                        genericDataDTO.setResponseMessage(SoapConstants.SUCCESS);
                        genericDataDTO.setResponseCode(SoapConstants.SUCCESS_CODE);
                        return genericDataDTO;
                    }
                }
            }
            log.warn("Username not found in SPR table: {}", userName);
            genericDataDTO.setResponseMessage("Input username is not available in SPR table");
            genericDataDTO.setResponseCode(SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE);
            return genericDataDTO;
        } catch (RetryableException e) {
            log.error("Retry_ableException occurred while processing request From Radius Side for username: {}. Error: {}", userName, e.getMessage());
            genericDataDTO.setResponseMessage("SubscriberProfileWebServiceException due to technical issue");
            genericDataDTO.setResponseCode(SoapConstants.REMOTE_EXCEPTION_GENERATED_CODE);
            return genericDataDTO;
        } catch (RuntimeException e) {
            log.error("RuntimeException occurred while processing request for username: {}. Error: {}", userName, e.getMessage());
            genericDataDTO.setResponseMessage("SubscriberProfileWebServiceException Exception due to technical issue");
            genericDataDTO.setResponseCode(SoapConstants.REMOTE_EXCEPTION_GENERATED_CODE);
            return genericDataDTO;
        } catch (RemoteException e) {
            log.error("RemoteException occurred while processing request for username: {}. Error: {}", userName, e.getMessage());
            genericDataDTO.setResponseMessage("AxisFault Exception due to technical issue");
            genericDataDTO.setResponseCode(SoapConstants.REMOTE_EXCEPTION_GENERATED_CODE);
            return genericDataDTO;
        } catch (SQLException e) {
            log.error("SQLException occurred while processing request for username: {}. Error: {}", userName, e.getMessage());
            genericDataDTO.setResponseMessage(SoapConstants.SQL_EXCEPTION);
            genericDataDTO.setResponseCode(SoapConstants.SQL_EXCPTION_CODE);
            return genericDataDTO;
        } catch (Exception e) {
            log.error("Unexpected exception occurred while processing request for username: {}. Error: {}", userName, e.getMessage());
            genericDataDTO.setResponseMessage("Exception");
            genericDataDTO.setResponseCode(500);
            return genericDataDTO;
        }
    }

    public DOMSource removeSubscriberaccount(@RequestPayload RemoveSubscriberAccount request, MessageContext messageContext) throws Exception {
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String userName = request.getString1().trim();
        log.info("Starting removeSubscriberaccount process for username: {}", userName);
        if (userName == null || userName.isEmpty()) {
            log.warn("Provided username is empty or null");
            return generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse("generalException",
                    "InvalidSubscriberAccountException",
                    "Username is Empty or Null",
                    "ecaaa1", messageContext);
        }
        try {
            userName = request.getString1().toLowerCase().trim();
            log.info("Fetching customer details From Radius for username: {}", userName);
            GenericDataDTO genericDataDTO = radiusClientService.getCustomerDetails(userName, SoapConstants.MVNOID);
            if (genericDataDTO.getData() != null) {
                Map<String, Object> mapData = (Map<String, Object>) genericDataDTO.getData();
                if (userName.equalsIgnoreCase(mapData.get("username").toString())) {
                    log.info("Customer details found for username: {}. Proceeding to remove subscriber status.", userName);
                    ResponseEntity<?> response = cmsClientService.removeSubscriberCustomerStatus(request.getString1(), SoapConstants.MVNOID, token);
                    Map<String, Object> objectMap = (Map<String, Object>) response.getBody();
                    if (objectMap.get("terminationCheck") != null && objectMap.get("terminationCheck").equals("Success")) {
                        log.info("Successfully terminated subscriber account for username: {}", userName);
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
            log.error("RuntimeException occurred while processing request for username: {}. Error: {}", userName, e.getMessage());
            return generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse(
                    "RemoteException",
                    "RemoteException",
                    "SubscriberProfileWebServiceException Excpetion due to technical issue",
                    "ecaaa1", messageContext
            );
        } catch (RemoteException e) {
            log.error("RemoteException occurred while processing request for username: {}. Error: {}", userName, e.getMessage());
            return generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse(
                    "RemoteException",
                    "RemoteException",
                    "AxisFault Exception due to technical issue",
                    "ecaaa1", messageContext
            );
        } catch (SQLException e) {
            log.error("SQLException occurred while processing request for username: {}. Error: {}", userName, e.getMessage());
            return generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse(
                    "SQLException",
                    "SQLException",
                    "SQL Exception",
                    "ecaaa1", messageContext
            );
        } catch (Exception e) {
            log.error("Unexpected exception occurred while processing request for username: {}. Error: {}", userName, e.getMessage());
            return generateRemoveSubscriberAccountSOAP11InvalidIpExceptionResponse(
                    "Exception",
                    "Exception",
                    " Exception",
                    "ecaaa1", messageContext
            );
        }
    }

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

}
