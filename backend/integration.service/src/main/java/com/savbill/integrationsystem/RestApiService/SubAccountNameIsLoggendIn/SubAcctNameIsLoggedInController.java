package com.savbill.integrationsystem.RestApiService.SubAccountNameIsLoggendIn;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class SubAcctNameIsLoggedInController {


    @Autowired
    private SubAcctNameIsLoggedInRestService subAcctNameIsLoggedInRestService;

    @PostMapping("/getsubAcctNameIsLoggedOn")
    public GenericResponse<Object> handleRequest(@RequestBody SubAccNameDto request) throws Exception {
        return subAcctNameIsLoggedInRestService.handleSubsAcctLogedIn(request);
    }


}
