package com.savbill.integrationsystem.RestApiService.GetSubAcctName;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import feign.FeignException;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class GetSubAccNameService {
    @Autowired
    private RadiusClientService radiusClientService;

    @Autowired
    private JwtUtil jwtUtil;

    public GenericResponse<Object> handleSubAccnameRequest(@RequestBody GetSubAccDto request) {
        Map<String, Object> responseData = new HashMap<>();
        GenericResponse<Object> response = new GenericResponse<Object>();
        String responseMessage = SoapConstants.FAILURE;
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String ipAddress = request.getString1().trim();

        log.info("Started handling SubAccName request for IP: {}", ipAddress);
        try {
            if (ipAddress == null || StringUtils.isEmpty(ipAddress)) {
                responseMessage = SoapConstants.INPUT_IP_ADDRESS_NULL;
                responseCode = SoapConstants.INPUT_MISSING_CODE;
                log.warn("IP address is null or empty for request: {} responseCode: {} responseMessage: {}", request,responseCode,responseMessage);
                responseData.put(SoapConstants.RESPONSECODE, responseCode);
                responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.setData(responseData);
                return response;
            }
            if (!isValidIPAddress(ipAddress)) {
                responseMessage = SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID;
                responseCode = SoapConstants.InvalidActivation;
                log.warn("Invalid IP address format for request: {} responseCode: {} responseMessage: {}", ipAddress,responseCode,responseMessage);
                responseData.put(SoapConstants.RESPONSECODE, responseCode);
                responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.setData(responseData);
                return response;
            }
            if (!ipAddress.isEmpty()) {
                log.info("Calling Radius Client for IP address: {}", ipAddress);
                GenericDataDTO genericDataDTO = radiusClientService.GetAccountNameApi(ipAddress, SoapConstants.MVNOID);
                if (genericDataDTO.getData() == null) {
                    responseMessage = "Input IP Address not found in Session Table";
                    responseCode = SoapConstants.NOT_AVAILABLE;
                    log.warn("No data found for IP address In Session Table: {} responseCode: {} responseMessage: {}", ipAddress,responseCode,responseMessage);
                    responseData.put(SoapConstants.RESPONSECODE, responseCode);
                    responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                    response.setData(responseData);
                    return response;
                } else if (genericDataDTO.getData() != null && SoapConstants.UNKNOWN_DATA.equals(genericDataDTO.getData())) {
                    responseMessage = SoapConstants.SUCCESS;
                    responseCode = SoapConstants.SUCCESS_CODE;
                    log.warn("Received unknown data for IP address: {} responseCode: {} responseMessage: {}", ipAddress,responseCode,responseMessage);
                    responseData.put(SoapConstants.RESPONSECODE, responseCode);
                    responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                    responseData.put("accountName",genericDataDTO.getData());
                    response.setData(responseData);
                    return response;
                } else if (genericDataDTO != null) {
                    responseMessage = SoapConstants.SUCCESS;
                    responseCode = SoapConstants.SUCCESS_CODE;
                    log.info("Successfully retrieved for ip: {} responseCode: {} responseMessage: {}", ipAddress, responseCode, responseMessage);
                    responseData.put(SoapConstants.RESPONSECODE, responseCode);
                    responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                    responseData.put("accountName",genericDataDTO.getData());
                    response.setData(responseData);
                    return response;
                }
            }
            responseMessage = "Invalid IP Address";
            responseCode = SoapConstants.UNKNOWN;
            log.warn("Invalid IP address: {}", ipAddress);

        } catch (FeignException e) {
            responseCode = SoapConstants.REMOTE_EXCEPTION_GENERATED_CODE;
            log.error("FeignException occurred while processing IP: {}", ipAddress, e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            responseMessage = "AxisFault Exception due to technical issue";
            responseCode = SoapConstants.REMOTE_EXCEPTION_GENERATED_CODE;
            log.error("Exception occurred while processing IP: {}", ipAddress, e.getMessage());
            e.printStackTrace();
        }
        responseData.put(SoapConstants.RESPONSECODE, responseCode);
        responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
        response.setData(responseData);
        log.info("Returning responseCode: {} responseMessage: {}", responseCode, responseMessage);
        return response;
    }

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }
}
