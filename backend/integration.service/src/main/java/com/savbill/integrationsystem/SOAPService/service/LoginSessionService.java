package com.savbill.integrationsystem.SOAPService.service;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.RestApiService.logginSession.LoginSessionRequest;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClient;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.logingSession.LoginSessionEndPoint;
import com.savbill.integrationsystem.billgen.entity.CustomerData;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.generated.wslogingsession.WsLoginSession;
import com.savbill.integrationsystem.generated.wslogingsession.WsLoginSessionResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class LoginSessionService {
    @Autowired
    private CustomerRepository customerDataRepository;
    @Autowired
    private RadiusClient radiusClients;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private LoginSessionService loginSessionService;
    @Autowired
    private RadiusClientService radiusClientService;
    private final Logger logger = LoggerFactory.getLogger(LoginSessionEndPoint.class);
    public CustomerData getCustomerData(String userName) {

        return customerDataRepository.findByUsername(userName);
    }

    public Boolean velidateInputData(LoginSessionRequest request) {
        CustomerData customerData = customerDataRepository.findByUsername(request.getUserName());
        if (Objects.nonNull(customerData) && customerData.getUsername().equalsIgnoreCase(request.getUserName())
                && request.getPassword().equalsIgnoreCase("password")
                && request.getIpAddress().equalsIgnoreCase("120.23.24.56")) {
            return true;
        }
        return false;
    }

    //    public Boolean velidateSoapInputData(WsLoginSessionRequest request) {
//        CustomerData customerData = customerDataRepository.findByUsername(request.getUserName());
//        if(Objects.nonNull(customerData) && customerData.getUsername().equalsIgnoreCase(request.getUserName())
//                && request.getPassword().equalsIgnoreCase("password")
//                && request.getIpAddress().equalsIgnoreCase("120.23.24.56")){
//            return true;
//        }
//        return false;
//    }
    public Boolean checkIpAddress(String ipAddress) {
        try {
            GenericDataDTO genericDataDTO = radiusClients.getUserSessions(ipAddress, SoapConstants.MVNOID);
            if (genericDataDTO.getData() instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) genericDataDTO.getData();
                String loginIpAddress = dataMap.get("framedIpAddress").toString();
                if (ipAddress.equalsIgnoreCase(loginIpAddress)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    public WsLoginSessionResponse loginSession(@RequestPayload LoginSessionRequest request) {
        WsLoginSessionResponse wsLoginSessionResponse = new WsLoginSessionResponse();
        String userName = request.getUserName().trim();
        String password = request.getPassword().trim();
        String ipAddress = request.getIpAddress().trim();
        String responseMessage = SoapConstants.FAILURE;
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        GenericResponse<Object> response = new GenericResponse<>();
        String token = jwtUtil.generateJwtToken(SoapConstants.MVNOID);
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        wsLoginSessionResponse.setRequestId(requestId);
        if (userName == null || userName.isEmpty()) {
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input Username is Empty or Null.";
            wsLoginSessionResponse.setResponeCode(responseCode);
            wsLoginSessionResponse.setResponseMessage(responseMessage);
            wsLoginSessionResponse.setRequestId(requestId);
            return wsLoginSessionResponse;
        }
        try {
            Map<String, String> payload = new HashMap<String, String>();
            payload.put("username", userName);
            payload.put("password", password);
            payload.put("name", "mtik");
            payload.put("sa", "2");
            payload.put("framed-ip-address", ipAddress);

            userName = userName.toLowerCase().trim();
            Boolean checkIpAddress = loginSessionService.checkIpAddress(ipAddress);
            if (!checkIpAddress) {
                responseCode = SoapConstants.NOT_AVAILABLE;
                responseMessage = "No Records Found in session table for give IPAddress.";
                wsLoginSessionResponse.setResponeCode(responseCode);
                wsLoginSessionResponse.setResponseMessage(responseMessage);
                wsLoginSessionResponse.setRequestId(requestId);
                return wsLoginSessionResponse;
            }
            GenericDataDTO genericDataDTO = radiusClients.getCustomerDetails(userName, SoapConstants.MVNOID);
            if (genericDataDTO.getData() instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) genericDataDTO.getData();
                String liveUsername = dataMap.get("username").toString();
                String userStatus = dataMap.get("status").toString();
                String userPassword = dataMap.get("password").toString();
                if ((userStatus != null && Objects.nonNull(userStatus)) && !userStatus.equalsIgnoreCase("Active") && !userStatus.equalsIgnoreCase("suspend")) {
                    responseCode = SoapConstants.STATUS_INACTIVE_CODE;
                    responseMessage = "User in Inactive status in SPR.";
                    wsLoginSessionResponse.setResponeCode(responseCode);
                    wsLoginSessionResponse.setResponseMessage(responseMessage);
                    wsLoginSessionResponse.setRequestId(requestId);
                    return wsLoginSessionResponse;
                } else if ((userPassword != null && !userPassword.isEmpty()) && !userPassword.equalsIgnoreCase(password)) {
                    responseCode = SoapConstants.INPUT_NOT_MATCH_CODE;
                    responseMessage = "Input Password is not match with Username.";
                    wsLoginSessionResponse.setResponeCode(responseCode);
                    wsLoginSessionResponse.setResponseMessage(responseMessage);
                    wsLoginSessionResponse.setRequestId(requestId);
                    return wsLoginSessionResponse;
                } else if (!liveUsername.equalsIgnoreCase(userName)) {
                    responseCode = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE;
                    responseMessage = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE;
                    wsLoginSessionResponse.setResponeCode(responseCode);
                    wsLoginSessionResponse.setResponseMessage(responseMessage);
                    wsLoginSessionResponse.setRequestId(requestId);
                    return wsLoginSessionResponse;
                } else {
                    Map<String, Object> locationLockResponse = radiusClientService.getLocationLockResponse(payload, SoapConstants.MVNOID, token);
                    boolean checkLocationLock = false;
                    if (locationLockResponse.get("data") != null) {
                        checkLocationLock = (boolean) locationLockResponse.get("data");
                    }
                    if (!checkLocationLock) {
                        responseCode = SoapConstants.USER_NOT_ALLOW_CODE;
                        responseMessage = "User is not allow service at This Geo location.";
                        if (locationLockResponse.get("status") != null) {
                            responseCode = (Integer) locationLockResponse.get("status");
                        }
                        if (locationLockResponse.get("message") != null) {
                            responseMessage = (String) locationLockResponse.get("message");
                        }
                        wsLoginSessionResponse.setResponeCode(responseCode);
                        wsLoginSessionResponse.setResponseMessage(responseMessage);
                        wsLoginSessionResponse.setRequestId(requestId);
                        return wsLoginSessionResponse;
                    }
                    responseCode = SoapConstants.SUCCESS_CODE;
                    responseMessage = "COA successfully";
                }
            } else {
                responseCode = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE_CODE;
                responseMessage = SoapConstants.USER_NOT_AVAILABLE_IN_SPR_TABLE;
            }
            wsLoginSessionResponse.setResponeCode(responseCode);
            wsLoginSessionResponse.setResponseMessage(responseMessage);
            wsLoginSessionResponse.setRequestId(requestId);
        } catch (FeignException e) {
            wsLoginSessionResponse.setResponeCode(SoapConstants.INPUT_NOT_MATCH_CODE);
            wsLoginSessionResponse.setResponseMessage(e.getMessage());
            wsLoginSessionResponse.setRequestId(requestId);
            logger.error("error message : " + e.getMessage());
        } catch (Exception e) {
            wsLoginSessionResponse.setResponeCode(responseCode);
            wsLoginSessionResponse.setResponseMessage(responseMessage);
            wsLoginSessionResponse.setRequestId(requestId);
            logger.error("error message : " + e.getMessage());
        }
        return wsLoginSessionResponse;
    }

}
