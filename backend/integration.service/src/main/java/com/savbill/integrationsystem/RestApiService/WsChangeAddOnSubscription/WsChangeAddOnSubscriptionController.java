package com.savbill.integrationsystem.RestApiService.WsChangeAddOnSubscription;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wschangeaddonsubscription.WsChangeAddOnSubscription;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class WsChangeAddOnSubscriptionController {

    @Autowired
    private WsChangeAddOnSubscriptionRestService wsChangeAddOnSubscriptionRestService;

    @PostMapping("/WsChangeSubscribeAddOn")
    public GenericResponse<Object> handleRequest(@RequestBody WsChangeAddOnSubscription request) throws Exception {
        return wsChangeAddOnSubscriptionRestService.handleChangeSubscribeAddOn(request);
    }
}
