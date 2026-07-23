package com.savbill.integrationsystem.RestApiService.updateSubscriberAccountXML;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.service.ChangeServService;
import com.savbill.integrationsystem.SOAPService.updateSubscriberAccountXML.UpdateSubscriberAccountXMLService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class UpdateSubscriberAccountXMLControllor {
    @Autowired
    CmsClientService cmsClientService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    ChangeServService changeServService;
    @Autowired
    UpdateSubscriberAccountXMLService subscriberAccService;
    @PostMapping("/updateSubscriberAccountXML")
    public GenericDataDTO updateSubscriberAccount(@RequestBody UpdateSubscriberAccountXMLDTO request) {
        return subscriberAccService.update(request);
    }
}


