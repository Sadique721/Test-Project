package com.savbill.integrationsystem.SOAPService.Interface;

import com.savbill.integrationsystem.RestApiService.logOnSubSession.LogOnSubSessionDTO;
import com.savbill.integrationsystem.SOAPService.wsGetBalance.GetBalanceRadiusDTO;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RadiusClientService {

    @Autowired
    RadiusClient radiusClient;

    public GenericDataDTO GetBalanceApi(String SubscriberId, Long mvnoId) {
        return radiusClient.getBalanceFunction(SubscriberId, mvnoId);
    }

    public GenericDataDTO GetAccountDetailsApi(String username, Long mvnoId) {
        return radiusClient.getAccountDetailsFunction(username, mvnoId);
    }


    public GenericDataDTO getLiveUserLoginStatus(String subscriberId, Long mvnoId) {
        return radiusClient.getLiveUserLoginStatus(subscriberId, mvnoId);
    }

    public GenericDataDTO getCustomerDetails(String username, Long mvnoId) throws Exception {
        return radiusClient.getCustomerDetails(username, mvnoId);
    }

    public GenericDataDTO GetUserSessionApi(String ipAddress, Long mvnoId) throws Exception {
        return radiusClient.getUserSessions(ipAddress, mvnoId);
    }

    public GenericDataDTO getUserSessionsTimeZ(String ipAddress, Long mvnoId) throws Exception {
        return radiusClient.getUserSessionsTimeZ(ipAddress, mvnoId);
    }


    public GenericDataDTO GetAccountNameApi(String ipAddress, Long mvnoId) {
        return radiusClient.getAccountName(ipAddress, mvnoId);
    }

    public ResponseEntity<Map<String, Object>> logOffUserSession(Long cdrId, Long mvnoId, String request) {
        return radiusClient.logOffUserSession(cdrId, Math.toIntExact(mvnoId), false, request);
    }

    public GenericDataDTO UpdateUerUsage(String username, Long mvnoId, double usageBytes) {
        return radiusClient.UpdateUerUsage(username, mvnoId, usageBytes);
    }

    public GenericDataDTO SessionLoginStatus(String ipAddress, Long mvnoId) {
        return radiusClient.SessionLoginStatus(ipAddress, mvnoId);
    }

    public GenericDataDTO checkUserSessionInRadiusClient(String ipAddress, Long mvnoId) throws Exception {
        return radiusClient.checkUserSessionInRadiusClient(ipAddress, mvnoId);
    }

    public GenericDataDTO LoggOffSubSession(String ipAddress, Long mvnoId) {
        return radiusClient.LoggOffSubSession(ipAddress, mvnoId);
    }

    public GenericDataDTO LoggOffSubSessions(String username, Long mvnoId) {
        return radiusClient.LoggOffSubSessions(username, mvnoId);
    }


    public Map<String, Object> getLocationLockResponse(Map<String, String> payload, Long mvnoId, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (payload.get("framed-ip-address") != null) {
                ResponseEntity<Map<String, Object>> genericDataDTO = radiusClient.getLocationLockStatus(payload, Math.toIntExact(mvnoId), token, payload.get("framed-ip-address"));
                Map<String, Object> objectMap = genericDataDTO.getBody();
                String status = objectMap.get("status").toString();
                if (status != null && status.equalsIgnoreCase("200")) {
                    response.put("status", 200);
                    response.put("message", "COA successfully");
                    response.put("data", true);
                    return response;
                }
            } else {
                ResponseEntity<Map<String, Object>> genericDataDTO = radiusClient.getLocationLockStatus(payload, Math.toIntExact(mvnoId), token);
                Map<String, Object> objectMap = genericDataDTO.getBody();
                String status = objectMap.get("status").toString();
                if (status != null && status.equalsIgnoreCase("200")) {
                    response.put("status", 200);
                    response.put("message", "COA successfully");
                    response.put("data", true);
                    return response;
                }
            }
        } catch (FeignException e) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, Object> result = mapper.readValue(e.contentUTF8(), Map.class);
                String message = (String) result.get("message");
                int status = (Integer) result.get("status");
                if (message.contains("location lock")) {
                    response.put("status", 412);
                    response.put("message", "User is not allow service at This Geo location.");
                    response.put("data", false);
                } else if (message.contains("Duplicate Login attempt by userName")) {
                    response.put("status", 407);
                    response.put("message", "No Records Found in session table for give IPAddress.");
                    response.put("data", false);
                } else {
                    response.put("status", status);
                    response.put("message", message);
                    response.put("data", false);
                }
                System.out.printf("Status: " + status + " message: " + message);
            } catch (JsonProcessingException ex) {
                response.put("status", 400);
                response.put("message", "Invalid Data");
                response.put("data", false);
            }
            return response;
        } catch (Exception e) {
            response.put("status", 400);
            response.put("message", "Invalid Data");
            response.put("data", false);
        }
        return response;
    }

    public GenericDataDTO getMeteredVolumeUsage(String SubscriberId, Long mvnoId) throws Exception {
        return radiusClient.getMeteredVolumeUsage(SubscriberId, mvnoId);
    }

    public GenericDataDTO ReAuthSession(String username, Long mvnoId) {
        return radiusClient.ReAuthSession(username, mvnoId);
    }

    public GenericDataDTO getCustQoutaDetails(String subscriberId, Long mvnoId) {
        return radiusClient.getCustQoutaDetails(subscriberId, mvnoId);
    }

    public GenericDataDTO getSubAcctNameIsLoggedIn(String subscriberId, Long mvnoId) {
        return radiusClient.getSubAcctNameIsLoggedIn(subscriberId, mvnoId);
    }

    public GenericDataDTO getSubscriberAccountDetails(String username, Long mvnoId) throws Exception {
        return radiusClient.getSubscriberAccountDetails(username, mvnoId);
    }

    public GenericDataDTO getCOAValidation(String ipAddress, Long mvnoId) throws Exception {
        return radiusClient.getCOAValidation(ipAddress, mvnoId);
    }

    public GenericDataDTO checkUnKnownUser(String ipAddress, Long mvnoId) {
        return radiusClient.checkUnKnownUser(ipAddress, mvnoId);
    }

    public GenericDataDTO GetBalanceApiList(GetBalanceRadiusDTO dto) {
        return radiusClient.getBalanceFunctionList(dto);
    }

    public GenericDataDTO GetUserUsageSummery(String SubscriberId, Long mvnoId) {
        return radiusClient.GetUserUsageSummery(SubscriberId, mvnoId);
    }

    public GenericDataDTO CheckLiveUser(String ipAddress, Long mvnoId) {
        return radiusClient.CheckLiveUser(ipAddress, mvnoId);
    }

    public GenericDataDTO logOnSubSessionRadius(LogOnSubSessionDTO req, Long mvnoId) throws Exception {
        return radiusClient.logOnSubSessionRadius(req, mvnoId);
    }

    public List<Object> GetListTopUpSubscriptions(String SubscriberId, Long mvnoId) throws Exception {
        return radiusClient.GetListTopUpSubscriptions(SubscriberId, mvnoId);
    }

    public List<Object> GetListAddOnSubscriptions(String SubscriberId, Long mvnoId) throws Exception {
        return radiusClient.GetListAddOnSubscriptions(SubscriberId, mvnoId);
    }
}
