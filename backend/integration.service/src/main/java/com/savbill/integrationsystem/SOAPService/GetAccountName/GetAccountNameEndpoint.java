package com.savbill.integrationsystem.SOAPService.GetAccountName;


import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.newgetaccountname.GetAccountNameResponse;
import com.savbill.integrationsystem.generated.wsgetaccountname.GetAccountName;
import com.savbill.integrationsystem.generated.wsgetaccountname.WsGetAccountName;
import com.savbill.integrationsystem.generated.wsgetaccountname.WsGetAccountNameResponse;
import feign.FeignException;
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
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Endpoint
public class GetAccountNameEndpoint {

    @Autowired
    private RadiusClientService radiusClientService;

    @Autowired
    private JwtUtil jwtUtil;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "wsGetAccountName")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newgetaccountname.WsGetAccountNameResponse getAccountNameResponse(@RequestPayload WsGetAccountName request, MessageContext messageContext) throws SOAPException, IOException {
        WsGetAccountNameResponse response = null;
        try {
            return getAccountName(request);
        } catch (Exception e) {
            return getAccountName(request);
        }
    }
//    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI, localPart = "GetAccountName")
//    @ResponsePayload
//    public DOMSource getAccountNameResponse1(@RequestPayload GetAccountName request, MessageContext messageContext) throws SOAPException, IOException {
//        GetAccountNameResponse response = null;
//        try {
//            response = getAccountName1(request);
//            return generateGetAccountNameSAOP11SuccessResponse1(response, messageContext);
//        } catch (Exception e) {
//            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
//            return generateGetAccountNameSAOP11InvalidIpResponse1(response, messageContext);
//        }
//    }

    public com.savbill.integrationsystem.generated.newgetaccountname.WsGetAccountNameResponse getAccountName(WsGetAccountName request) {
        com.savbill.integrationsystem.generated.newgetaccountname.WsGetAccountNameResponse response = new com.savbill.integrationsystem.generated.newgetaccountname.WsGetAccountNameResponse();
        GetAccountNameResponse getAccountNameResponse = new GetAccountNameResponse();
        String ipAddress = request.getIpAddress().trim();
        Long mvnoId = SoapConstants.MVNOID;
        String token = jwtUtil.generateJwtToken(mvnoId);
        String requestId = request.getRequestId();
        getAccountNameResponse.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
        long startTime = System.currentTimeMillis();
        log.info("Starting method getAccountName for IP address: {} At: {}", request.getIpAddress(), new Date(startTime));
        try {
            if (ipAddress == null || ipAddress.isEmpty()) {
                log.warn("IP address validation failed: Empty or null IP address");
                getAccountNameResponse.setResponeCode(SoapConstants.EMPTY);
                getAccountNameResponse.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_NULL_DOT);
                response.setGetAccountName(getAccountNameResponse);
                log.info("Method getAccountName completed in {}ms for IP address: {}", System.currentTimeMillis() - startTime, ipAddress);
                return response;
            }
            if (!isValidIPAddress(ipAddress)) {
                log.warn("Invalid IP address format: {}", ipAddress);
                getAccountNameResponse.setResponeCode(SoapConstants.InvalidActivation);
                getAccountNameResponse.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_INVALID);
                response.setGetAccountName(getAccountNameResponse);
                log.info("Method getAccountName completed in {}ms for IP address: {}", System.currentTimeMillis() - startTime, ipAddress);
                return response;
            } else {
                log.debug("Calling RadiusClient for Get Account Name: {}", ipAddress);
                GenericDataDTO genericDataDTO = radiusClientService.GetAccountNameApi(ipAddress, SoapConstants.MVNOID);
                String data = genericDataDTO.getData() != null ? genericDataDTO.getData().toString() : null;
                log.debug("Integration Received Response In:{}MS,Resposne:{}",System.currentTimeMillis()-startTime,data);
               if(data == null || data.isEmpty()){
                   log.warn("No records found for IP address: {}", ipAddress);
                   getAccountNameResponse.setRequestId(requestId);
                   getAccountNameResponse.setResponeCode(SoapConstants.NOT_AVAILABLE);
                   getAccountNameResponse.setResponseMessage("No Records Found in session table for give IPAddress.");
                   response.setGetAccountName(getAccountNameResponse);
                   log.info("Method getAccountName completed in {}ms for IP address: {}", System.currentTimeMillis() - startTime, ipAddress);
                   return response;
               }
                if (data != null) {
                    if (data.equalsIgnoreCase(SoapConstants.UNKNOWN_DATA)) {
                        log.warn("Unknown username found for IP address: {} ", ipAddress);
                        getAccountNameResponse.setAccountName(SoapConstants.UNKNOWN_DATA);
                        getAccountNameResponse.setRequestId(requestId);
                        getAccountNameResponse.setResponeCode(SoapConstants.UNKNOWN);
                        getAccountNameResponse.setResponseMessage(SoapConstants.UNKNOWN_USERNAME_FOUND_AGAINST_INPUT_IP_ADDRESS_FOR_LOGIN_SESSION_WITHOUT_DOT);
                        response.setGetAccountName(getAccountNameResponse);
                        log.info("Method getAccountName completed in {}ms for IP address: {}", System.currentTimeMillis() - startTime, ipAddress);
                        return response;
                    } else {
                        log.info("Successfully retrieved account name for IP address: {}", ipAddress);
                        getAccountNameResponse.setAccountName(genericDataDTO.getData().toString());
                        getAccountNameResponse.setRequestId(requestId);
                        getAccountNameResponse.setResponeCode(SoapConstants.SUCCESS_CODE);
                        getAccountNameResponse.setResponseMessage(SoapConstants.SUCCESS);
                        response.setGetAccountName(getAccountNameResponse);
                        log.info("Method getAccountName completed in {}ms for IP address: {}", System.currentTimeMillis() - startTime, ipAddress);
                        return response;
                    }
                } else {
                    log.warn("Null data received for IP address: {}", ipAddress);
                    getAccountNameResponse.setResponeCode(SoapConstants.EMPTY);
                    getAccountNameResponse.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_NULL_DOT);
                    response.setGetAccountName(getAccountNameResponse);
                }
            }
        } catch (FeignException.FeignClientException e) {
            log.error("FeignClientException for IP address: {}, Error: {}", ipAddress, e.getMessage());
            getAccountNameResponse.setResponeCode(SoapConstants.INTERNAL_ERROR);
            getAccountNameResponse.setResponseMessage(e.getMessage());
            response.setGetAccountName(getAccountNameResponse);
            e.printStackTrace();
        } catch (NullPointerException e) {
            log.error("NullPointerException for IP address: {}, Error: {}", ipAddress, e.getMessage());
            getAccountNameResponse.setResponeCode(SoapConstants.INTERNAL_ERROR);
            getAccountNameResponse.setResponseMessage(e.getMessage());
            response.setGetAccountName(getAccountNameResponse);
            e.printStackTrace();
        } catch (Exception e) {
            log.error("Unexpected error for IP address: {}, Error: {}", ipAddress, e.getMessage(), e);
            getAccountNameResponse.setResponeCode(SoapConstants.INTERNAL_ERROR);
            getAccountNameResponse.setResponseMessage("Server Error: An unexpected error occurred.");
            response.setGetAccountName(getAccountNameResponse);
            e.printStackTrace();
        }
        log.info("Method getAccountName completed in {}ms for IP address: {}", System.currentTimeMillis() - startTime, ipAddress);
        return response;
    }
