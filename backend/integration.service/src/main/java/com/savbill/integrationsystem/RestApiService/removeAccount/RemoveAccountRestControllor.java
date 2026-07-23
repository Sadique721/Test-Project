package com.savbill.integrationsystem.RestApiService.removeAccount;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClient;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClient;
import com.savbill.integrationsystem.SOAPService.Interface.RadiusClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.service.RemoveAccountService;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class RemoveAccountRestControllor {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CmsClient cmsClient;
    @Autowired
    private RemoveAccountService removeAccountService;
    @Autowired
    private CmsClientService cmsClientService;

    @Autowired
    private RadiusClient radiusClient;
    @Autowired
    private RadiusClientService radiusClientService;

    @PostMapping("/removeAccount")
    public GenericResponse<Object> getRemoveAccount(@RequestBody RemoveAccountRequest request) {
        Map<String, Object> response = new HashMap<>();
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        Long mvnoId = SoapConstants.MVNOID;
        String token = jwtUtil.generateJwtToken(mvnoId);
        log.info("Request received to remove account. UserName: {}, MVNO ID: {}", request.getUserName(), mvnoId);
        String userName = request.getUserName();
        if (userName == null || userName.isEmpty()) {
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input UserName is Empty or Null";
            response.put("responseCode", responseCode);
            response.put("responseMessage", responseMessage);
            response.put("requestId", requestId);
            genericResponse.setData(response);
            log.warn("UserName is empty or null. Request ID: {}, Response Code: {}", requestId, responseCode);
            return genericResponse;
        }

        try {
            userName = userName.toLowerCase().trim();
            log.debug("Fetching customer details In RadiusClient for username: {}", userName);
            GenericDataDTO genericDataDTO = radiusClientService.getCustomerDetails(userName, SoapConstants.MVNOID);
            if (genericDataDTO.getData() != null) {
                Map<String, Object> mapData = (Map<String, Object>) genericDataDTO.getData();
                if (userName.equalsIgnoreCase(mapData.get("username").toString())) {
                    log.debug(" In Radius Customer: {} details found Call Cms Client To Perform Remove Operation", userName);
                    ResponseEntity<?> responseEntity = cmsClientService.removeCustomerStatus(request, SoapConstants.MVNOID, token);
                    Map<String, Object> objectMap = (Map<String, Object>) responseEntity.getBody();
                    if (objectMap.get("terminationCheck") != null && objectMap.get("terminationCheck").equals("Success")) {
                        responseCode = SoapConstants.SUCCESS_CODE;
                        responseMessage = "User is deleted from SPR table and Usages Summary Table.";
                        log.info("Account removal successful for username: {}. Response Code: {}", userName, responseCode);
                    }
                }
            } else {
                responseCode = SoapConstants.EMPTY;
                responseMessage = "No data found for the provided username.";
                log.warn("No data found for username: {}. Response Code: {}", userName, responseCode);
            }

            response.put("responseCode", responseCode);
            response.put("responseMessage", responseMessage);
            response.put("requestId", requestId);
            genericResponse.setData(response);
            log.debug("Response prepared for request ID: {}. Response Code: {}, Response Message: {}", requestId, responseCode, responseMessage);
            return genericResponse;
        } catch (Exception e) {
            response.put("responseCode", responseCode);
            response.put("responseMessage", responseMessage);
            response.put("requestId", requestId);
            genericResponse.setData(response);
            log.error("Exception occurred while removing account for username: {}. Request ID: {}, Error: {}", userName, requestId, e.getMessage(), e);
        }

        return genericResponse;
    }
}