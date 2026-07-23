package com.savbill.integrationsystem.RestApiService.chargeService;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.changeservice.WsChangeService;
import com.savbill.integrationsystem.generated.changeservice.WsChangeServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class ChangeServiceControllor {
    @Autowired
    private ChangeServiseServ changeServiseServ;

    @PostMapping("/changeService")
    public GenericResponse<Object> getChangeService(@RequestBody ChangeServiceRequest request) {
        log.info(" Request Received in ChangeServiceController for requestId: {}, userName: {}, serviceId: {}, overrides: {}", request.getRequestId(), request.getUserName(), request.getServiceId(),request.getOverrides());
        return changeServiseServ.getWsChange(request);
    }
}
