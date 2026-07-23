package com.savbill.integrationsystem.RestApiService.GetSubscriberSession;

import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.getsubscribersession.GetSubscriberSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class GetSubscriberSessionRestController {

    @Autowired
    private GetSubscriberSessionRestService subscriberSessionService;


    @GetMapping("/getSubsriberSession")
    public GenericDataDTO handleRequest(@RequestBody GetSubscriberSession request) {
        log.info("Received request to get subscriber session for IP address: {}", request.getString1());
        return subscriberSessionService.getSubscriberSession(request);
    }
}
