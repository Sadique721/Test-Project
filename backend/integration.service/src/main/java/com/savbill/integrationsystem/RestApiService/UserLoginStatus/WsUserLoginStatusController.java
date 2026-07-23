package com.savbill.integrationsystem.RestApiService.UserLoginStatus;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wsuserloginstatus.WsUserLoginStatus;
import com.savbill.integrationsystem.generated.wsuserloginstatus.WsUserLoginStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class WsUserLoginStatusController {


    @Autowired
    private RadiusClientService radiusClientService;

    @PostMapping("/userLoginStatus")
    public GenericResponse<Object> handleUserLoginStatus(@RequestBody WsUserLoginStatus request) {
        log.info("Request Received In handleUserLoginStatus : {}", request.getUserName());
        Map<String, Object> responseData = new HashMap<>();
        GenericResponse<Object> response = new GenericResponse<>();
        WsUserLoginStatusResponse.UserLoginStatus status = new WsUserLoginStatusResponse.UserLoginStatus();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        status.setRequestId(requestId);
        Integer responsecode;
        String responseMessage;
        Boolean result = false;
        String userName = request.getUserName().toLowerCase().trim();
        if (userName == null || userName.isEmpty()) {
            responsecode = SoapConstants.EMPTY;
            responseMessage = "Input Username is Empty or null.";
            status.setResponeCode(responsecode);
            status.setResponseMessage(responseMessage);
            status.setRequestId(requestId);
            status.setResult(result);
            response.setData(status);
            log.info("UserName Is Empty or null with ResponseCode: {}", responsecode);
            return response;
        }
        try {
            log.info("Radius Client Calling to check Status for user: {}", userName);
            GenericDataDTO genericDataDTO = radiusClientService.getLiveUserLoginStatus(userName, SoapConstants.MVNOID);
            Object data = genericDataDTO.getData();
            if (Objects.nonNull(data)) {
                if (data instanceof Map) {
                    Map<String, Object> dataMap = (Map<String, Object>) data;
                    String liveUsername = dataMap.get("userName").toString();
                    if (liveUsername != null && !liveUsername.isEmpty() && liveUsername.equalsIgnoreCase(userName)) {
                        status.setRequestId(requestId);
                        status.setResponeCode(SoapConstants.SUCCESS_CODE);
                        status.setResponseMessage("Session Is LoggedIN");
                        status.setResult(true);
                        log.info("Session Is LoggedIN for user: {} with ResponseCode: {}", userName, status.getResponeCode());
                        response.setData(status);
                        return response;
                    }
                } else if (Objects.nonNull(genericDataDTO) && genericDataDTO.getResponseCode() == SoapConstants.UNKNOWN_PARAM) {
                    status.setRequestId(requestId);
                    status.setResponeCode(SoapConstants.UNKNOWN_PARAM);
                    status.setResponseMessage("IP is available in session table with PARAM_STR9 is preauth : Ericsson/Huawei/Nokia");
                    status.setResult(result);
                    log.info("Session Is Not-LoggedIN User Is Unknown :{}  with ResponseCode:{}", userName, status.getResponeCode());
                    response.setData(status);
                    return response;
                }
            }
            status.setRequestId(requestId);
            status.setResponeCode(SoapConstants.UNKNOWN_PARAM);
            status.setResponseMessage("IP is available in session table with PARAM_STR9 is preauth : Ericsson/Huawei/Nokia");
            status.setResult(result);
            log.info("User Not LoggedIn or userNot Found In System:{} ", userName);
            response.setData(status);
            return response;
        } catch (Exception e) {
            status.setRequestId(requestId);
            status.setResponeCode(HttpStatus.EXPECTATION_FAILED.value());
            status.setResponseMessage(SoapConstants.FAILURE);
            status.setResult(result);
            response.setData(status);
            log.error("Error occurring While Processing Request with error message: {}", e);
        }
        return response;
    }
}
