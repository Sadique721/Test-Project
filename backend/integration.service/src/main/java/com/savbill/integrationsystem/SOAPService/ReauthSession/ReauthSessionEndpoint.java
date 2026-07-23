package com.savbill.integrationsystem.SOAPService.ReauthSession;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.newwsreauthsession.SessionReAuthResponse;
import com.savbill.integrationsystem.generated.wsreauthsession.WsReauthSessionsBySubscriberIdentityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.soap.*;
import java.util.Date;

@Slf4j
@Endpoint
public class ReauthSessionEndpoint {

    @Autowired
    private RadiusClientService radiusClientService;

    @Autowired
    private JwtUtil jwtUtil;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW_SES, localPart = "wsReauthSessionsBySubscriberIdentity")
    @ResponsePayload
    public com.savbill.integrationsystem.generated.newwsreauthsession.WsReauthSessionsBySubscriberIdentityResponse handleReauthSession(@RequestPayload WsReauthSessionsBySubscriberIdentity request, MessageContext messageContext) throws Exception {
        com.savbill.integrationsystem.generated.newwsreauthsession.WsReauthSessionsBySubscriberIdentityResponse statusResponse = null;
        long startTime = System.currentTimeMillis();
        log.info("Starting method: handleReauthSession");
        try {
            statusResponse = handleReauthSession(request);
            log.info("Method handleReauthSession completed in {}ms", System.currentTimeMillis() - startTime);
            return createSOAP12SuccessResponse(statusResponse);
        } catch (Exception e) {
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "Exception was encountered during processing Request.";
            log.info("Method handleReauthSession completed in {}ms", System.currentTimeMillis() - startTime);
            return createSOAP12SuccessResponse(statusResponse);
        }
    }

    public com.savbill.integrationsystem.generated.newwsreauthsession.WsReauthSessionsBySubscriberIdentityResponse handleReauthSession(WsReauthSessionsBySubscriberIdentity request) {

        com.savbill.integrationsystem.generated.newwsreauthsession.WsReauthSessionsBySubscriberIdentityResponse response = new com.savbill.integrationsystem.generated.newwsreauthsession.WsReauthSessionsBySubscriberIdentityResponse();

        SessionReAuthResponse reauth = new SessionReAuthResponse();

        String username = request.getSubscriberId();

        long startTime = System.currentTimeMillis();

        log.info("Starting method: handleReauthSession At:{}", new Date(startTime));
        try {
            if (username == null || username.isEmpty()) {
                reauth.setResponseCode(SoapConstants.EMPTY);
                reauth.setResponseMessage("INPUT PARAMETER MISSING. Reason: Identity parameter missing");
                response.setReturn(reauth);
                log.warn("SubscriberId Is Null Or Empty");
                return response;
            }
            log.debug("Call RadiusClient for ReAuthSession:{}", username);
            GenericDataDTO radiusResponse = radiusClientService.ReAuthSession(username, SoapConstants.MVNOID);
            log.debug("Integration Received Response At:{}MS ,Response:{}", System.currentTimeMillis() - startTime, radiusResponse.getData());
            if (radiusResponse.getData() == null) {
                reauth.setResponseCode(SoapConstants.NOT_FOUND);
                reauth.setResponseMessage("NOT FOUND. Unable to re-auth session(s) by subscriber Id:" + username + ". Reason: Session not found while performing Re-Auth for Id: " + username);
                response.setReturn(reauth);
                log.warn("SubscriberId Not Found IN System");
                return response;
            }
            int status = radiusResponse.getResponseCode();
            if (200 == status) {
                reauth.setResponseCode(SoapConstants.SUCCESS_CODE);
                reauth.setResponseMessage(SoapConstants.SUCCESS);
                log.info("Successfully re-auth session for subscriber: {}", username);
            } else if (404 == status) {
                reauth.setResponseCode(404);
                reauth.setResponseMessage("NOT FOUND. Unable to re-auth session(s) by subscriber Id:" + username + ". Reason: Session not found while performing Re-Auth for Id: " + username);
                log.warn("re-auth sessions Not happen Due To Session Not Found for subscriber: {}", username);
            } else {
                reauth.setResponseCode(SoapConstants.NOT_FOUND);
                reauth.setResponseMessage("Unexpected status from radius: " + status);
                log.warn("Unexpected status Received from radius for subscriber: {}", username);

            }
        } catch (Exception e) {
            e.printStackTrace();
            reauth.setResponseCode(SoapConstants.NOT_FOUND);
            reauth.setResponseMessage("An error occurred while processing the request");
            log.error("An error occurred while processing the request Error: " + e);
        }
        response.setReturn(reauth);
        return response;
    }

    public com.savbill.integrationsystem.generated.newwsreauthsession.WsReauthSessionsBySubscriberIdentityResponse createSOAP12SuccessResponse(com.savbill.integrationsystem.generated.newwsreauthsession.WsReauthSessionsBySubscriberIdentityResponse response) throws SOAPException {
        com.savbill.integrationsystem.generated.newwsreauthsession.WsReauthSessionsBySubscriberIdentityResponse wsReauthSessionsBySubscriberIdentityResponse = new com.savbill.integrationsystem.generated.newwsreauthsession.WsReauthSessionsBySubscriberIdentityResponse();
        SessionReAuthResponse return1 = new SessionReAuthResponse();
        return1.setResponseCode(response.getReturn().getResponseCode());
        return1.setResponseMessage(response.getReturn().getResponseMessage());
        wsReauthSessionsBySubscriberIdentityResponse.setReturn(return1);
        return wsReauthSessionsBySubscriberIdentityResponse;
    }

