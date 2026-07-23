package com.savbill.integrationsystem.SOAPService.SubAccountNameIsLoggendIn;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClient;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.transform.dom.DOMSource;

@Endpoint
public class SubAcctNameIsLoggedInEndPoint {

    @Autowired
    RadiusClient radiusClient;

    @Autowired
    SubAcctNameIsLoggedInSoapService subAcctNameIsLoggedInSoapService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "subAcctNameIsLoggedOn")
    @ResponsePayload
    public DOMSource handleRequest(@RequestPayload SubAcctNameIsLogged request, MessageContext messageContext) throws Exception {
        return subAcctNameIsLoggedInSoapService.handleSubAcctLoggedInRequest(request, messageContext);
    }

}
