package com.savbill.integrationsystem.SOAPService.ListAddOnSubscription;


import com.savbill.integrationsystem.generated.wslistaddonsubscriptions.WsListAddOnSubscriptions;
import com.savbill.integrationsystem.generated.wslistaddonsubscriptions.WsListAddOnSubscriptionsResponse;
import com.savbill.integrationsystem.generated.wslisttopupsubscriptions.WsListTopUpSubscriptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ListAddOnSubscriptionEndPoint {

    @Autowired
    private ListAddOnSubscriptionsService service;

    @PayloadRoot(namespace = "http://subscription.ws.nvsmx.elitecore.com/", localPart = "wsListAddOnSubscriptions")
    @ResponsePayload
    public WsListAddOnSubscriptionsResponse getBalanceList(@RequestPayload WsListAddOnSubscriptions request, MessageContext messageContext) throws Exception {
        return service.getData(request);
    }
}

