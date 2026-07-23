package com.savbill.integrationsystem.RestApiService.WsUpdateAccount;

import com.savbill.integrationsystem.RestApiService.GenericResponse.GenericResponse;
import com.savbill.integrationsystem.SOAPService.Interface.CmsClientService;
import com.savbill.integrationsystem.SOAPService.SoapConstant.SoapConstants;
import com.savbill.integrationsystem.SOAPService.UpdateAccountService.wsUpdateAccount;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.generated.wsupdateaccount.WsUpdateAccountResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.REST_API)
public class WsUpdateAccountController {

    @Autowired
    private CmsClientService cmsClientService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/updateAccount")
    public GenericResponse<Object> updateAccount(@RequestBody wsUpdateAccount request) {
        Map<String, Object> responseData = new HashMap<>();
        GenericResponse<Object> response = new GenericResponse<>();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String userName = request.getUserName().toLowerCase().trim();
        WsUpdateAccountResponse wsUpdateAccountResponse = new WsUpdateAccountResponse();
        WsUpdateAccountResponse.UpdateAccount updateAccount = new WsUpdateAccountResponse.UpdateAccount();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        updateAccount.setRequestId(requestId);
        try {
            log.info("Processing update account request for user: {}", userName);
            wsUpdateAccountResponse = handleUpdateAccount(request);
            if (Objects.nonNull(wsUpdateAccountResponse)) {
                responseCode = wsUpdateAccountResponse.getUpdateAccount().getResponeCode();
                responseMessage = wsUpdateAccountResponse.getUpdateAccount().getResponseMessage();
                responseData.put(SoapConstants.REQUESTID, requestId);
                responseData.put(SoapConstants.RESPONSECODE, responseCode);
                responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.setData(responseData);
                log.info("Update account request processed successfully for user: {}", userName);
                return response;
            } else {
                responseCode = wsUpdateAccountResponse.getUpdateAccount().getResponeCode();
                responseMessage = wsUpdateAccountResponse.getUpdateAccount().getResponseMessage();
                responseData.put(SoapConstants.REQUESTID, requestId);
                responseData.put(SoapConstants.RESPONSECODE, responseCode);
                responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
                response.setData(responseData);
                log.warn("Update account request returned null response for user: {}", userName);
                return response;
            }
        } catch (Exception e) {
            log.error("Error processing update account request for user: {}", userName, e.getMessage());
            responseCode = SoapConstants.NOT_FOUND;
            responseMessage = "Error processing update account request Or User Not Found";
            responseData.put(SoapConstants.REQUESTID, requestId);
            responseData.put(SoapConstants.RESPONSECODE, responseCode);
            responseData.put(SoapConstants.RESPONSEMESSAGE, responseMessage);
            response.setData(responseData);
            return response;
        }
    }

