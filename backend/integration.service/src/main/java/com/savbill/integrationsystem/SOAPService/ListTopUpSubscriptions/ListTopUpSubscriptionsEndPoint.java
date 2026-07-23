package com.savbill.integrationsystem.SOAPService.ListTopUpSubscriptions;

import com.savbill.integrationsystem.generated.wslisttopupsubscriptions.WsListTopUpSubscriptions;
import com.savbill.integrationsystem.generated.wslisttopupsubscriptions.WsListTopUpSubscriptionsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class ListTopUpSubscriptionsEndPoint {

    @Autowired
    private ListTopUpSubscriptionsService service;

    @PayloadRoot(namespace = "http://subscription.ws.nvsmx.elitecore.com/", localPart = "wsListTopUpSubscriptions")
    @ResponsePayload
    public WsListTopUpSubscriptionsResponse getBalanceList(@RequestPayload WsListTopUpSubscriptions request, MessageContext messageContext) throws Exception {
        return service.getData(request);
    }
}
