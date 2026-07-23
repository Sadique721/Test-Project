package com.savbill.integrationsystem.SOAPService.authenticateSubscriber;

import com.savbill.integrationsystem.RestApiService.authenticateUser.LoginPojo;
import com.savbill.integrationsystem.SOAPService.CustomResponseGeneratorHandler.CustomResponseGenerator;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wsauthenticatesubscriber.AuthenticateSubscriber;
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
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;

@Slf4j
@Endpoint
public class AuthenticateSubbscriberEndpoint {

    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    public CmsClient cmsClient;
    @Autowired
    private RadiusClientService radiusClientService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "authenticateSubscriber")
    @ResponsePayload
    public DOMSource getWsAuthenticateSubscriber(@RequestPayload AuthenticateSubscriber request, MessageContext messageContext) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Starting getWsAuthenticateSubscriber At: {}", new Date(startTime));

        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String userName = request.getString1().trim();
        if (userName == null || userName.isEmpty()) {
            log.error("Authentication failed: Username is empty or null");
            return createFault("generalException",
                    "InvalidSubscriberAccountException",
                    "Input Username is Empty or Null",
                    "ecaaa1",
                    messageContext
            );
        } else if (request.getString2().trim() == null || request.getString2().trim().isEmpty()) {
            log.error("Authentication failed: Password is empty or null");
            return createFault("generalException",
                    "InvalidSubscriberAccountException",
                    "Input Password is Empty or Null",
                    "ecaaa1",
                    messageContext
            );
        }
        try {
            LoginPojo pojo = new LoginPojo();
            pojo.setUsername(userName.toLowerCase().trim());
            pojo.setPassword(request.getString2().trim());
            log.debug("Call Radius Client To Fetch customer details for username: {}", pojo.getUsername());
            GenericDataDTO genericDataDTO = radiusClientService.getCustomerDetails(pojo.getUsername(), SoapConstants.MVNOID);
            log.debug("Integration Received response In:{}MS,Response:{}", System.currentTimeMillis() - startTime, genericDataDTO.getResponseMessage());
            if (genericDataDTO.getData() instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) genericDataDTO.getData();
                log.debug("Authenticating user with CMS service");
                Boolean loginValidator = cmsClientService.getAuthenticateUser(pojo, token);
                log.debug("Authenticate Received In:{}MS,Response:{}", System.currentTimeMillis() - startTime, loginValidator);
                String custPassword = (String) map.get("password");
                if (custPassword != null && !custPassword.equals(request.getString2().trim())) {
                    log.info("Authentication failed: Invalid password for user: {}", userName);
                    return getAuthenticateSubscriberSuccess(false, messageContext);
                } else {
                    log.info("Authentication completed for user: {}, result: {}", userName, loginValidator);
                    return getAuthenticateSubscriberSuccess(loginValidator, messageContext);
                }
            }
            log.error("Authentication failed: Username not found in SPR Table for user: {}", userName);
            return createFault(
                    "generalException",
                    "InvalidSubscriberAccountException",
                    "Username is not available in SPR Table ",
                    "ecaaa1",
                    messageContext
            );
        } catch (RuntimeException e) {
            log.error("SubscriberProfileWebServiceException occurred", e.getMessage());
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "SubscriberProfileWebServiceException",
                    "SubscriberProfileWebServiceException",
                    "SubscriberProfileWebServiceException while calling production API",
                    "ecaaa1",
                    messageContext
            );
        } catch (RemoteException e) {
            log.error("RemoteException occurred", e.getMessage());
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "RemoteException",
                    "RemoteException",
                    "AxisFault Exception due to technical issue",
                    "ecaaa1",
                    messageContext
            );
        } catch (Exception e) {
            log.error("Unexpected exception occurred", e);
            return CustomResponseGenerator.createSOAP11FaultResponse(
                    "Exception",
                    "Exception",
                    "Exception",
                    "ecaaa1",
                    messageContext
            );
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("getWsAuthenticateSubscriber method completed in {} ms", executionTime);
        }
    }


    public DOMSource getAuthenticateSubscriberSuccess(Boolean booleanExpression, MessageContext messageContext) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        envelope.setPrefix("soapenv");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode(); // Remove the header if it exists
        }

        SOAPElement sessionIsLogged = body.addChildElement("authenticateSubscriberResponse", "ns1", "http://npm.redback.com");
        sessionIsLogged.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");

        SOAPElement result = sessionIsLogged.addChildElement("result");
        Element element1 = result;
        element1.setAttribute("href", "#id0");

        // Create multiRef element
        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
        org.w3c.dom.Element multiRefElement = (org.w3c.dom.Element) multiRef;

        // Set attributes for multiRef element
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/"); // Third
        multiRefElement.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "xsd:boolean");

        // Set namespaces for multiRef element
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);

        // Set the content (true/false)
        multiRefElement.setTextContent(String.valueOf(booleanExpression));

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

        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }
        return new DOMSource(fragment);
    }


    public static DOMSource createFault(String faultCodeString, String exceptionNameString, String exceptionMessage, String host, MessageContext messageContext) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Declare the required namespaces explicitly at the envelope level
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        // Set the envelope prefix to "soapenv"
        envelope.setPrefix("soapenv");

        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode(); // Remove the header if it exists
        }

        // Set up the Body and remove any pre-existing header if present
        SOAPBody body = envelope.getBody();
        body.setPrefix("soapenv");

        SOAPFault fault = body.addFault();
        fault.setPrefix("soapenv");

        fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server." + faultCodeString, "soapenv"));
        fault.setFaultString("");

        Detail detail = fault.addDetail();
        DetailEntry faultData = detail.addDetailEntry(new QName("", "faultData", ""));
        faultData.setAttribute("href", "#id0");

        SOAPElement invalidSubscriberAccountException = detail.addChildElement("exceptionName", "ns1", "http://xml.apache.org/axis/");
        invalidSubscriberAccountException.addTextNode("com.redback.npm." + exceptionNameString);

        SOAPElement hostName = detail.addChildElement("hostname", "ns2", "http://xml.apache.org/axis/");
        hostName.addTextNode(host);

        // Manually create the multiRef element
        SOAPElement multiRef = body.addChildElement(new QName("", "multiRef"));
        Element multiRefElement = multiRef;
        multiRefElement.setAttribute("id", "id0");
        multiRefElement.setAttributeNS(SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE, "soapenc:root", "0");
        multiRefElement.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/"); // Third
        multiRefElement.setAttributeNS(SoapConstants.XSI_NAMESPACE, "xsi:type", "ns3:InvalidSubscriberAccountException");
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:soapenc", SoapConstants.SOAP_ENCODING_STYLE_NAMESPACE);
        multiRefElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", "http://npm.redback.com");
        SOAPElement message = multiRef.addChildElement("message");
        message.addAttribute(new QName(SoapConstants.XSI_NAMESPACE, "type", "xsi"), "xsd:string");
        message.addTextNode(exceptionMessage);

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


        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }
        return new DOMSource(fragment);
    }

}
