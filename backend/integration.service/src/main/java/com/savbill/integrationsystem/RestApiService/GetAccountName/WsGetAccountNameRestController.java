package com.savbill.integrationsystem.RestApiService.GetAccountName;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wsgetaccountname.WsGetAccountNameResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class WsGetAccountNameRestController {

    @Autowired
    private RadiusClientService radiusClientService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/getAccountName")
    public GenericResponse<Object> getWsGetAccountNameResponse(@RequestBody WsGetAccountNameDTO request) {
        GenericResponse<Object> responseData = new GenericResponse<>();
        WsGetAccountNameResponse response = new WsGetAccountNameResponse();
        String ipAddress = request.getIpAddress().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        response.setRequestId(requestId);

        try {
            log.info("Received request for getAccountName for ipAddress: {}", ipAddress);
            response = getAccountName(request, requestId, ipAddress);

            if (response != null) {
                responseData.setData(response);
                log.info("Successfully processed request for ipAddress: {}", ipAddress);
            } else {
                response.setResponeCode(SoapConstants.USER_DOSE_NOT_EXIST_CODE);
                response.setResponseMessage(SoapConstants.USER_NOT_AVAILABLE);
                responseData.setData(response);
                log.warn("User not found for for ipAddress: {}", ipAddress);
            }
        } catch (NullPointerException e) {
            response.setResponeCode(SoapConstants.INTERNAL_ERROR);
            response.setResponseMessage("Server Error: A null value caused an exception.");
            log.error("NullPointerException occurred for for ipAddress: {},message: {}", ipAddress, e.getMessage());
        } catch (Exception e) {
            response.setResponeCode(SoapConstants.INTERNAL_ERROR);
            response.setResponseMessage("Server Error: An unexpected error occurred.");
            log.error("Exception occurred for for ipAddress: {},message: {}", ipAddress, e.getMessage());
        }

        responseData.setData(response);
        return responseData;
    }

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }

    public WsGetAccountNameResponse getAccountName(WsGetAccountNameDTO request, String requestId, String ipAddress) {
        WsGetAccountNameResponse response = new WsGetAccountNameResponse();
        try {
            if (ipAddress == null || ipAddress.isEmpty()) {
                response.setRequestId(requestId);
                response.setResponeCode(SoapConstants.EMPTY);
                response.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_NULL_DOT);
                log.warn("Empty IP address provided for requestId: {}", requestId);
                return response;
            }
            if (!isValidIPAddress(ipAddress)) {
                response.setRequestId(requestId);
                response.setResponeCode(SoapConstants.InvalidActivation);
                response.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID);
                log.warn("Invalid IP address provided for requestId: {}", requestId);
                return response;
            }
            log.warn("Radius Client Call to find AccountName through Ip: {}", ipAddress);
            GenericDataDTO genericDataDTO = radiusClientService.GetAccountNameApi(ipAddress, SoapConstants.MVNOID);
            String data = genericDataDTO.getData() != null ? genericDataDTO.getData().toString() : null;

            if (data == null || data.isEmpty()) {
                response.setRequestId(requestId);
                response.setResponeCode(SoapConstants.InvalidActivation);
                response.setResponseMessage("Input Ip Address is not valid.");
                log.warn("No data found for IP address: {} and ResponseCode: {}", ipAddress, response.getResponeCode());
                return response;
            }
            if (data.equalsIgnoreCase(SoapConstants.UNKNOWN_DATA)) {
                response.setAccountName(SoapConstants.UNKNOWN_DATA);
                response.setRequestId(requestId);
                response.setResponeCode(SoapConstants.UNKNOWN);
                response.setResponseMessage(SoapConstants.UNKNOWN_USERNAME_FOUND_AGAINST_INPUT_IP_ADDRESS_FOR_LOGIN_SESSION_WITHOUT_DOT);
                log.warn("Unknown data found for IP address: {} and ResponseCode: {}", ipAddress, response.getResponeCode());
            } else {
                response.setAccountName(data);
                response.setRequestId(requestId);
                response.setResponeCode(SoapConstants.SUCCESS_CODE);
                response.setResponseMessage(SoapConstants.SUCCESS);
                log.info("Successfully retrieved account name for IP address: {} and ResponseCode: {}", ipAddress, response.getResponeCode());
            }
        } catch (FeignException.FeignClientException e) {
            response.setResponeCode(SoapConstants.INTERNAL_ERROR);
            response.setResponseMessage(e.getMessage());
            e.printStackTrace();
            log.error("FeignClientException occurred for for ipAddress: {},message: {}", ipAddress, e.getMessage());
        } catch (NullPointerException e) {
            response.setResponeCode(SoapConstants.INTERNAL_ERROR);
            response.setResponseMessage(e.getMessage());
            e.printStackTrace();
            log.error("NullPointerException occurred for for ipAddress: {},message: {}", ipAddress, e.getMessage());
        } catch (Exception e) {
            response.setResponeCode(SoapConstants.INTERNAL_ERROR);
            response.setResponseMessage("Server Error: An unexpected error occurred.");
            e.printStackTrace();
            log.error("Exception occurred for for ipAddress: {},message: {}", ipAddress, e);
        }

        return response;
    }
}