    private boolean isValidIPAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }

    public WsUpdateAccountResponse handleUpdateAccount(wsUpdateAccount request) throws Exception {
        WsUpdateAccountResponse response = new WsUpdateAccountResponse();
        WsUpdateAccountResponse.UpdateAccount updateAccount = new WsUpdateAccountResponse.UpdateAccount();
        Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = SoapConstants.FAILURE;
        String userName = request.getUserName().trim();
        String requestId = (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) ? "?" : request.getRequestId().trim();
        updateAccount.setRequestId(requestId);

        if (userName == null || userName.isEmpty()) {
            log.warn("Input UserName is Empty or Null for requestId: {}", requestId);
            responseCode = SoapConstants.EMPTY;
            responseMessage = "Input UserName is Empty or Null";
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(responseCode);
            updateAccount.setResponseMessage(responseMessage);
            response.setUpdateAccount(updateAccount);
            return response;
        }

        List<wsUpdateAccount.Item> itms = request.getItem();
        if (itms != null && !itms.isEmpty()) {
            for (wsUpdateAccount.Item item : itms) {
                if ((SoapConstants.PARAM1.equalsIgnoreCase(item.getKey()) && !isValidIPAddress(item.getValue())) || (SoapConstants.PARAM2.equalsIgnoreCase(item.getKey()) && !isValidIPAddress(item.getValue()))) {
                    log.warn("Invalid IP address format for key: {} for user : {}", item.getKey(), userName);
                    updateAccount.setRequestId(requestId);
                    updateAccount.setResponeCode(SoapConstants.BAD_REQUEST);
                    updateAccount.setResponseMessage(SoapConstants.INPUT_IP_ADDRESS_FORMATE_INVALID);
                    response.setUpdateAccount(updateAccount);
                    return response;
                }
//                if (!SoapConstants.CUSTOMERSTATUS.equals(item.getKey()) && !SoapConstants.PARAM1.equals(item.getKey()) && !SoapConstants.PARAM2.equals(item.getKey())) {
//                    updateAccount.setResponeCode(SoapConstants.BAD_REQUEST);
//                    updateAccount.setResponseMessage(SoapConstants.BAD_REQUEST_MESSAGE);
//                    response.setUpdateAccount(updateAccount);
//                    return response;
//                }
                if (SoapConstants.CUSTOMERSTATUS.equalsIgnoreCase(item.getKey())) {
                    if (SoapConstants.ACTIVE.equalsIgnoreCase(item.getValue()) || SoapConstants.INACTIVE.equalsIgnoreCase(item.getValue()) || SoapConstants.SUSPEND.equalsIgnoreCase(item.getValue())) {
                        updateAccount.setResponseMessage(SoapConstants.SUCCESS);
                        updateAccount.setResponeCode(SoapConstants.SUCCESS_CODE);
                    } else if (SoapConstants.INVALID_ACTIVATION.equalsIgnoreCase(item.getValue())) {
                        log.warn("Invalid activation status 'H' for user: {} in requestId: {}", userName, requestId);
                        updateAccount.setRequestId(requestId);
                        updateAccount.setResponseMessage(SoapConstants.INVALID_ACTIVATION_WITH_STATUS_H);
                        updateAccount.setResponeCode(SoapConstants.InvalidActivation);
                        response.setUpdateAccount(updateAccount);
                        return response;
                    } else {
                        log.warn("Invalid activation status: {} for user: {} in requestId: {}", item.getValue(), userName, requestId);
                        updateAccount.setRequestId(requestId);
                        updateAccount.setResponeCode(SoapConstants.InvalidActivation);
                        updateAccount.setResponseMessage(SoapConstants.INVALID_ACTIVATION_WITH_STATUS + " " + item.getValue());
                        response.setUpdateAccount(updateAccount);
                        return response;
                    }
                }
            }
        } else {
            log.warn("No items provided in the request for user: {} in requestId: {}", userName, requestId);
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(SoapConstants.INTERNAL_ERROR);
            updateAccount.setResponseMessage("No items provided in the request.");
            response.setUpdateAccount(updateAccount);
            return response;
        }

        try {
            Long mvnoId = SoapConstants.MVNOID;
            String token = jwtUtil.generateJwtToken(mvnoId);
            log.info("Calling CMS client service for user: {} with requestId: {}", userName, requestId);
            ResponseEntity<?> responseEntity = cmsClientService.UpdateAccount(request, mvnoId, token);
            if (responseEntity != null && responseEntity.getBody() instanceof Map) {
                Map<String, Object> responseMap = (Map<String, Object>) responseEntity.getBody();
                if (responseMap.get(SoapConstants.STATUS).equals(SoapConstants.SUCCESS_CODE)) {
                    if (responseMap.containsKey("message") && responseMap.containsValue("invalid location lock")) {
                        log.warn("Invalid location lock for user: {} in requestId: {}", userName, requestId);
                        updateAccount.setResponseMessage(SoapConstants.INVALID_LOCATION_LOCK);
                        updateAccount.setResponeCode(SoapConstants.InvalidActivation);
                    } else {
                        updateAccount.setResponseMessage(SoapConstants.SUCCESS);
                        updateAccount.setResponeCode(SoapConstants.SUCCESS_CODE);
                    }
                } else {
                    log.warn("User Name Incorrect or Service returned an invalid response for user: {} in requestId: {}", userName, requestId);
                    updateAccount.setResponeCode(SoapConstants.INTERNAL_ERROR);
                    updateAccount.setResponseMessage("User Name Incorrect or Service returned an invalid response");
                }
            } else {
                log.warn("User Name Incorrect or Service returned an invalid response for user: {} in requestId: {}", userName, requestId);
                updateAccount.setResponeCode(SoapConstants.INTERNAL_ERROR);
                updateAccount.setResponseMessage("User Name Incorrect or Service returned an invalid response");
            }
        } catch (FeignException e) {
            log.debug("FeignException occurred for user: {} in requestId: {} message: {}", userName, requestId, e.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            String message = "An error occurred while processing the request";
            int status = 404;
            try {
                String errorMessage = e.contentUTF8();
                JsonNode jsonNode = objectMapper.readTree(errorMessage);
                message = jsonNode.get("ERROR").asText();
                status = jsonNode.get("status").asInt();
                if (Objects.nonNull(message) && message.equalsIgnoreCase("Package not Available")) {
                    log.warn("Service ID is not available in System for user: {} in requestId: {}", userName, requestId);
                    updateAccount.setRequestId(requestId);
                    updateAccount.setResponeCode(SoapConstants.NOT_FOUND);
                    updateAccount.setResponseMessage("Service ID is not available in System");
                    response.setUpdateAccount(updateAccount);
                    return response;
                } else if (Objects.nonNull(message) && message.equalsIgnoreCase("Status field is mandatory, Please add Status")) {
                    log.warn("Status field is mandatory for user: {} in requestId: {}", userName, requestId);
                    updateAccount.setRequestId(requestId);
                    updateAccount.setResponeCode(SoapConstants.NOT_FOUND);
                    updateAccount.setResponseMessage("Status field is mandatory, Please add Status");
                    response.setUpdateAccount(updateAccount);
                    return response;
                }
            } catch (JsonProcessingException je) {
                log.error("Error processing JSON response for user: {} in requestId: {}", userName, requestId, je.getMessage());
                throw new RuntimeException("Error processing JSON response", je);
            }
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(status);
            updateAccount.setResponseMessage(message);
            response.setUpdateAccount(updateAccount);
            return response;
        } catch (Exception e) {
            log.error("Error processing update account request for user: {} in requestId: {}", userName, requestId, e.getMessage());
            updateAccount.setRequestId(requestId);
            updateAccount.setResponeCode(SoapConstants.NOT_FOUND);
            updateAccount.setResponseMessage("Error processing update account request Or User Not Found");
            response.setUpdateAccount(updateAccount);
            return response;
        }

        response.setUpdateAccount(updateAccount);
        return response;
    }
}