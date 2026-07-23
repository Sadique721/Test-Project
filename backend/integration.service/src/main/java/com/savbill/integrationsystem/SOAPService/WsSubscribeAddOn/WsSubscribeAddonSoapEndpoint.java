package com.savbill.integrationsystem.SOAPService.WsSubscribeAddOn;

import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.generated.newwssubscriberaddon.WsSubscribeAddOnResponse;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class WsSubscribeAddonSoapEndpoint {
    @Autowired
    private WsSubscribeAddonSoapService wsSubscribeAddonSoapService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_ELITECORE
            , localPart = "wsSubscribeAddOn")
    @ResponsePayload
    public WsSubscribeAddOnResponse handleRequest(@RequestPayload WsSubscribeAddOn request, MessageContext messageContext) throws Exception {
        return wsSubscribeAddonSoapService.handleSubscribeAddonRequest(request,messageContext);
    }
}
