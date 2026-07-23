package com.savbill.integrationsystem.SOAPService.RemoveService;

import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.generated.removeservice.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.transform.dom.DOMSource;
@Endpoint
public class RemoveServiceEndPoint {

    @Autowired
    private RemoveServiceSoapService removeServiceSoapService;

    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "removeService")
    @ResponsePayload
    public DOMSource handleRequest(@RequestPayload RemoveService request, MessageContext messageContext) throws Exception {
        return removeServiceSoapService.handleRemoveServiceRequest(request, messageContext);
    }
}
