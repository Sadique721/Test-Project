package com.savbill.integrationsystem.SOAPService.resetMeteredUsageForSubAcctName;

import com.savbill.integrationsystem.RestApiService.resetMeteredUsageForSubAccountName.ReserMeteredUsageForSubAccNameService;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.resetmeteredusageforsubacctname.ResetMeteredUsageForSubAcctName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.transform.dom.DOMSource;

@Endpoint
public class ResetMeteredUsageForSubAcctNameEndPoint {

    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ReserMeteredUsageForSubAccNameService reserMeteredUsageForSubAccNameService;
    @PayloadRoot(namespace = SoapConstants.NAMESPACE_URI_NEW, localPart = "resetMeteredUsageForSubAcctName")
    @ResponsePayload
    public DOMSource resetMeteredUsageForSubAccName(@RequestPayload ResetMeteredUsageForSubAcctName request, MessageContext messageContext) throws Exception {

       return reserMeteredUsageForSubAccNameService.resetMeteredUsageForSubAccName(request,messageContext);

    }
}