//    public GetAccountNameResponse getAccountName1(GetAccountName request) {
//        GetAccountNameResponse response = new GetAccountNameResponse();
//
//        String ipAddress = request.getIpAddress().trim();
//        Long mvnoId = SoapConstants.MVNOID;
//        String token = jwtUtil.generateJwtToken(mvnoId);
//        String requestId = request.getRequestId();
//        response.setRequestId((requestId != null && !requestId.trim().isEmpty()) ? requestId : "?");
//        WsGetAccountName wsGetAccountName = wsGetAccountNameMapper(request);
//        try {
//            if (ipAddress == null || ipAddress.isEmpty()) {
//                response.setResponeCode(SoapConstants.EMPTY);
//                response.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_NULL_DOT);
//                return response;
//            }
//            if (!isValidIPAddress(ipAddress)) {
//                response.setResponeCode(SoapConstants.InvalidActivation);
//                response.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_INVALID);
//                return response;
//            } else {
//                GenericDataDTO genericDataDTO = radiusClientService.GetAccountNameApi(ipAddress, SoapConstants.MVNOID);
//                String data = genericDataDTO.getData() != null ? genericDataDTO.getData().toString() : null;
//                if(data == null || data.isEmpty()){
//                    response.setRequestId(requestId);
//                    response.setResponeCode(SoapConstants.NOT_AVAILABLE);
//                    response.setResponseMessage("No Records Found in session table for give IPAddress.");
//                    return response;
//                }
//                if (data != null) {
//                    if (data.equalsIgnoreCase(SoapConstants.UNKNOWN_DATA)) {
//                        response.setAccountName(SoapConstants.UNKNOWN_DATA);
//                        response.setRequestId(requestId);
//                        response.setResponeCode(SoapConstants.UNKNOWN);
//                        response.setResponseMessage(SoapConstants.UNKNOWN_USERNAME_FOUND_AGAINST_INPUT_IP_ADDRESS_FOR_LOGIN_SESSION_WITHOUT_DOT);
//                        return response;
//                    } else {
//                        response.setAccountName(genericDataDTO.getData().toString());
//                        response.setRequestId(requestId);
//                        response.setResponeCode(SoapConstants.SUCCESS_CODE);
//                        response.setResponseMessage(SoapConstants.SUCCESS);
//                        return response;
//                    }
//                } else {
//                    response.setResponeCode(SoapConstants.EMPTY);
//                    response.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_NULL_DOT);
//                }
//            }
//        } catch (FeignException.FeignClientException e) {
//            response.setResponeCode(SoapConstants.INTERNAL_ERROR);
//            response.setResponseMessage(e.getMessage());
//            e.printStackTrace();
//        } catch (NullPointerException e) {
//            response.setResponeCode(SoapConstants.INTERNAL_ERROR);
//            response.setResponseMessage(e.getMessage());
//            e.printStackTrace();
//        } catch (Exception e) {
//            response.setResponeCode(SoapConstants.INTERNAL_ERROR);
//            response.setResponseMessage("Server Error: An unexpected error occurred.");
//            e.printStackTrace();
//        }
//
//        return response;
//
//    }

    /*
    public DOMSource generateGetAccountNameSOAPResponse(WsGetAccountNameResponse response) throws SOAPException {
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
        SOAPElement responseElement = body.addChildElement("wsGetAccountNameResponse", "ns2");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement accountName = responseElement.addChildElement("GetAccountName");

        // Add AccountName element if available
        if (response.getResponeCode()==200 || response.getResponeCode()==203){
            accountName.addChildElement("accountName").addTextNode(getSafeText(response.getAccountName()));
        }
        accountName.addChildElement("requestId").addTextNode(getSafeText(response.getRequestId()));
        accountName.addChildElement("responeCode").addTextNode(getSafeNumber(response.getResponeCode()));
        accountName.addChildElement("responseMessage").addTextNode(getSafeText(response.getResponseMessage()));

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

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }

    public Boolean chackipAddressInLiveUserCheck(String address) {
        try {
            GenericDataDTO radiusCheck = radiusClientService.checkUserSessionInRadiusClient(address, SoapConstants.MVNOID);
            if (radiusCheck.getData() != null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Use this both success and InvalidIp response generator method without interceptor
    // First test for checking sheet response and adjust parameter value by taking from response class

    /**
     * Creates a SOAP 1.1 success response for GetAccountName with account details.
     *
     * @param response       The response object containing account name details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP success response.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateGetAccountNameSAOP11SuccessResponse(WsGetAccountNameResponse response, MessageContext messageContext) throws SOAPException {
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

        SOAPElement responseElement = body.addChildElement("wsGetAccountNameResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");

        SOAPElement getAccountName = responseElement.addChildElement("GetAccountName");
        getAccountName.addChildElement("accountName").addTextNode(response.getAccountName());
        getAccountName.addChildElement("requestId").addTextNode(response.getRequestId());
        getAccountName.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        getAccountName.addChildElement("responseMessage").addTextNode(response.getResponseMessage());

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

    /**
     * Creates a SOAP 1.1 response for GetAccountName with an invalid IP address error.
     *
     * @param response       The response object containing account name details.
     * @param messageContext The message context for the current request.
     * @return DOMSource containing the SOAP response for invalid IP.
     * @throws SOAPException If there is an error in creating the SOAP message.
     */
    public DOMSource generateGetAccountNameSAOP11InvalidIpResponse(WsGetAccountNameResponse response, MessageContext messageContext) throws SOAPException {
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

        SOAPElement responseElement = body.addChildElement("wsGetAccountNameResponse", "ns2", "http://api.act.com/");
        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");


        SOAPElement getAccountName = responseElement.addChildElement("GetAccountName");
        getAccountName.addChildElement("requestId").addTextNode(response.getRequestId());
        getAccountName.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
        getAccountName.addChildElement("responseMessage").addTextNode(response.getResponseMessage());

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

    private WsGetAccountName wsGetAccountNameMapper(GetAccountName accountName) {
        WsGetAccountName wsGetAccountName = new WsGetAccountName();
        wsGetAccountName.setIpAddress(accountName.getIpAddress());
        wsGetAccountName.setRequestId(accountName.getRequestId());
        wsGetAccountName.setActionItem(accountName.getActionItem());


        return wsGetAccountName;
    }
//    public DOMSource generateGetAccountNameSAOP11SuccessResponse1(GetAccountNameResponse response, MessageContext messageContext) throws SOAPException {
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
//        SOAPElement responseElement = body.addChildElement("GetAccountNameResponse", "ns2", "http://api.act.com/");
//        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");
//
//        SOAPElement getAccountName = responseElement.addChildElement("GetAccountName");
//        getAccountName.addChildElement("accountName").addTextNode(response.getAccountName());
//        getAccountName.addChildElement("requestId").addTextNode(response.getRequestId());
//        getAccountName.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
//        getAccountName.addChildElement("responseMessage").addTextNode(response.getResponseMessage());
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
//    public DOMSource generateGetAccountNameSAOP11InvalidIpResponse1(GetAccountNameResponse response, MessageContext messageContext) throws SOAPException {
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
//        SOAPElement responseElement = body.addChildElement("GetAccountNameResponse", "ns2", "http://api.act.com/");
//        responseElement.addNamespaceDeclaration("ns3", "http://subscription.ws.nvsmx.elitecore.com/");
//
//
//        SOAPElement getAccountName = responseElement.addChildElement("GetAccountName");
//        getAccountName.addChildElement("requestId").addTextNode(response.getRequestId());
//        getAccountName.addChildElement("responeCode").addTextNode(String.valueOf(response.getResponeCode()));
//        getAccountName.addChildElement("responseMessage").addTextNode(response.getResponseMessage());
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
