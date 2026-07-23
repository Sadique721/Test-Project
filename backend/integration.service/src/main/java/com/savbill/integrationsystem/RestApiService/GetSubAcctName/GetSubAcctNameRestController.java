package com.savbill.integrationsystem.RestApiService.GetSubAcctName;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.core.utillity.URLConstants;

import com.savbill.integrationsystem.generated.getsubacctname.GetSubAcctName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class GetSubAcctNameRestController {

    @Autowired
    private GetSubAccNameService getSubAccNameService;

    @PostMapping("/getSubAccountName")
    public GenericResponse<Object> handleRequest(@RequestBody GetSubAccDto request) {
        log.info("Request Received in GetSubAcctNameRestController : {}", request);
        return getSubAccNameService.handleSubAccnameRequest(request);
    }
}
