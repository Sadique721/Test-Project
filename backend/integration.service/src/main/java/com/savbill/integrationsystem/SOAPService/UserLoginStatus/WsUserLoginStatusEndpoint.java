package com.savbill.integrationsystem.SOAPService.UserLoginStatus;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.wsuserloginstatus.UserLoginStatus;
import com.savbill.integrationsystem.generated.wsuserloginstatus.UserLoginStatusResponse;
import com.savbill.integrationsystem.generated.wsuserloginstatus.WsUserLoginStatus;
import com.savbill.integrationsystem.generated.wsuserloginstatus.WsUserLoginStatusResponse;
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
import java.util.Map;
import java.util.Objects;

@Slf4j
@Endpoint
public class WsUserLoginStatusEndpoint {

    @Autowired
    private RadiusClientService radiusClientService;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsUserLoginStatus")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newuserloginstatus.WsUserLoginStatusResponse UserLoginStatusRequest(@RequestPayload WsUserLoginStatus request, MessageContext messageContext) throws SOAPException, IOException {
       // WsUserLoginStatusResponse response = null;
        com.savbill.integrationsystem.generated.newuserloginstatus.WsUserLoginStatusResponse response = new com.savbill.integrationsystem.generated.newuserloginstatus.WsUserLoginStatusResponse();
        long startTime = System.currentTimeMillis();

