package com.savbill.integrationsystem.SOAPService.updateSubscriberAccountXML;


import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.addsubscriberaccountxml.AddSubscriberAccountXML;
import com.savbill.integrationsystem.generated.changeandapplyservicestosubacctnamexml.ChangeAndApplyServicesToSubAcctNameXML;
import com.savbill.integrationsystem.generated.updatesubscriberaccountxml.UpdateSubscriberAccountXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.transform.dom.DOMSource;


@Endpoint
public class UpdateSubscriberAccountXMLEndPoint {

    @Autowired
    private UpdateSubscriberAccountXMLService updateSubscriberAccountXMLService;

    @Autowired
    private JwtUtil jwtUtil;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "updateSubscriberAccountXML")
    @ResponsePayload
    public DOMSource handleRequest(@RequestPayload UpdateSubscriberAccountXML request, MessageContext messageContext) throws Exception {
        DOMSource domSource = null;
        try {
             domSource = updateSubscriberAccountXMLService.UpdateSubscriberAccount(request, messageContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return domSource;
    }
}