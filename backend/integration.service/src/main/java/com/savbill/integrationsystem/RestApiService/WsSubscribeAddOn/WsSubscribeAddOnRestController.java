package com.savbill.integrationsystem.RestApiService.WsSubscribeAddOn;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class WsSubscribeAddOnRestController {

    @Autowired
    private WsSubscribeAddOnRestservice wsSubscribeAddOnRestservice;

    @PostMapping("/WsSubscribeAddOn")
    public GenericResponse handleRequest(@RequestBody WsSubscribeAddOn request) throws Exception {
        return wsSubscribeAddOnRestservice.handleSubscribeAddOn(request);
    }
}
