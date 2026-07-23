package com.savbill.integrationsystem.RestApiService.AddServiceToSubAcctName;

import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.addservicetosubacctname.AddServiceToSubAcctName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class AddServiceToSubAcctNameRestController {


    @Autowired
    private AddServiceToSubAcctNameRestService addServiceToSubAcctNameRestService;

    @PostMapping("/AddServiceToSubAcctName")
    public GenericDataDTO handleRequest(@RequestBody AddServiceToSubAccDto request) throws Exception {
        return addServiceToSubAcctNameRestService.handleAddServiceToSubAcctRequest(request);
    }

}
