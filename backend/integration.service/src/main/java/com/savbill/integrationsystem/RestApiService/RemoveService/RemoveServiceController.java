package com.savbill.integrationsystem.RestApiService.RemoveService;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.removeservice.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class RemoveServiceController {

    @Autowired
    private RemoveServiceRestService removeServiceRestService;

    @PostMapping("/removeService")
    public GenericResponse<Object> handleRequest(@RequestBody RemoveService request) throws Exception {
        return removeServiceRestService.handleRemoveService(request);
    }
}
