package com.savbill.integrationsystem.RestApiService.subSessionIsLoggedOn;

import com.savbill.integrationsystem.SOAPService.Interface.RadiusClient;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.subSessionIsLoggedOn.SubsessionIdLoggedOnService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class SubSessionIsLoggedOnControllor {
    @Autowired
    private RadiusClient radiusClient;
    @Autowired
    private RadiusClientService radiusClientService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SubsessionIdLoggedOnService subsessionIdLoggedOnService;

    @PostMapping("/subSessionIsLoggedOn")
    public GenericDataDTO WsSubSessionIsLoggedOn(@RequestBody SubSessionIsLoggedOnDTO request) throws Exception {
        String ipAddress = request.getIpAddress().trim();
        GenericDataDTO genericDataDTOs = new GenericDataDTO();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;

        if (ipAddress == null || ipAddress.isEmpty()) {
            genericDataDTOs.setResponseCode(417);
            genericDataDTOs.setResponseMessage("Input IP Address is Empty or Null");
            genericDataDTOs.setResponseCode(SoapConstants.NOT_FOUND);
            log.warn("IP Address is null or empty");
            return genericDataDTOs;
        }
        if (!isValidIPAddress(ipAddress)) {
            genericDataDTOs.setResponseCode(417);
            genericDataDTOs.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID);
            log.warn("Invalid IP Address format: {}", ipAddress);
            return genericDataDTOs;
        }
        try {
            GenericDataDTO checkLiveuseer = radiusClientService.CheckLiveUser(ipAddress, SoapConstants.MVNOID);
            log.info("Checked live user for IP: {}", ipAddress);

            GenericDataDTO userSession = radiusClientService.GetUserSessionApi(ipAddress, SoapConstants.MVNOID);
            log.info("Fetched user session for IP: {}", ipAddress);

            if (userSession.getData() instanceof Map) {
                Map<String, Object> attributes = (Map<String, Object>) userSession.getData();
                if (attributes.entrySet().stream()
                        .anyMatch(entry -> entry.getKey().equalsIgnoreCase("knownUser") && Boolean.FALSE.equals(entry.getValue()))) {
                    genericDataDTOs.setResponseCode(SoapConstants.SUCCESS_CODE);
                    genericDataDTOs.setData(false);
                    genericDataDTOs.setResponseMessage(SoapConstants.SUCCESS);
                    log.info("User session for IP {} is not known", ipAddress);
                    return genericDataDTOs;
                }

                boolean isUserSessionValid = subsessionIdLoggedOnService.checkUserSession(attributes, ipAddress);
                log.info("User session valid status for IP {}: {}", ipAddress, isUserSessionValid);

                if (isUserSessionValid) {
                    GenericDataDTO radiusCheck = radiusClientService.checkUserSessionInRadiusClient(ipAddress, SoapConstants.MVNOID);
                    log.info("Checked user session in radius client for IP: {}", ipAddress);

                    if (radiusCheck.getData() != null && "true".equalsIgnoreCase(radiusCheck.getData().toString())) {
                        genericDataDTOs.setResponseCode(417);
                        genericDataDTOs.setResponseMessage(radiusCheck.getResponseMessage());
                        log.warn("Session already logged off for IP: {}", ipAddress);
                        return genericDataDTOs;
                    }
                    genericDataDTOs.setResponseCode(SoapConstants.SUCCESS_CODE);
                    genericDataDTOs.setData(isUserSessionValid);
                    genericDataDTOs.setResponseMessage(radiusCheck.getResponseMessage());
                    log.info("Session validated successfully for IP: {}", ipAddress);
                    return genericDataDTOs;
                } else{
                    genericDataDTOs.setResponseCode(SoapConstants.SUCCESS_CODE);
                    genericDataDTOs.setData(isUserSessionValid);
                    genericDataDTOs.setResponseMessage(SoapConstants.SUCCESS);
                    log.info("Session validation completed for IP: {}", ipAddress);
                    return genericDataDTOs;
                }
            } else {
                if (checkLiveuseer != null &&
                        checkLiveuseer.getResponseMessage() != null &&
                        checkLiveuseer.getResponseMessage().equalsIgnoreCase("IP is not available in session table")) {
                    genericDataDTOs.setResponseCode(417);
                    genericDataDTOs.setResponseMessage("IP is not available in session table");
                    log.warn("IP address {} not found in session table", ipAddress);
                    return genericDataDTOs;
                } else {
                    genericDataDTOs.setResponseCode(SoapConstants.SUCCESS_CODE);
                    genericDataDTOs.setData(false);
                    genericDataDTOs.setResponseMessage(SoapConstants.SUCCESS);
                    log.info("Session status for IP {}: no active session", ipAddress);
                    return genericDataDTOs;
                }
            }
        } catch (TimeoutException e) {
            genericDataDTOs.setResponseCode(500);
            genericDataDTOs.setResponseMessage("Timeout occurred while processing the request: " + e.getMessage());
            log.error("Timeout occurred while processing the request for IP: {}", ipAddress, e);
        } catch (RuntimeException e) {
            genericDataDTOs.setResponseCode(500);
            genericDataDTOs.setResponseMessage("An unexpected error occurred: " + e.getMessage());
            log.error("Unexpected error occurred for IP: {}", ipAddress, e);
        }
        return genericDataDTOs;
    }

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }
}
