package com.savbill.integrationsystem.RestApiService.logginSession;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClient;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.service.LoginSessionService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wslogingsession.WsLoginSessionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class LoginSessionControllor {

    @Autowired
    private LoginSessionService loginSessionService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RadiusClient radiusClients;
    private final Logger logger = LoggerFactory.getLogger(LoginSessionControllor.class);
    @Autowired
    private RadiusClientService radiusClientService;
    @PostMapping("/velidateLogingsession")
    public GenericDataDTO velidateLogingsession(@RequestBody LoginSessionRequest request) {
        WsLoginSessionResponse wsLoginSessionResponse = new WsLoginSessionResponse();
        GenericDataDTO response = new GenericDataDTO();
        try {
            wsLoginSessionResponse= loginSessionService.loginSession(request);
            if(Objects.nonNull(response)){
                response.setResponseMessage(wsLoginSessionResponse.getResponseMessage());
                response.setResponseCode(wsLoginSessionResponse.getResponeCode());
                response.setData(wsLoginSessionResponse);
            }else {
                response.setResponseMessage(wsLoginSessionResponse.getResponseMessage());
                response.setResponseCode(wsLoginSessionResponse.getResponeCode());
                response.setData(wsLoginSessionResponse);
            }
        }catch (Exception e){
            response.setResponseMessage(e.getMessage());
            response.setResponseCode(500);
            response.setData(wsLoginSessionResponse);
        }
        return response;
    }
}
