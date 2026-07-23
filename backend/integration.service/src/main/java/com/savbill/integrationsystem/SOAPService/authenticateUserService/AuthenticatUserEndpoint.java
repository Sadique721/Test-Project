package com.savbill.integrationsystem.SOAPService.authenticateUserService;

import com.savbill.integrationsystem.RestApiService.authenticateUser.LoginPojo;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wsauthenticateuser.AuthenticateUser;
import com.savbill.integrationsystem.generated.wsauthenticateuser.AuthenticateUserResponse;
import com.savbill.integrationsystem.generated.wsauthenticateuser.WsAuthenticateUser;
import com.savbill.integrationsystem.generated.wsauthenticateuser.WsAuthenticateUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Endpoint
public class AuthenticatUserEndpoint {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    public CmsClient cmsClient;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsAuthenticateUser")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.authenticateuser.WsAuthenticateUserResponse getWsAuthenticateUser(@RequestPayload WsAuthenticateUser request, MessageContext messageContext) throws SOAPException, IOException {
        com.savbill.integrationsystem.generated.authenticateuser.WsAuthenticateUserResponse response1 = new com.savbill.integrationsystem.generated.authenticateuser.WsAuthenticateUserResponse();
        com.savbill.integrationsystem.generated.authenticateuser.AuthenticateUser type = new com.savbill.integrationsystem.generated.authenticateuser.AuthenticateUser();
        long startTime = System.currentTimeMillis();
        // log.info("Method:getWsAuthenticateUser Started At:{}",new Date(startTime));
        WsAuthenticateUserResponse response = null;
        try {
            response = getWsAuthenticate(request);
            type.setRequestId(response.getRequestId());
            type.setResponeCode(String.valueOf(response.getResponeCode()));
            type.setResponseMessage(response.getResponseMessage());
            type.setResult(Boolean.valueOf(response.getResult()));
            response1.setAuthenticateUser(type);
            log.info("Method getWsAuthenticate completed in {}ms", System.currentTimeMillis() - startTime);
            return response1;
//            return generateAuthenticateUserSOAP11SuccessResponse(response, messageContext);
        } catch (Exception e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
//            return generateAuthenticateUserSOAP11SuccessResponse(response, messageContext);
            type.setRequestId(response.getRequestId());
            type.setResponeCode(String.valueOf(response.getResponeCode()));
            type.setResponseMessage(response.getResponseMessage());
            type.setResult(Boolean.valueOf(response.getResult()));
            response1.setAuthenticateUser(type);
            log.info("Method getWsAuthenticate completed in {}ms", System.currentTimeMillis() - startTime);
            return response1;
        }
    }

    public WsAuthenticateUserResponse getWsAuthenticate(WsAuthenticateUser request) {
        WsAuthenticateUserResponse response = new WsAuthenticateUserResponse();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String responseResult = "false";
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String userName = request.getUserName();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        response.setRequestId(requestId);
        long startTime = System.currentTimeMillis();
        log.info("Starting method getWsAuthenticate with username: {}, At:{}", request.getUserName(), new Date(startTime));
        if (userName == null || userName.isEmpty()) {
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input Username is Empty or null.";
            response.setResponeCode(responseCode);
            response.setResponseMessage(responseMessage);
            response.setRequestId(requestId);
            response.setResult("false");
            log.warn("Username validation failed: Empty or null username. RequestId: {}", requestId);
            return response;
        } else if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input Password is Empty or null.";
            response.setResponeCode(responseCode);
            response.setResponseMessage(responseMessage);
            response.setRequestId(requestId);
            response.setResult("false");
            log.warn("Password validation failed: Empty or null password. RequestId: {}", requestId);
            return response;
        }
        try {
            LoginPojo pojo = new LoginPojo();
            pojo.setUsername(request.getUserName().toLowerCase().trim());
            pojo.setPassword(request.getPassword());
            log.debug("Cms Client Call To check Authentication");
            ResponseEntity<?> responseEntity = cmsClient.authenticateUser(pojo, token);
            log.debug("Integration Received Authentication Status:{}", responseEntity.getBody());

            if (responseEntity != null && responseEntity.getStatusCodeValue() == 200) {
                responseCode = SoapConstants.SUCCESS_CODE;
                responseMessage = "Username and Password is matched - AUTHENTICATED";
                responseResult = "true";
                response.setResponeCode(responseCode);
                response.setResponseMessage(responseMessage);
                response.setResult(responseResult);
                response.setRequestId(requestId);
                log.info("Authentication successful for user: {}", userName);
                return response;
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Password Not Match")) {
                responseCode = SoapConstants.INPUT_NOT_MATCH_CODE;
                responseMessage = "Input Password is not match with Username";
                responseResult = "false";
                response.setResponeCode(responseCode);
                response.setResponseMessage(responseMessage);
                response.setResult(responseResult);
                response.setRequestId(requestId);
                log.warn("Authentication failed: Password mismatch for userName: {}. status: {}", userName, responseResult);
                return response;
            }
            if (e.getMessage().contains("Username or Password not matched")) {
                responseCode = 503;
                responseMessage = "Username is not available in SPR Table via Product API[findByUserIdentity]";
                responseResult = "false";
                response.setResponeCode(responseCode);
                response.setResponseMessage(responseMessage);
                response.setResult(responseResult);
                response.setRequestId(requestId);
                log.warn("Authentication failed: Username or Password not matched for user:{}. status:{}", userName, responseResult);
                return response;
            }
            response.setResponeCode(responseCode);
            response.setResult(responseResult);
            response.setResponseMessage(e.getMessage());
            response.setRequestId(requestId);
        }
        return response;
    }


