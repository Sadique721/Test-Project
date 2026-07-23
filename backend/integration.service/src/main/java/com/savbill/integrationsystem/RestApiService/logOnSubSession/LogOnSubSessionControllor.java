package com.savbill.integrationsystem.RestApiService.logOnSubSession;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClient;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.logOnSubSession.LogOnSubSessionService;
import com.savbill.integrationsystem.SOAPService.service.ChangeServService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class LogOnSubSessionControllor {
    @Autowired
    CmsClientService cmsClientService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    ChangeServService changeServService;
    @Autowired
    RadiusClient radiusClient;
    @Autowired
    LogOnSubSessionService logOnSubSessionService;
    @Autowired
    private RadiusClientService radiusClientService;

    private  final String LOG_ON_SUB_SESSION_RESPONSE= "logonSubSessionResponse";

    @PostMapping("/logOnSubSession")
    public GenericDataDTO logOnSubSession(@RequestBody LogOnSubSessionDTO request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            log.info("Request Received LogOnSubSessionController : " + request);
            return logOnSubSessionService.getLogOnSubSession(request);
        } catch (Exception e){
            log.error("Error in LogOnSubSessionController : " + e.getMessage());
            genericDataDTO.setResponseCode(500);
            genericDataDTO.setResponseMessage("Error in logonSubSession");
        }
        return genericDataDTO;
    }
}
