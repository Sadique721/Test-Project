package com.savbill.integrationsystem.SOAPService.Interface;

import com.savbill.integrationsystem.RestApiService.authenticateUser.LoginPojo;
import com.savbill.integrationsystem.RestApiService.chargeService.ChangeServiceRequest;
import com.savbill.integrationsystem.RestApiService.recordpayment.SearchPaymentPojo;
import com.savbill.integrationsystem.RestApiService.removeAccount.RemoveAccountRequest;
import com.savbill.integrationsystem.SOAPService.AddSubscriberAccountXML.SubscriberAccount;
import com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML.ChangeServiceSubRequest;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;

import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccount;
import com.savbill.integrationsystem.SOAPService.AddAccountService.wsAddAccount;
import com.savbill.integrationsystem.SOAPService.UpdateAccountService.wsUpdateAccount;
import com.savbill.integrationsystem.generated.removeservice.RemoveService;
import com.savbill.integrationsystem.generated.wschangeaddonsubscription.WsChangeAddOnSubscription;
import com.savbill.integrationsystem.generated.wschangetopupsubscription.WsChangeTopUpSubscription;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOn;
import com.savbill.integrationsystem.generated.wssubscribetopup.WsSubscribeTopUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

@Service
public class CmsClientService {

    @Autowired
    public CmsClient cmsClient;

    public ResponseEntity<?> AddAccount(wsAddAccount request,Long serviceArea, Long mvnoId, String token, String plan) {
        return cmsClient.addAccount(request,serviceArea, mvnoId, token, plan);
    }

    public ResponseEntity<?> removeCustomerStatus(RemoveAccountRequest accountRequest, Long mvnoid, String token) {
        return cmsClient.removeCustomerStatus(accountRequest, mvnoid, token);
    }

    public Boolean getAuthenticateUser(LoginPojo pojo, String token) {
        ResponseEntity<?> responseEntity = null;
        try {
            responseEntity = cmsClient.authenticateUser(pojo, token);
            if (responseEntity.getBody() instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) responseEntity.getBody();
                String status = map.get("status").toString();
                if (status.equalsIgnoreCase("200")) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean resetUsageForAccount(String userName, Long mvnoid, String token) throws Exception  {
      try{
        GenericDataDTO genericDataDTO = cmsClient.resetUsageForAccount(userName, mvnoid, token);
        if (Objects.nonNull(genericDataDTO)) {
            Integer code = 200;
            Integer responseCode = genericDataDTO.getResponseCode();
            String responseMessage = genericDataDTO.getResponseMessage().trim();
            if (responseCode.equals(code) && responseMessage.equalsIgnoreCase("SUCCESS")) {
                return true;
            }
        }
      }catch (Exception e){
          return false;
      }
        return false;
    }

    public ResponseEntity<?> AddServiceToAccountAccount(WsAddServiceToAccount request, Long mvnoId, String token)throws Exception {
        return cmsClient.addServiceToAccount(request, mvnoId, token);
    }

    public ResponseEntity<?> changeService(ChangeServiceRequest request, Long mvnoid, String token) {
        return cmsClient.changeService(request, mvnoid, token);
    }

    public ResponseEntity<?> UpdateAccount(wsUpdateAccount request, Long mvnoId, String token) throws Exception {
        return cmsClient.updateAccount(request, mvnoId, token);
    }

    public ResponseEntity<?> approvePayment(SearchPaymentPojo entity, String token) {
        return cmsClient.approvePayment(entity, token);
    }

    public ResponseEntity<?> removeSubscriberCustomerStatus(String accountRequest, Long mvnoid, String token) {
        return cmsClient.removeSubscriberCustomerStatus(accountRequest, mvnoid, token);
    }

    public ResponseEntity<?> removeService(RemoveService request, Long mvnoId, String token) {
        return cmsClient.removeService(request, mvnoId, token);
    }

    public GenericDataDTO wsSubscribeAddon(WsSubscribeAddOn request, Long mvnoId, String token) {
        return cmsClient.wsSubscribeAddon(request, mvnoId, token);
    }

    public GenericDataDTO wsSubscribeTopUp(WsSubscribeTopUp request, Long mvnoId, String token) {
        return cmsClient.wsSubscribeTopUp(request, mvnoId, token);
    }

    public ResponseEntity<?> AddSubscriberAcctXML(SubscriberAccount request, Long serviceAreaId,Long mvnoId, String token,String plan) throws Exception {
        return cmsClient.addSubscriberAcctXML(request, serviceAreaId,mvnoId, token,plan);
    }

    public ResponseEntity<?> UpdateSubscriberAccount(SubscriberAccount request, Long mvnoId, String token) throws SQLException {
        return cmsClient.updateSubcriberAccount(request, mvnoId, token);
    }

    public GenericDataDTO changeAddOnSubscription(WsChangeAddOnSubscription request, Long mvnoId, String token) {
        return cmsClient.changeAddOnSubscription(request, mvnoId, token);
    }

    public GenericDataDTO changeTopUpSubscription(WsChangeTopUpSubscription request, Long mvnoId, String token) {
        return cmsClient.changeTopUpSubscription(request, mvnoId, token);
    }

    public ResponseEntity<?> changeSubService(ChangeServiceSubRequest request, Long mvnoid, String token) {
        return cmsClient.changeSubService(request, mvnoid, token);
    }
}
