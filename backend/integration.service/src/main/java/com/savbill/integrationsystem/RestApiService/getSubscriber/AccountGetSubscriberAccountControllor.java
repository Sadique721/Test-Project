package com.savbill.integrationsystem.RestApiService.getSubscriber;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class AccountGetSubscriberAccountControllor {

    @Autowired
    private GetSubscriberAccXmlService getSubscriberAccXmlService;

    @PostMapping("/getSubscriberAccount")
    public GenericDataDTO handleRequest(@RequestBody GetSubscriberAccount request) {
        return getSubscriberAccXmlService.getSubscriberAccount(request);
    }
}