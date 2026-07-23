package com.savbill.integrationsystem.RestApiService.WsChangeTOpUpSubscription;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wschangeaddonsubscription.WsChangeAddOnSubscription;
import com.savbill.integrationsystem.generated.wschangetopupsubscription.WsChangeTopUpSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class WsChangeTopUpSubscriptionController {
    @Autowired
    private WsChangeTopUpSubscriptionRestService wsChangeTopUpSubscriptionRestService;

    @PostMapping("/WsChangeSubscribeTopUp")
    public GenericResponse<Object> handleRequest(@RequestBody WsChangeTopUpSubscription request) throws Exception {
        return wsChangeTopUpSubscriptionRestService.handleChangeSubscribeTopUp(request);
    }
}
