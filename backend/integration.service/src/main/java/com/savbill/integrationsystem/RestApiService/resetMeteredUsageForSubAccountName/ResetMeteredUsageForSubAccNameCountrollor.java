package com.savbill.integrationsystem.RestApiService.resetMeteredUsageForSubAccountName;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class ResetMeteredUsageForSubAccNameCountrollor {
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReserMeteredUsageForSubAccNameService reserMeteredUsageForSubAccNameService;
    @PostMapping("/resetMeteredUsageForSubAccName")
    public GenericDataDTO resetMeteredUsageForSubAccName(@RequestBody ResetMeteredUsageForSubAcctNameDTO request) throws Exception {
        return reserMeteredUsageForSubAccNameService.resetMeteredUsageForSubAccName(request);
    }
}
