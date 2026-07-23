package com.savbill.integrationsystem.RestApiService.changeandapplyService;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
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

public class ChangeAndApplyServiceController {
    @Autowired
    private ChangeAndApplyServiceService changeAndApplyServiceService;

    @PostMapping("/ChangeAndApplyService")
    public GenericResponse<Object> handleRequest(@RequestBody ChangeAndApplyServiceDTO request) throws Exception {
        log.info("ChangeAndApplyServiceController User: {}", request.getString_1());
        return changeAndApplyServiceService.handleChangeAndApplyService(request);
    }
}