//        public DOMSource generateRauthSessionsSOAPResponse(WsReauthSessionsBySubscriberIdentityResponse response, MessageContext messageContext, boolean isSuccess) throws SOAPException, IOException {
//        SOAPMessage soapMessage;
//
//        // Based on isSuccess flag, either create success or exception response
//        if (isSuccess) {
//            soapMessage = createSOAP12SuccessResponse(response);
//        } else {
//            soapMessage = createSOAP12ExceptionResponse(response);
//        }
//
//        SaajSoapMessage saajResponse = (SaajSoapMessage) messageContext.getResponse();
//        saajResponse.setSaajMessage(soapMessage);
//        saajResponse.getSaajMessage().saveChanges();
//
//        SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();
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


//    public SOAPMessage createSOAP12SuccessResponse(WsReauthSessionsBySubscriberIdentityResponse response) throws SOAPException {
//        // Create a SOAP Message factory for SOAP 1.2
//        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
//        SOAPMessage soapMessage = factory.createMessage();
//
//        // Get the SOAP Envelope
//        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
//
//        // Remove the default "env" prefix and set "soap" as the prefix
//        envelope.removeNamespaceDeclaration("env");
//        envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
//        envelope.setPrefix("soap"); // Set "soap" as the envelope's prefix
//        envelope.getBody().setPrefix("soap");
//
//        SOAPHeader header = envelope.getHeader();
//        if (header != null) {
//            header.detachNode();
//        }
//        // Build the SOAP Body content
//        SOAPBody body = envelope.getBody();
//        SOAPElement responseElement = body.addChildElement("wsReauthSessionsBySubscriberIdentityResponse", "ns2", "http://sessionmanagement.ws.nvsmx.elitecore.com/");
//        SOAPElement returnElement = responseElement.addChildElement("return");
//        returnElement.addChildElement("responseCode").addTextNode(String.valueOf(response.getReturn().getResponseCode()));
//        returnElement.addChildElement("responseMessage").addTextNode(response.getReturn().getResponseMessage());
//
//        // Save changes to the SOAP message
//        soapMessage.saveChanges();
//
//        return soapMessage;
//    }


    public SOAPMessage createSOAP12ExceptionResponse(WsReauthSessionsBySubscriberIdentityResponse response) throws SOAPException {
        // Create a SOAP Message factory for SOAP 1.2
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();

        // Get the SOAP Envelope
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();

        // Remove the default "env" prefix and set "soap" as the prefix
        envelope.removeNamespaceDeclaration("env");
        envelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");
        envelope.setPrefix("soap"); // Set "soap" as the envelope's prefix
        envelope.getBody().setPrefix("soap");
        envelope.getHeader().setPrefix("soap");

        SOAPHeader header = envelope.getHeader();
        if (header != null) {
            header.detachNode();
        }
        // Build the SOAP Body content
        SOAPBody body = envelope.getBody();
        SOAPElement responseElement = body.addChildElement("wsReauthSessionsBySubscriberIdentityResponse", "ns2", "http://sessionmanagement.ws.nvsmx.elitecore.com/");
        SOAPElement returnElement = responseElement.addChildElement("return");

        // Add custom error response code and message (404 error)
        returnElement.addChildElement("responseCode").addTextNode(String.valueOf(response.getReturn().getResponseCode()));
        returnElement.addChildElement("responseMessage").addTextNode(response.getReturn().getResponseMessage());

        // Save changes to the SOAP message
        soapMessage.saveChanges();

        return soapMessage;
    }

}
