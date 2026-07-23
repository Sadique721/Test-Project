package com.savbill.integrationsystem.SOAPService.logingSession;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClient;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.service.LoginSessionService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wslogingsession.WsLoginSession;
import com.savbill.integrationsystem.generated.wslogingsession.WsLoginSessionResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Endpoint
public class LoginSessionEndPoint {

    @Autowired
    private LoginSessionService loginSessionService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RadiusClient radiusClients;

    @Autowired
    private RadiusClientService radiusClientService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsLoginSession")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newloginsession.WsLoginSessionResponse validateLoginSessionResponse(@RequestPayload WsLoginSession request, MessageContext messageContext) throws SOAPException, IOException {
        try {
            return velidateLogingsession(request);
        } catch (Exception e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            return velidateLogingsession(request);
        }
    }
//    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "LoginSession")
//    @ResponsePayload
//    public DOMSource validateLoginSessionResponse(@RequestPayload LoginSession request, MessageContext messageContext) throws SOAPException, IOException {
//        LoginSessionResponse response = null;
//        try {
//            response = velidateLogingsession(request);
//            return generateLoginSessionSAOP11SuccessAndExceptionResponse(response, messageContext);
//        } catch (Exception e) {
//            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
//            return generateLoginSessionSAOP11SuccessAndExceptionResponse(response, messageContext);
//        }
//    }

    public com.savbill.integrationsystem.generated.newloginsession.WsLoginSessionResponse velidateLogingsession(@RequestPayload WsLoginSession request) {
        com.savbill.integrationsystem.generated.newloginsession.WsLoginSessionResponse response = new com.savbill.integrationsystem.generated.newloginsession.WsLoginSessionResponse();
        com.savbill.integrationsystem.generated.newloginsession.LoginSessionResponse loginSessionResponse = new com.savbill.integrationsystem.generated.newloginsession.LoginSessionResponse();
        long startTime = System.currentTimeMillis();
        log.info("Starting method velidateLogingsession for username: {}, IP: {},At: {}", request.getUserName(), request.getIpAddress(), new Date(startTime));
        String userName = request.getUserName().trim();
        String password = request.getPassword().trim();
        String ipAddress = request.getIpAddress().trim();
        String responseMessage = SoapConstants.FAILURE;
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
//        GenericResponse<Object> response = new GenericResponse<>();
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        loginSessionResponse.setRequestId(requestId);
        if (userName == null || userName.isEmpty()) {
            log.warn("Username validation failed: Empty or null username");
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input Username is Empty or Null.";
            loginSessionResponse.setResponeCode(responseCode);
            loginSessionResponse.setResponseMessage(responseMessage);
            loginSessionResponse.setRequestId(requestId);
            response.setLoginSession(loginSessionResponse);
            log.info("Method velidateLogingsession completed in {}ms for username: {}", System.currentTimeMillis() - startTime, userName);
            return response;
        }
        try {
            Map<String, String> payload = new HashMap<String, String>();
            payload.put("username", userName);
            payload.put("password", password);
            payload.put("name", "mtik");
            payload.put("sa", "2");
            payload.put("framed-ip-address", ipAddress);

            userName = userName.toLowerCase().trim();
            log.debug("Checking IP address validation for IP: {}", ipAddress);
            Boolean checkIpAddress = loginSessionService.checkIpAddress(ipAddress);
            log.debug("Integration Received IpStatus: {} in: {}ms", checkIpAddress, System.currentTimeMillis() - startTime);
            if (!checkIpAddress) {
                log.warn("No session records found for IP: {}", ipAddress);
                responseCode = SoapConstants.NOT_AVAILABLE;
                responseMessage = "No Records Found in session table for give IPAddress.";
                loginSessionResponse.setResponeCode(responseCode);
                loginSessionResponse.setResponseMessage(responseMessage);
                loginSessionResponse.setRequestId(requestId);
                response.setLoginSession(loginSessionResponse);
                log.info("Method velidateLogingsession completed in {}ms for username: {}", System.currentTimeMillis() - startTime, userName);
                return response;
            }

            log.debug("Getting customer details for username: {}", userName);
            GenericDataDTO genericDataDTO = radiusClients.getCustomerDetails(userName, SoapConstants.MVNOID);
            log.debug("Integration Received Customer Details From Radius in:{}ms: Response{}", System.currentTimeMillis() - startTime, genericDataDTO.getData());

            if (genericDataDTO.getData() instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) genericDataDTO.getData();
                String liveUsername = dataMap.get("username").toString();
                String userStatus = dataMap.get("status").toString();
                String userPassword = dataMap.get("password").toString();
                log.debug("Validating user status: {}, username: {}", userStatus, userName);
                if ((userStatus != null && Objects.nonNull(userStatus)) && !userStatus.equalsIgnoreCase("Active") && !userStatus.equalsIgnoreCase("suspend")) {
                    log.warn("User status inactive for username: {}", userName);
                    responseCode = SoapConstants.STATUS_INACTIVE_CODE;
                    responseMessage = "User in Inactive status in SPR.";
                    loginSessionResponse.setResponeCode(responseCode);
                    loginSessionResponse.setResponseMessage(responseMessage);
                    loginSessionResponse.setRequestId(requestId);
                    response.setLoginSession(loginSessionResponse);
                    log.info("Method velidateLogingsession completed in {}ms for username: {}", System.currentTimeMillis() - startTime, userName);
                    return response;
                } else if ((userPassword != null && !userPassword.isEmpty()) && !userPassword.equalsIgnoreCase(password)) {
                    log.warn("Password mismatch for username: {}", userName);
                    responseCode = SoapConstants.INPUT_NOT_MATCH_CODE;
                    responseMessage = "Input Password is not match with Username.";
                    loginSessionResponse.setResponeCode(responseCode);
                    loginSessionResponse.setResponseMessage(responseMessage);
                    loginSessionResponse.setRequestId(requestId);
                    response.setLoginSession(loginSessionResponse);
                    log.info("Method velidateLogingsession completed in {}ms for username: {}", System.currentTimeMillis() - startTime, userName);
                    return response;
                } else if (!liveUsername.equalsIgnoreCase(userName)) {
                    log.warn("Username mismatch in SPR table for username: {}", userName);
                    responseCode = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE;
                    responseMessage = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE;
                    loginSessionResponse.setResponeCode(responseCode);
                    loginSessionResponse.setResponseMessage(responseMessage);
                    loginSessionResponse.setRequestId(requestId);
                    response.setLoginSession(loginSessionResponse);
                    log.info("Method velidateLogingsession completed in {}ms for username: {}", System.currentTimeMillis() - startTime, userName);
                    return response;
                } else {
                    log.debug("Checking location lock for username: {}, IP: {}", userName, ipAddress);
                    Map<String, Object> locationLockResponse = radiusClientService.getLocationLockResponse(payload, SoapConstants.MVNOID, token);
                    log.debug("Integration Received Location Lock Response From Radius: {},in: {}ms", locationLockResponse, System.currentTimeMillis() - startTime);
                    boolean checkLocationLock = false;
                    if (locationLockResponse.get("data") != null) {
                        checkLocationLock = (boolean) locationLockResponse.get("data");
                    }
                    if (!checkLocationLock) {
                        log.warn("Location lock validation failed for username: {}, IP: {}", userName, ipAddress);
                        responseCode = SoapConstants.USER_NOT_ALLOW_CODE;
                        responseMessage = "User is not allow service at This Geo location.";
                        if (locationLockResponse.get("status") != null) {
                            responseCode = (Integer) locationLockResponse.get("status");
                        }
                        if (locationLockResponse.get("message") != null) {
                            responseMessage = (String) locationLockResponse.get("message");
                        }
                        loginSessionResponse.setResponeCode(responseCode);
                        loginSessionResponse.setResponseMessage(responseMessage);
                        loginSessionResponse.setRequestId(requestId);
                        response.setLoginSession(loginSessionResponse);
                        log.info("Method velidateLogingsession completed in {}ms for username: {}", System.currentTimeMillis() - startTime, userName);
                        return response;
                    }
                    log.info("Login validation successful for username: {}", userName);
                    responseCode = SoapConstants.SUCCESS_CODE;
                    responseMessage = "COA successfully";
                }
            } else {
                log.warn("User not found in SPR table for username: {}", userName);
                responseCode = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE;
                responseMessage = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE;
            }
            loginSessionResponse.setResponeCode(responseCode);
            loginSessionResponse.setResponseMessage(responseMessage);
            loginSessionResponse.setRequestId(requestId);
            response.setLoginSession(loginSessionResponse);
        } catch (FeignException e) {
            loginSessionResponse.setResponeCode(SoapConstants.INPUT_NOT_MATCH_CODE);
            loginSessionResponse.setResponseMessage(e.getMessage());
            loginSessionResponse.setRequestId(requestId);
            response.setLoginSession(loginSessionResponse);
            log.error("FeignException for username: {}, Error: {}", userName, e.getMessage());
        } catch (Exception e) {
            loginSessionResponse.setResponeCode(responseCode);
            loginSessionResponse.setResponseMessage(responseMessage);
            loginSessionResponse.setRequestId(requestId);
            response.setLoginSession(loginSessionResponse);
            log.error("Unexpected error for username: {}, Error: {}", userName, e.getMessage(), e);
        }
        log.info("Method velidateLogingsession completed in {}ms for username: {}", System.currentTimeMillis() - startTime, userName);
        return response;
    }
