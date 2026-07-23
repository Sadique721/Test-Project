package com.savbill.integrationsystem.RestApiService.SubAccountNameIsLoggendIn;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SubAcctNameIsLoggedInRestService {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    RadiusClientService radiusClientService;

    public GenericResponse<Object> handleSubsAcctLogedIn(SubAccNameDto request) throws Exception {
        Map<String, Object> responseData = new HashMap<>();
        GenericResponse<Object> response = new GenericResponse<Object>();
        String responseMessage = SoapConstants.FAILURE;
        int responseCode = SoapConstants.INTERNAL_ERROR;

        try {
            String userName = request.getString1().trim();
            Long mvnoId = SoapConstants.MVNOID;

            log.info("Received request to check if sub-account is logged in for username: {}", userName);
            if (userName == null || StringUtils.isEmpty(userName)) {
                responseCode = SoapConstants.INPUT_MISSING_CODE;
                responseMessage = SoapConstants.INPUT_USERNAME_NULL;
                log.warn("Username is missing or empty. Response Code: {}, Message: {}", responseCode, responseMessage);
                responseData.put(SoapConstants.RESPONSECODE, responseCode);
                responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.setData(responseData);
                return response;
            } else {
                log.info("Checking if sub-account with username '{}' is logged in.", userName);
                GenericDataDTO genericDataDTO = radiusClientService.getSubAcctNameIsLoggedIn(userName, mvnoId);
                if (genericDataDTO.getResponseMessage().equalsIgnoreCase("true")) {
                    responseCode = SoapConstants.SUCCESS_CODE;
                    responseMessage = SoapConstants.SUBACCT_NAME_IS_LOGGEDIN;
                    log.info("Sub-account with username '{}' is logged in. Response Code: {}, Message: {}", userName, responseCode, responseMessage);
                    responseData.put(SoapConstants.RESPONSECODE, responseCode);
                    responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                    responseData.put("result",true);
                    response.setData(responseData);
                    return response;
                } else {
                    responseCode = SoapConstants.SUCCESS_CODE;
                    responseMessage = SoapConstants.SUBACCT_NAME_IS_NOT_LOGGEDIN;
                    log.info("Sub-account with username '{}' is NOT logged in. Response Code: {}, Message: {}", userName, responseCode, responseMessage);
                    responseData.put(SoapConstants.RESPONSECODE, responseCode);
                    responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                    responseData.put("result",false);
                    response.setData(responseData);
                    return response;
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while processing the request to check sub-account login status.", e);
            responseMessage = SoapConstants.FAILURE;
            responseCode = SoapConstants.INTERNAL_ERROR;
        }

        responseData.put(SoapConstants.RESPONSECODE, responseCode);
        responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
        response.setData(responseData);
        return response;
    }
}