        try {
            response = userLoginStatus(request);
            log.info("handleUpdateUserUsage Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return response;
        } catch (Exception e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            response = userLoginStatus(request);
            log.info("handleUpdateUserUsage Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return response;

        }
    }

//    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "UserLoginStatus")
//    @ResponsePayload
//    public DOMSource UserLoginStatusRequest1(@RequestPayload UserLoginStatus request, MessageContext messageContext) throws SOAPException, IOException {
//        UserLoginStatusResponse response = null;
//        try {
//            response = userLoginStatus1(request);
//            return generateUserLoginStatusSOAP11SuccessAndExceptionResponse1(response, messageContext);
//        } catch (Exception e) {
//            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
//            return generateUserLoginStatusSOAP11SuccessAndExceptionResponse1(response, messageContext);
//        }
//    }

    public com.savbill.integrationsystem.generated.newuserloginstatus.WsUserLoginStatusResponse userLoginStatus(WsUserLoginStatus request) {
        com.savbill.integrationsystem.generated.newuserloginstatus.WsUserLoginStatusResponse response = new com.savbill.integrationsystem.generated.newuserloginstatus.WsUserLoginStatusResponse();
        com.savbill.integrationsystem.generated.newuserloginstatus.UserLoginStatusResponse status = new com.savbill.integrationsystem.generated.newuserloginstatus.UserLoginStatusResponse();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        Integer responsecode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = "FAILURE";
        long startTime = System.currentTimeMillis();
        log.info("Method:userLoginStatus Started In:{}", new Date(startTime));
        String userName = request.getUserName().trim();
        status.setRequestId(requestId);
        if (userName == null || userName.isEmpty()) {
            responsecode = SoapConstants.EMPTY;
            responseMessage = "Input Username is Empty or null.";
            log.warn("Input userName Is Null or empty");
            status.setResponeCode(responsecode);
            status.setResponseMessage(responseMessage);
            status.setRequestId(requestId);
            response.setUserLoginStatus(status);
            return response;
        }
        try {
            userName = request.getUserName().toLowerCase().trim();
            log.debug("Call Radius Client To Check User:{} Login Status", userName);
            GenericDataDTO genericDataDTO = radiusClientService.getLiveUserLoginStatus(userName, SoapConstants.MVNOID);
            Object data = genericDataDTO.getData();
            log.debug("Integration Received Response In:{}MS Response:{} ", System.currentTimeMillis() - startTime, genericDataDTO.getResponseMessage());

            if (Objects.nonNull(data)) {
                if (data instanceof Map) {
                    Map<String, Object> dataMap = (Map<String, Object>) data;
                    String liveUsername = dataMap.get("userName").toString();
                    if (liveUsername != null && !liveUsername.isEmpty() && liveUsername.equalsIgnoreCase(userName)) {
                        status.setResponeCode(SoapConstants.SUCCESS_CODE);
                        status.setResponseMessage("Session Is LoggedIN");
                        status.setResult(true);
                        log.info("User Login Status : Session Is LoggedIN for user : " + userName);
                        response.setUserLoginStatus(status);
                        return response;
                    }
                } else if (Objects.nonNull(genericDataDTO) && genericDataDTO.getResponseCode() == SoapConstants.UNKNOWN_PARAM) {
                    status.setResponeCode(SoapConstants.UNKNOWN_PARAM);
                    status.setResponseMessage("IP is available in session table with PARAM_STR9 is preauth : Ericsson/Huawei/Nokia");
                    status.setResult(false);
                    status.setRequestId(request.getRequestId());
                    response.setUserLoginStatus(status);
                    log.info("User Login Status : Session Is UNKNOW for user : " + userName);
                    return response;
                }
            }
            status.setResponeCode(SoapConstants.UNKNOWN_PARAM);
            status.setResponseMessage("IP is available in session table with PARAM_STR9 is preauth : Ericsson/Huawei/Nokia");
            status.setResult(false);
            status.setRequestId(request.getRequestId());
            log.info("Input UserName:{} Not Found In System",userName);
            response.setUserLoginStatus(status);
            return response;
        } catch (Exception e) {
            status.setResponeCode(HttpStatus.EXPECTATION_FAILED.value());
            status.setResponseMessage("Exception was encountered during processing Request.");
            status.setResult(false);
            status.setRequestId(request.getRequestId());
            response.setUserLoginStatus(status);
            log.error("Exception was encountered during processing Request.",e);
        }
        return response;
    }


    public UserLoginStatusResponse userLoginStatus1(UserLoginStatus request) {
        UserLoginStatusResponse response = new UserLoginStatusResponse();
        UserLoginStatusResponse.UserLoginStatus status = new UserLoginStatusResponse.UserLoginStatus();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        Integer responsecode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = "FAILURE";
        long startTime = System.currentTimeMillis();
        log.info("Starting method: userLoginStatus AT:{}", new Date(startTime));
        String userName = request.getUserName().trim();
        status.setRequestId(requestId);
        if (userName == null || userName.isEmpty()) {
            responsecode = SoapConstants.EMPTY;
            responseMessage = "Input Username is Empty or null.";
            status.setResponeCode(responsecode);
            status.setResponseMessage(responseMessage);
            status.setRequestId(requestId);
            response.setUserLoginStatus(status);
            log.warn("Input Username is Empty or null.");
            return response;
        }
        try {
            userName = request.getUserName().toLowerCase().trim();
            log.debug("Call Radius Client To Check User:{} Login Status", userName);
            GenericDataDTO genericDataDTO = radiusClientService.getLiveUserLoginStatus(userName, SoapConstants.MVNOID);
            log.debug("Integration Received Response In:{}MS User:{} Login ", System.currentTimeMillis() - startTime, userName);
            Object data = genericDataDTO.getData();
            if (Objects.nonNull(data)) {
                if (data instanceof Map) {
                    Map<String, Object> dataMap = (Map<String, Object>) data;
                    String liveUsername = dataMap.get("userName").toString();
                    if (liveUsername != null && !liveUsername.isEmpty() && liveUsername.equalsIgnoreCase(userName)) {
                        status.setResponeCode(SoapConstants.SUCCESS_CODE);
                        status.setResponseMessage("Session Is LoggedIN");
                        status.setResult(true);
                        log.info("User Login Status : Session Is LoggedIN for user : " + userName);
                        response.setUserLoginStatus(status);
                        return response;
                    }
                } else if (Objects.nonNull(genericDataDTO) && genericDataDTO.getResponseCode() == SoapConstants.UNKNOWN_PARAM) {
                    status.setResponeCode(SoapConstants.UNKNOWN_PARAM);
                    status.setResponseMessage("IP is available in session table with PARAM_STR9 is preauth : Ericsson/Huawei/Nokia");
                    status.setResult(false);
                    status.setRequestId(request.getRequestId());
                    response.setUserLoginStatus(status);
                    log.info("User Login Status : Session Is UNKNOW for user : " + userName);
                    return response;
                }
            }
            status.setResponeCode(SoapConstants.UNKNOWN_PARAM);
            status.setResponseMessage("IP is available in session table with PARAM_STR9 is preauth : Ericsson/Huawei/Nokia");
            status.setResult(false);
            status.setRequestId(request.getRequestId());
            log.info("User Login Status : " + genericDataDTO.getResponseMessage() + userName);

            response.setUserLoginStatus(status);
            return response;
        } catch (Exception e) {
            status.setResponeCode(HttpStatus.EXPECTATION_FAILED.value());
            status.setResponseMessage(SoapConstants.FAILURE);
            status.setResult(false);
            log.error("User Login Status : IP is available in session table with PARAM_STR9 is preauth : Ericsson/Huawei/Nokia : ");
        }
        return response;
    }

    /*
    public DOMSource generateUserLoginStatusSOAPResponse(WsUserLoginStatusResponse response) throws SOAPException, ParserConfigurationException {
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
        SOAPElement responseElement = body.addChildElement("wsUserLoginStatusResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        // Add UserLoginStatus element
        SOAPElement userLoginStatusElement = responseElement.addChildElement("UserLoginStatus");

        // Add child elements to UserLoginStatus
        if(response.getUserLoginStatus().getRequestId()==null || response.getUserLoginStatus().getRequestId().equals("?") || response.getUserLoginStatus().getRequestId().equals("") || response.getUserLoginStatus().getRequestId().equals(" ")){
            userLoginStatusElement.addChildElement("requestId").addTextNode("?");
        }else {
            userLoginStatusElement.addChildElement("requestId").addTextNode(getSafeText(response.getUserLoginStatus().getRequestId()));
        }
        userLoginStatusElement.addChildElement("responeCode").addTextNode(getSafeNumber(response.getUserLoginStatus().getResponeCode()));
        userLoginStatusElement.addChildElement("responseMessage").addTextNode(getSafeText(response.getUserLoginStatus().getResponseMessage()));
        userLoginStatusElement.addChildElement("result").addTextNode(getSafeText(String.valueOf(response.getUserLoginStatus().isResult())));

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

    /**
     * Generates a SOAP 1.1 response for user login status.
     * The response includes a status indicating whether the user is logged in or not.
     *
     * @param response       The response object containing user login status details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for user login status.
     * @throws SOAPException If there is an error creating the SOAP message.
     */
    public DOMSource generateUserLoginStatusSOAP11SuccessAndExceptionResponse(WsUserLoginStatusResponse response, MessageContext messageContext) throws SOAPException {
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
        SOAPElement userLoginStatusResponseElement = body.addChildElement("wsUserLoginStatusResponse", "ns2", "http://api.act.com/");
        userLoginStatusResponseElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement userLoginStatus = userLoginStatusResponseElement.addChildElement("UserLoginStatus");
        userLoginStatus.addChildElement("requestId").addTextNode(response.getUserLoginStatus().getRequestId() != null ? response.getUserLoginStatus().getRequestId() : "?");
        userLoginStatus.addChildElement("responeCode").addTextNode(String.valueOf(response.getUserLoginStatus().getResponeCode()));
        userLoginStatus.addChildElement("responseMessage").addTextNode(response.getUserLoginStatus().getResponseMessage());
        userLoginStatus.addChildElement("result").addTextNode(String.valueOf(response.getUserLoginStatus().isResult()));

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

    public DOMSource generateUserLoginStatusSOAP11SuccessAndExceptionResponse1(UserLoginStatusResponse response, MessageContext messageContext) throws SOAPException {
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
        SOAPElement userLoginStatusResponseElement = body.addChildElement("UserLoginStatusResponse", "ns2", "http://api.act.com/");
        userLoginStatusResponseElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement userLoginStatus = userLoginStatusResponseElement.addChildElement("UserLoginStatus");
        userLoginStatus.addChildElement("requestId").addTextNode(response.getUserLoginStatus().getRequestId() != null ? response.getUserLoginStatus().getRequestId() : "?");
        userLoginStatus.addChildElement("responeCode").addTextNode(String.valueOf(response.getUserLoginStatus().getResponeCode()));
        userLoginStatus.addChildElement("responseMessage").addTextNode(response.getUserLoginStatus().getResponseMessage());
        userLoginStatus.addChildElement("result").addTextNode(String.valueOf(response.getUserLoginStatus().isResult()));

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


