package com.savbill.integrationsystem.RestApiService.resetUsageForAccount;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.Services.ResetUsageForAccountService;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class ResetUsageForAccountControllor {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CmsClientService cmsClientService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ResetUsageForAccountService resetUsageForAccount;

    @PostMapping("/getWsResetUsageForAccount")
    public GenericResponse<Object> getWsResetUsageForAccount(@RequestBody ResetUsageForAccountRequest request) {
        log.info("Request Received For Reset Usage Account: {} ", request.getUserName());
        Map<String, Object> wsResetUsageForAccountResponse = new HashMap<>();
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String userName = request.getUserName().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        wsResetUsageForAccountResponse.put(SoapConstants.REQUESTID, requestId);
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        if (userName == null || userName.isEmpty()) {
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input UserName is Empty or Null";
            wsResetUsageForAccountResponse.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            wsResetUsageForAccountResponse.put(SoapConstants.RESPONSECODE, responseCode);
            wsResetUsageForAccountResponse.put(SoapConstants.REQUESTID, requestId);
            genericResponse.setData(wsResetUsageForAccountResponse);
            log.warn("Username IS Null Or empty");
            return genericResponse;
        }
        try {
            userName = userName.toLowerCase().trim();
            log.info("Call CmsClient For Reset Usage For Account: {}", userName);
            Boolean resetValidate = cmsClientService.resetUsageForAccount(userName, SoapConstants.MVNOID, token);
            if (resetValidate) {
                responseCode = SoapConstants.SUCCESS_CODE;
                responseMessage = SoapConstants.SUCCESS;
                log.info("Reset Usage For Account: {} Success ResponseCode: {}", userName, responseCode);

            } else {
                responseCode = SoapConstants.USAGE_NOT_RESECT_CODE;
                responseMessage = "Could not Reset Usage for Subscriber Account.";
                log.info("User usage Not Reset Because user: {} is Invalid ResponseCode: {}", userName, responseCode);

            }

            wsResetUsageForAccountResponse.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            wsResetUsageForAccountResponse.put(SoapConstants.RESPONSECODE, responseCode);
            wsResetUsageForAccountResponse.put(SoapConstants.REQUESTID, requestId);
            genericResponse.setData(wsResetUsageForAccountResponse);

            return genericResponse;
        } catch (Exception e) {
            wsResetUsageForAccountResponse.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            wsResetUsageForAccountResponse.put(SoapConstants.RESPONSECODE, responseCode);
            wsResetUsageForAccountResponse.put(SoapConstants.REQUESTID, requestId);
            log.info("Error occurred while resetting usage for account :{} with ErrorMessage: {}", userName, e.getMessage());
            genericResponse.setData(wsResetUsageForAccountResponse);
        }
        log.info("Return Response For user: {}  responseCode: {}", userName, responseCode);
        return genericResponse;
    }
}