//    public LoginSessionResponse velidateLogingsession(@RequestPayload LoginSession request) {
//        LoginSessionResponse wsLoginSessionResponse = new LoginSessionResponse();
//        String userName = request.getUserName().trim();
//        String password = request.getPassword().trim();
//        String ipAddress = request.getIpAddress().trim();
//        String responseMessage = SoapConstants.FAILURE;
//        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
//        GenericResponse<Object> response = new GenericResponse<>();
//        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
//        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
//        wsLoginSessionResponse.setRequestId(requestId);
//        if (userName == null || userName.isEmpty()) {
//            responseCode = SoapConstants.EMPTY;
//            responseMessage = "Input Username is Empty or Null.";
//            wsLoginSessionResponse.setResponeCode(responseCode);
//            wsLoginSessionResponse.setResponseMessage(responseMessage);
//            wsLoginSessionResponse.setRequestId(requestId);
//            return wsLoginSessionResponse;
//        }
//        try {
//            Map<String, String> payload = new HashMap<String, String>();
//            payload.put("username", userName);
//            payload.put("password", password);
//            payload.put("name", "mtik");
//            payload.put("sa", "2");
//            payload.put("framed-ip-address", ipAddress);
//
//            userName = userName.toLowerCase().trim();
//            Boolean checkIpAddress = loginSessionService.checkIpAddress(ipAddress);
//            if (!checkIpAddress) {
//                responseCode = SoapConstants.NOT_AVAILABLE;
//                responseMessage = "No Records Found in session table for give IPAddress.";
//                wsLoginSessionResponse.setResponeCode(responseCode);
//                wsLoginSessionResponse.setResponseMessage(responseMessage);
//                wsLoginSessionResponse.setRequestId(requestId);
//                return wsLoginSessionResponse;
//            }
//            GenericDataDTO genericDataDTO = radiusClients.getCustomerDetails(userName, SoapConstants.MVNOID);
//            if (genericDataDTO.getData() instanceof Map) {
//                Map<String, Object> dataMap = (Map<String, Object>) genericDataDTO.getData();
//                String liveUsername = dataMap.get("username").toString();
//                String userStatus = dataMap.get("status").toString();
//                String userPassword = dataMap.get("password").toString();
//                if ((userStatus != null && Objects.nonNull(userStatus)) && !userStatus.equalsIgnoreCase("Active") && !userStatus.equalsIgnoreCase("suspend")) {
//                    responseCode = SoapConstants.STATUS_INACTIVEC_CODE;
//                    responseMessage = "User in Inactive status in SPR.";
//                    wsLoginSessionResponse.setResponeCode(responseCode);
//                    wsLoginSessionResponse.setResponseMessage(responseMessage);
//                    wsLoginSessionResponse.setRequestId(requestId);
//                    return wsLoginSessionResponse;
//                } else if ((userPassword != null && !userPassword.isEmpty()) && !userPassword.equalsIgnoreCase(password)) {
//                    responseCode = SoapConstants.INPUT_NOT_MATCH_CODE;
//                    responseMessage = "Input Password is not match with Username.";
//                    wsLoginSessionResponse.setResponeCode(responseCode);
//                    wsLoginSessionResponse.setResponseMessage(responseMessage);
//                    wsLoginSessionResponse.setRequestId(requestId);
//                    return wsLoginSessionResponse;
//                } else if (!liveUsername.equalsIgnoreCase(userName)) {
//                    responseCode = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE;
//                    responseMessage = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE;
//                    wsLoginSessionResponse.setResponeCode(responseCode);
//                    wsLoginSessionResponse.setResponseMessage(responseMessage);
//                    wsLoginSessionResponse.setRequestId(requestId);
//                    return wsLoginSessionResponse;
//                } else {
//                    Map<String, Object> locationLockResponse = radiusClientService.getLocationLockResponse(payload, SoapConstants.MVNOID, token);
//                    boolean checkLocationLock = false;
//                    if (locationLockResponse.get("data") != null) {
//                        checkLocationLock = (boolean) locationLockResponse.get("data");
//                    }
//                    if (!checkLocationLock) {
//                        responseCode = SoapConstants.USER_NOT_ALLOW_CODE;
//                        responseMessage = "User is not allow service at This Geo location.";
//                        if (locationLockResponse.get("status") != null) {
//                            responseCode = (Integer) locationLockResponse.get("status");
//                        }
//                        if (locationLockResponse.get("message") != null) {
//                            responseMessage = (String) locationLockResponse.get("message");
//                        }
//                        wsLoginSessionResponse.setResponeCode(responseCode);
//                        wsLoginSessionResponse.setResponseMessage(responseMessage);
//                        wsLoginSessionResponse.setRequestId(requestId);
//                        return wsLoginSessionResponse;
//                    }
//                    responseCode = SoapConstants.SUCCESS_CODE;
//                    responseMessage = "COA successfully";
//                }
//            } else {
//                responseCode = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE;
//                responseMessage = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE;
//            }
//            wsLoginSessionResponse.setResponeCode(responseCode);
//            wsLoginSessionResponse.setResponseMessage(responseMessage);
//            wsLoginSessionResponse.setRequestId(requestId);
//        } catch (FeignException e) {
//            wsLoginSessionResponse.setResponeCode(SoapConstants.INPUT_NOT_MATCH_CODE);
//            wsLoginSessionResponse.setResponseMessage(e.getMessage());
//            wsLoginSessionResponse.setRequestId(requestId);
//            logger.error("error message : " + e.getMessage());
//        } catch (Exception e) {
//            wsLoginSessionResponse.setResponeCode(responseCode);
//            wsLoginSessionResponse.setResponseMessage(responseMessage);
//            wsLoginSessionResponse.setRequestId(requestId);
//            logger.error("error message : " + e.getMessage());
//        }
//        return wsLoginSessionResponse;
//    }

    /*
    public DOMSource generateLoginSessionSOAPResponse(WsLoginSessionResponse response) throws SOAPException, ParserConfigurationException {
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
        SOAPElement responseElement = body.addChildElement("wsLoginSessionResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add LoginSession element
        SOAPElement loginSessionElement = responseElement.addChildElement("LoginSession");

        // Add child elements to LoginSession
        loginSessionElement.addChildElement("requestId").addTextNode(getSafeText(response.getRequestId()));
        loginSessionElement.addChildElement("responeCode").addTextNode(getSafeNumber(response.getResponeCode()));
        loginSessionElement.addChildElement("responseMessage").addTextNode(getSafeText(response.getResponseMessage()));

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

    // Use this both success and Exception response generator method without interceptor
    // First test for checking sheet response and adjust parameter value by taking from response class

    /**
     * Creates a SOAP 1.1 success and exception both response for LoginSession with details.
     *
     * @param response       The response object containing login session details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP success response.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateLoginSessionSAOP11SuccessAndExceptionResponse(WsLoginSessionResponse response, MessageContext messageContext) throws SOAPException {
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

        SOAPElement responseElement = body.addChildElement("wsLoginSessionResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement loginSession = responseElement.addChildElement("LoginSession");

        loginSession.addChildElement("requestId").addTextNode(response.getRequestId());
        loginSession.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        loginSession.addChildElement("responseMessage").addTextNode(response.getResponseMessage());

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

//    public DOMSource generateLoginSessionSAOP11SuccessAndExceptionResponse(LoginSessionResponse response, MessageContext messageContext) throws SOAPException {
//        // Create a SOAP Message factory for SOAP 1.1 protocol
//        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
//        SOAPMessage soapMessage = factory.createMessage();
//        SOAPPart soapPart = soapMessage.getSOAPPart();
//        SOAPEnvelope envelope = soapPart.getEnvelope();
//
//        envelope.removeNamespaceDeclaration("SOAP-ENV");
//        envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
//        envelope.setPrefix("soap");
//
//        SOAPBody body = envelope.getBody();
//        body.setPrefix("soap");
//        SOAPHeader header = envelope.getHeader();
//        if (header != null) {
//            header.detachNode();
//        }
//
//        SOAPElement responseElement = body.addChildElement("LoginSessionResponse", "ns2", "http://api.act.com/");
//        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");
//
//        SOAPElement loginSession = responseElement.addChildElement("LoginSession");
//
//        loginSession.addChildElement("requestId").addTextNode(response.getRequestId());
//        loginSession.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
//        loginSession.addChildElement("responseMessage").addTextNode(response.getResponseMessage());
//
//        SaajSoapMessage updateResponse = (SaajSoapMessage) messageContext.getResponse();
//        updateResponse.setSaajMessage(soapMessage);
//        updateResponse.getSaajMessage().saveChanges();
//
//        Document document = soapMessage.getSOAPPart().getEnvelope().getOwnerDocument();
//        DocumentFragment fragment = document.createDocumentFragment();
//
//        NodeList childNodes = body.getChildNodes();
//        for (int i = 0; i < childNodes.getLength(); i++) {
//            fragment.appendChild(childNodes.item(i).cloneNode(true));
//        }
//
//        return new DOMSource(fragment);
//    }

}
