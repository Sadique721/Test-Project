package com.savbill.integrationsystem.SOAPService.GetSubscriberSession;

import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.getsubscribersession.GetSubscriberSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.transform.dom.DOMSource;

@Endpoint
public class GetSubscriberSessionEndPoint {

    @Autowired
    private GetSubscriberSessionSoapService subscriberSessionSoapService;

    @Autowired
    private JwtUtil jwtUtil;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "getSubscriberSession")
    @ResponsePayload
    public DOMSource handleRequest(@RequestPayload GetSubscriberSession request, MessageContext messageContext) throws Exception {
        try {
            return subscriberSessionSoapService.handleSubscriberSessionRequest(request,messageContext);

        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }


    }
}
