package com.savbill.integrationsystem.RestApiService.ReauthSessionController;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class ReauthSessionController {

    @Autowired
    ReauthSessionService reauthSessionService;

    @PostMapping("/reAuthSession")
    public GenericResponse<Object> ReAuthSession(@RequestBody ReAuthSessionDto request) {
        return reauthSessionService.handleReauthSession(request);
    }
}
