package com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML;

import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
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
public class ChangeAndApplyServicesToSubAcctNameXMLEndPoint {

    @Autowired
    private ChangeAndApplyServicesToSubAcctNameXMLService changeAndApplyServicesToSubAcctNameXMLService;

    @Autowired
    private JwtUtil jwtUtil;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "changeAndApplyServicesToSubAcctNameXML")
    @ResponsePayload
    public DOMSource handleRequest(@RequestPayload ChangeAndApplyServicesToSubAcctNameXML request, MessageContext messageContext) throws Exception {
        return changeAndApplyServicesToSubAcctNameXMLService.applyServicesToSubAcct(request,messageContext);
    }
}