package com.savbill.integrationsystem.SOAPService.AddSubscriberAccountXML;

import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.addsubscriberaccountxml.AddSubscriberAccountXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.transform.dom.DOMSource;

@Endpoint
public class addSubscriberAccountXMLEndpoint {

    @Autowired
    private AddSubscriberAccountXMLSoapService addSubscriberAccountXMLSoapService;

    @Autowired
    private JwtUtil jwtUtil;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "addSubscriberAccountXML")
    @ResponsePayload
    public DOMSource handleRequest(@RequestPayload AddSubscriberAccountXML request , MessageContext messageContext) throws Exception {
        return addSubscriberAccountXMLSoapService.handleSubscriberSessionRequest(request, messageContext);
    }
}
