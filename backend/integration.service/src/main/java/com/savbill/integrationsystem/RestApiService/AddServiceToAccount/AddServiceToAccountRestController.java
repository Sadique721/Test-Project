package com.savbill.integrationsystem.RestApiService.AddServiceToAccount;

import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccount;
import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccountResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class AddServiceToAccountRestController {

    @Autowired
    CmsClientService cmsClientService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AddServiceToAccountService addServiceToAccountService;

    @PostMapping("/addServiceToAccount")
    public GenericDataDTO addServiceToAccount(@RequestBody WsAddServiceToAccount request) {
        GenericDataDTO genericResponse = new GenericDataDTO();
        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;
        WsAddServiceToAccountResponse response = null;

        try {
            log.info("Received request to add service to account with ServiceId: {}", request.getServiceId());
            response = addServiceToAccountService.getWsAddServiceToAccount(request);
            if (Objects.isNull(response)) {
                log.error("No response returned from service for serviceId: {}", request.getServiceId());
                genericResponse.setResponseCode(responseCode);
                genericResponse.setResponseMessage(responseMessage);
            } else {
                responseCode = response.getAddServiceToAccount().getResponeCode();
                responseMessage = response.getAddServiceToAccount().getResponseMessage();
                genericResponse.setData(response);
                log.info("Successfully processed request for requestId: {}, Response Code: {}, Message: {}",
                        request.getRequestId(), responseCode, responseMessage);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing request for serviceID: {} ,username {} ", request.getServiceId(), e);
            responseCode = SoapConstants.INTERNAL_ERROR;
            responseMessage = "Error message: " + e.getMessage();
        }

        genericResponse.setResponseCode(responseCode);
        genericResponse.setResponseMessage(responseMessage);
        return genericResponse;
    }
}
