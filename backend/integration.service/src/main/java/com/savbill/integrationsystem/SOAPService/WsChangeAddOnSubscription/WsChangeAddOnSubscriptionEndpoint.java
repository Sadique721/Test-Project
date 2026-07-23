package com.savbill.integrationsystem.SOAPService.WsChangeAddOnSubscription;

import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.newwschangeaddonsubscription.WsChangeAddOnSubscriptionResponse;
import com.savbill.integrationsystem.generated.wschangeaddonsubscription.WsChangeAddOnSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class WsChangeAddOnSubscriptionEndpoint {
    @Autowired
    private WsChangeAddOnSubscriptionService wsChangeAddOnSubscriptionService;

    @Autowired
    private JwtUtil jwtUtil;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_ELITECORE, localPart = "wsChangeAddOnSubscription")
    @ResponsePayload
    public WsChangeAddOnSubscriptionResponse handleRequest(@RequestPayload WsChangeAddOnSubscription request, MessageContext messageContext) throws Exception {
        return wsChangeAddOnSubscriptionService.handleChangeAddOnSubscriptionRequest(request, messageContext);
    }

}
