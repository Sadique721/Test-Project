package com.savbill.integrationsystem.SOAPService.AddServiceToSubAcctName;

import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.addservicetosubacctname.AddServiceToSubAcctName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.transform.dom.DOMSource;

@Endpoint
public class AddServiceToSubAcctNameEndpoint {
    @Autowired
    private AddServiceToSubAcctNameService addServiceToAcctNameService;

    @Autowired
    private JwtUtil jwtUtil;


    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "addServiceToSubAcctName")
    @ResponsePayload
    public DOMSource handleRequest(@RequestPayload AddServiceToSubAcctName request, MessageContext messageContext) throws Exception {
        return addServiceToAcctNameService.handleAddServiceToSubAcctRequest(request,messageContext);
    }

}
