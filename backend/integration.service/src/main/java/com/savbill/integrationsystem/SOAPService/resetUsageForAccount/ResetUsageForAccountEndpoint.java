package com.savbill.integrationsystem.SOAPService.resetUsageForAccount;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.removeAccount.RemoveAccountEndpoint;
import com.savbill.integrationsystem.Services.ResetUsageForAccountService;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.newresetusageforaccount.ResetUsageForAccountResponse;
import com.savbill.integrationsystem.generated.resetusageforaccount.WsResetUsageForAccount;
import com.savbill.integrationsystem.generated.resetusageforaccount.WsResetUsageForAccountResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Endpoint
public class ResetUsageForAccountEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(RemoveAccountEndpoint.class);
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ResetUsageForAccountService resetUsageForAccount;
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsResetUsageForAccount")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newresetusageforaccount.WsResetUsageForAccountResponse getWsResetUsageForAccountResponse(@RequestPayload WsResetUsageForAccount request, MessageContext messageContext) throws SOAPException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("Starting method: getWsResetUsageForAccountResponse AT:{}", new Date(startTime));
        try {
            return getWsResetUsageForAccount(request);
        } catch (NullPointerException e) {
            log.info("Method getWsResetUsageForAccountResponse completed in {}ms", System.currentTimeMillis() - startTime);
            return getWsResetUsageForAccount(request);
        } catch (Exception e) {
            log.info("Method getWsResetUsageForAccountResponse completed in {}ms", System.currentTimeMillis() - startTime);
            return getWsResetUsageForAccount(request);
        }
    }

    public com.savbill.integrationsystem.generated.newresetusageforaccount.WsResetUsageForAccountResponse getWsResetUsageForAccount(WsResetUsageForAccount request) {
        com.savbill.integrationsystem.generated.newresetusageforaccount.WsResetUsageForAccountResponse wsResetUsageForAccountResponse = new com.savbill.integrationsystem.generated.newresetusageforaccount.WsResetUsageForAccountResponse();
        ResetUsageForAccountResponse resetUsageForAccountResponse = new ResetUsageForAccountResponse();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String userName = request.getUserName().trim();
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        resetUsageForAccountResponse.setRequestId(requestId);
        long startTime = System.currentTimeMillis();
        log.info("Starting method: getWsResetUsageForAccount At:{}", new Date(startTime));

        if (userName == null || userName.isEmpty()) {
            log.warn("Username is empty or null in getWsResetUsageForAccount");
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input UserName is Empty or Null.";
            resetUsageForAccountResponse.setResponeCode(responseCode);
            resetUsageForAccountResponse.setResponseMessage(responseMessage);
            resetUsageForAccountResponse.setRequestId(requestId);
            wsResetUsageForAccountResponse.setResetUsageForAccount(resetUsageForAccountResponse);
            log.info("Method getWsResetUsageForAccountResponse completed in {}ms", System.currentTimeMillis() - startTime);
            return wsResetUsageForAccountResponse;
        }
        try {
            userName = userName.toLowerCase().trim();
            log.debug("Call Cms Client To Attempting reset usage for account:{}", userName);
            Boolean resetValidate = cmsClientService.resetUsageForAccount(userName, SoapConstants.MVNOID, token);
            log.debug("Integration Received Response:{} In:{}MS For reset usage for account:{}", resetValidate, System.currentTimeMillis() - startTime, userName);

            if (resetValidate) {
                log.info("Successfully reset usage for account:{}", userName);
                responseCode = SoapConstants.SUCCESS_CODE;
                responseMessage = SoapConstants.SUCCESS;
            } else {
                log.warn("Failed to reset usage for account:{} Not Found In system", userName);
                responseCode = SoapConstants.USAGE_NOT_RESECT_CODE;
                responseMessage = "Could not Reset Usage for Subscriber Account";
            }
            resetUsageForAccountResponse.setResponeCode(responseCode);
            resetUsageForAccountResponse.setResponseMessage(responseMessage);
            resetUsageForAccountResponse.setRequestId(requestId);
            wsResetUsageForAccountResponse.setResetUsageForAccount(resetUsageForAccountResponse);
            log.info("Method getWsResetUsageForAccountResponse completed in {}ms", System.currentTimeMillis() - startTime);
            return wsResetUsageForAccountResponse;
        } catch (Exception e) {
            log.error("Error in getWsResetUsageForAccount", e);
            resetUsageForAccountResponse.setResponeCode(responseCode);
            resetUsageForAccountResponse.setResponseMessage(responseMessage);
            resetUsageForAccountResponse.setRequestId(requestId);
            wsResetUsageForAccountResponse.setResetUsageForAccount(resetUsageForAccountResponse);
        }
        log.info("Method getWsResetUsageForAccountResponse completed in {}ms", System.currentTimeMillis() - startTime);
        return wsResetUsageForAccountResponse;
    }
    /*
    public DOMSource generateResetUsageForAccountSOAPResponse(WsResetUsageForAccountResponse response) throws SOAPException, ParserConfigurationException {
        // Create a SOAP Message factory and message
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Add namespace declarations
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("ns2", "http://api.act.com/");

        SOAPBody body = envelope.getBody();

        // Create the response element
        SOAPElement responseElement = body.addChildElement("wsResetUsageForAccountResponse", "ns2");

        // Add ResetUsageForAccount element
        SOAPElement resetUsageForAccountElement = responseElement.addChildElement("ResetUsageForAccount");

        // Add child elements to ResetUsageForAccount
        if(response.getRequestId()==null || response.getRequestId().equals("?") || response.getRequestId().equals("") || response.getRequestId().equals(" ")){
            resetUsageForAccountElement.addChildElement("requestId").addTextNode("?");
        }else {
            resetUsageForAccountElement.addChildElement("requestId").addTextNode(getSafeText(response.getRequestId()));
        }
        resetUsageForAccountElement.addChildElement("responeCode").addTextNode(getSafeNumber(response.getResponeCode()));
        resetUsageForAccountElement.addChildElement("responseMessage").addTextNode(getSafeText(response.getResponseMessage()));

        // Save changes to the SOAP message
        soapMessage.saveChanges();

        // Convert SOAP message to DOMSource
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for the constructed XML
        return new DOMSource(fragment);
    }
     */

    // Use this both success and exception and NullUserName response generator method without interceptor
    // First test for checking sheet response and adjust parameter value by taking from response class

    /**
     * Creates a SOAP 1.1 response for resetting usage for an account.
     * The response indicates successful reset of account usage with a response code of 200.
     *
     * @param response       The response object containing reset usage details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for reset usage for account.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateResetUsageForAccountSOAP11SuccessResponse(WsResetUsageForAccountResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove the default namespace and add custom namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        if (String.valueOf(response.getResponeCode()).equalsIgnoreCase("401")) {
            envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        } else {
            envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        }
        SOAPBody body;
        if (String.valueOf(response.getResponeCode()).equalsIgnoreCase("401")) {
            envelope.setPrefix("soap");
            body = envelope.getBody();
            body.setPrefix("soap");
        } else {
            envelope.setPrefix("soapenv");

            body = envelope.getBody();
            body.setPrefix("soapenv");
        }
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Add the main response element with custom namespaces
        SOAPElement responseElement = body.addChildElement("wsResetUsageForAccountResponse", "ns2", "http://api.act.com/");

        // Add the ResetUsageForAccount element
        SOAPElement resetUsageForAccount = responseElement.addChildElement("ResetUsageForAccount");

        // Add custom values to the ResetUsageForAccount element
        resetUsageForAccount.addChildElement("requestId").addTextNode(response.getRequestId()); // Can be dynamic or fixed
        resetUsageForAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        resetUsageForAccount.addChildElement("responseMessage").addTextNode(response.getResponseMessage());

        // Set the response in the SaajSoapMessage object
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource for further processing
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        // Append all child nodes of the body to the fragment
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for further processing
        return new DOMSource(fragment);
    }

    /**
     * Creates a SOAP 1.1 response for resetting usage for an account with a null user ID.
     * The response indicates a successful reset with a response code of 200 and a success message.
     *
     * @param response       The response object containing reset usage details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for reset usage with null user ID.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateResetUsageForAccountSOAP11NullUserIdResponse(WsResetUsageForAccountResponse response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Remove the default namespace and add custom namespaces
        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        // Add the main response element with custom namespaces
        SOAPElement responseElement = body.addChildElement("wsResetUsageForAccountResponse", "ns2", "http://api.act.com/");

        // Add the ResetUsageForAccount element
        SOAPElement resetUsageForAccount = responseElement.addChildElement("ResetUsageForAccount");

        // Add custom values to the ResetUsageForAccount element
        resetUsageForAccount.addChildElement("requestId").addTextNode(response.getRequestId()); // Can be dynamic or fixed
        resetUsageForAccount.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        resetUsageForAccount.addChildElement("responseMessage").addTextNode(response.getResponseMessage());

        // Set the response in the SaajSoapMessage object
        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        // Convert SOAP message to DOMSource for further processing
        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        // Append all child nodes of the body to the fragment
        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        // Return the DOMSource for further processing
        return new DOMSource(fragment);
    }


}
