package com.savbill.integrationsystem.RestApiService.removeSubscribeAccount;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.authenticateUserService.AuthenticatUserEndpoint;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class RemoveSubscriberAccountControllor {
    private final Logger logger = LoggerFactory.getLogger(AuthenticatUserEndpoint.class);
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    public CmsClient cmsClient;
    @Autowired
    private RadiusClientService radiusClientService;
    @Autowired
    private removeSubscriberserviceSrv removeSubscriberserviceSrv;

    @PostMapping("/removeSubscriberAccount")
    public GenericDataDTO removeSubscriberaccount(@RequestBody RemoveSubscriberAccountDto request) throws Exception {
        return removeSubscriberserviceSrv.removeSubscriberaccount(request);
    }

}