//    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "AuthenticateUser")
//    @ResponsePayload
//    public AuthenticateUserResponse getAuthenticateUser(@RequestPayload AuthenticateUser request, MessageContext messageContext) throws SOAPException, IOException {
//        com.savbill.integrationsystem.generated.authenticateuser.WsAuthenticateUserResponse response = new com.savbill.integrationsystem.generated.authenticateuser.WsAuthenticateUserResponse();
//        AuthenticateUser type = new AuthenticateUser();
//
//        AuthenticateUserResponse response1 = null;
//        try {
//            response1 = getAuthenticate(request);
//            type.setRequestId(response1.getRequestId());
//            type.setResult(Boolean.valueOf(response1.getResult()));
//            type.setResponeCode(String.valueOf(response1.getResponeCode()));
//            type.setResponseMessage(response1.getResponseMessage());
////            return generateAuthenticateUserSOAP11SuccessResponse1(response, messageContext);
//        } catch (Exception e) {
//            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";

    /// /            return generateAuthenticateUserSOAP11SuccessResponse1(response1, messageContext);
//            type.setRequestId(response1.getRequestId());
//            type.setResult(Boolean.valueOf(response1.getResult()));
//            type.setResponeCode(String.valueOf(response1.getResponeCode()));
//            type.setResponseMessage(exceptionMessage);
//        }
//        response.setAuthenticateUser(type);
//        return response1;
//    }
    public AuthenticateUserResponse getAuthenticate(AuthenticateUser request) {
        AuthenticateUserResponse response = new AuthenticateUserResponse();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String responseResult = "false";
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String userName = request.getUserName().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        response.setRequestId(requestId);
        if (userName == null || userName.isEmpty()) {
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input Username is Empty or null.";
            response.setResponeCode(responseCode);
            response.setResponseMessage(responseMessage);
            response.setRequestId(requestId);
            response.setResult("false");
            return response;
        } else if (request.getPassword().trim() == null || request.getPassword().trim().isEmpty()) {
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input Password is Empty or null.";
            response.setResponeCode(responseCode);
            response.setResponseMessage(responseMessage);
            response.setRequestId(requestId);
            response.setResult("false");
            return response;
        }
        try {
            LoginPojo pojo = new LoginPojo();
            pojo.setUsername(request.getUserName().toLowerCase().trim());
            pojo.setPassword(request.getPassword());
            Boolean loginValidator = cmsClientService.getAuthenticateUser(pojo, token);
            if (loginValidator) {
                responseCode = SoapConstants.SUCCESS_CODE;
                responseMessage = "Username and Password is matched - AUTHENTICATED";
                responseResult = "true";
            } else {
                responseCode = SoapConstants.INPUT_NOT_MATCH_CODE;
                responseMessage = "Input Password is not match with Username";
                responseResult = "false";
            }
            response.setResponeCode(responseCode);
            response.setResponseMessage(responseMessage);
            response.setResult(responseResult);
            response.setRequestId(requestId);
            return response;
        } catch (Exception e) {
            response.setResponeCode(responseCode);
            response.setResult(responseResult);
            response.setResponseMessage(e.getMessage());
            response.setRequestId(requestId);
        }
        return response;
    }

    /*
    public DOMSource generategetWsAuthenticateSummerySOAPResponse(WsAuthenticateUserResponse response) throws SOAPException, ParserConfigurationException {
        // Create a SOAP Message factory and message
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Add namespace declarations
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("ns2", "http://api.act.com/");
        envelope.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPBody body = envelope.getBody();

        // Create the response element
        SOAPElement responseElement = body.addChildElement("wsAuthenticateUserResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add GetUserUsageSummary element
        SOAPElement addService = responseElement.addChildElement("AuthenticateUser");
        addService.addChildElement("requestId").addTextNode(getSafeText(response.getRequestId()));
        addService.addChildElement("responeCode").addTextNode(getSafeNumber(response.getResponeCode()));
        addService.addChildElement("responseMessage").addTextNode(getSafeText(response.getResponseMessage()));
        addService.addChildElement("result").addTextNode(getSafeText(response.getResult()));
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

    // Use this both success and exception response generator method without interceptor
    // First test for checking sheet response and adjust parameter value by taking from response class

    /**
     * Generates a SOAP 1.1 success response message for the AuthenticateUser operation.
     * This method constructs a SOAP message with custom response code, message, result, and requestId
     * to indicate successful user authentication and returns the resulting message as a DOMSource for further processing.
     *
     * @param response       the {@link WsAuthenticateUserResponse} containing the response data for the authentication request
     * @param messageContext the {@link MessageContext} used to update the response message context with the new SOAP message
     * @return a {@link DOMSource} containing the SOAP response message indicating a successful authentication
     * @throws SOAPException if there is an error in creating or processing the SOAP message
     */
    public DOMSource generateAuthenticateUserSOAP11SuccessResponse(WsAuthenticateUserResponse
                                                                           response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement responseElement = body.addChildElement("wsAuthenticateUserResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement authenticateUser = responseElement.addChildElement("AuthenticateUser");
        authenticateUser.addChildElement("requestId").addTextNode(response.getRequestId());
        authenticateUser.addChildElement("responeCode").addTextNode(String.valueOf((response.getResponeCode())));
        authenticateUser.addChildElement("responseMessage").addTextNode(response.getResponseMessage());
        authenticateUser.addChildElement("result").addTextNode(response.getResult());

        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

    public DOMSource generateAuthenticateUserSOAP11SuccessResponse1(AuthenticateUserResponse
                                                                            response, MessageContext messageContext) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.1 protocol
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.removeNamespaceDeclaration("SOAP-ENV");
        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix("soap");

        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }

        SOAPElement responseElement = body.addChildElement("wsAuthenticateUserResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement authenticateUser = responseElement.addChildElement("AuthenticateUser");
        authenticateUser.addChildElement("requestId").addTextNode(response.getRequestId());
        authenticateUser.addChildElement("responeCode").addTextNode(String.valueOf((response.getResponeCode())));
        authenticateUser.addChildElement("responseMessage").addTextNode(response.getResponseMessage());
        authenticateUser.addChildElement("result").addTextNode(response.getResult());

        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
        updateResponse.setSaajMessage(soapMessage);
        updateResponse.getSaajMessage().saveChanges();

        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
        DocumentFragment fragment = document.createDocumentFragment();

        NodeList childNodes = body.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            fragment.appendChild(childNodes.item(i).cloneNode(true));
        }

        return new DOMSource(fragment);
    }

}
