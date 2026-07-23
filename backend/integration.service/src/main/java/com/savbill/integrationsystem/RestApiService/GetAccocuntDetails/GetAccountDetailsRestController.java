package com.savbill.integrationsystem.RestApiService.GetAccocuntDetails;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.getaccountdetails.WsGetAccountDetails;
import com.savbill.integrationsystem.generated.getaccountdetails.WsGetAccountDetailsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class GetAccountDetailsRestController {


    @Autowired
    public RadiusClientService radiusClientService;
    @Autowired
    private GetAccountDetailsService getAccountDetailsService;

    @PostMapping("/getAccountDetails")
    public GenericDataDTO getAccountDetailsResponse(@RequestBody WsGetAccountDetails request) {
        GenericDataDTO genericResponse = new GenericDataDTO();
        Integer responsecode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        log.info("Request Received In controller for Account Details: {} ", request.getUserName());
        try {
            List<WsGetAccountDetailsResponse> accountDetailsResponseList = getAccountDetailsService.getWsAddAccountDetails(request);
            if (Objects.nonNull(accountDetailsResponseList)) {
                genericResponse.setDataList(accountDetailsResponseList);
                genericResponse.setResponseMessage(SoapConstants.SUCCESS);
                genericResponse.setResponseCode(SoapConstants.SUCCESS_CODE);
                log.info("Request Successfully Completed For Account Details: {} ", request.getUserName());
            } else {
                genericResponse.setDataList(accountDetailsResponseList);
                genericResponse.setResponseMessage(responseMessage);
                genericResponse.setResponseCode(responsecode);
                log.info("Request Failure to retrieve Account: {}  ", request.getUserName());
            }
        } catch (Exception e) {
            genericResponse.setResponseMessage(responseMessage);
            genericResponse.setResponseCode(responsecode);
            log.info("Exception Due to some tactical issues for process Account: {}", request.getUserName());
        }
        return genericResponse;
    }
}


