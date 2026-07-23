package com.savbill.integrationsystem.SOAPService.WsChangeTopUpSubscription;

import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.newchangetopupsubscription.WsChangeTopUpSubscriptionResponse;
import com.savbill.integrationsystem.generated.wschangeaddonsubscription.WsChangeAddOnSubscription;
import com.savbill.integrationsystem.generated.wschangetopupsubscription.WsChangeTopUpSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class wsChangeTopUpSubscriptionEndpoint {

    @Autowired
    private wsChangeTopUpSubscriptionSoapService wsChangeTopUpSubscriptionSoapService;

    @Autowired
    private JwtUtil jwtUtil;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_ELITECORE, localPart = "wsChangeTopUpSubscription")
    @ResponsePayload
    public WsChangeTopUpSubscriptionResponse handleRequest(@RequestPayload WsChangeTopUpSubscription request, MessageContext messageContext) throws Exception {
        return wsChangeTopUpSubscriptionSoapService.handleChangeTopUpSubscriptionRequest(request,messageContext);
    }
}
