package com.savbill.integrationsystem.SOAPService.Interface;



import com.savbill.integrationsystem.RestApiService.authenticateUser.LoginPojo;
import com.savbill.integrationsystem.RestApiService.chargeService.ChangeServiceRequest;
import com.savbill.integrationsystem.RestApiService.recordpayment.SearchPaymentPojo;
import com.savbill.integrationsystem.RestApiService.removeAccount.RemoveAccountRequest;
import com.savbill.integrationsystem.SOAPService.AddSubscriberAccountXML.SubscriberAccount;
import com.savbill.integrationsystem.SOAPService.UpdateAccountService.wsUpdateAccount;
import com.savbill.integrationsystem.SOAPService.changeAndApplyServicesToSubAcctNameXML.ChangeServiceSubRequest;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.generated.addservicetoaccount.WsAddServiceToAccount;
import com.savbill.integrationsystem.SOAPService.AddAccountService.wsAddAccount;

import com.savbill.integrationsystem.generated.removeservice.RemoveService;

import com.savbill.integrationsystem.generated.wschangeaddonsubscription.WsChangeAddOnSubscription;
import com.savbill.integrationsystem.generated.wschangetopupsubscription.WsChangeTopUpSubscription;
import com.savbill.integrationsystem.generated.wssubscribeaddon.WsSubscribeAddOn;
import com.savbill.integrationsystem.generated.wssubscribetopup.WsSubscribeTopUp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "SAVBILLAPIGATEWAYBSS-SERVICE")
public interface CmsClient {


    @PostMapping("/api/v1/cms/SoapApi/customers")
    ResponseEntity<?> addAccount(@RequestBody wsAddAccount request,
                                 @RequestParam(name = "serviceArea",required = false) Long serviceAreaId,
                                 @RequestParam(name = "mvnoid", required = false) Long mvnoid, @RequestHeader("Authorization") String token,
                                 @RequestParam(name = "plan",required = false) String plan);

    @PutMapping("/api/v1/cms/SoapApi/removeAccount/changeStatus")
    ResponseEntity<?> removeCustomerStatus(@RequestBody RemoveAccountRequest request, @RequestParam(name = "mvnoId",required = false) Long mvnoId, @RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/cms/portal/subscriber/login")
    ResponseEntity<?> authenticateUser(@RequestBody LoginPojo request,@RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/cms/SoapApi/resetUsegeForAccount")
    GenericDataDTO resetUsageForAccount(@RequestParam String userName, @RequestParam Long mvnoId, @RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/cms/SoapApi/changeService")
    ResponseEntity<?> changeService(@RequestBody ChangeServiceRequest wsChangeServiceRequest, @RequestParam Long mvnoid, @RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/cms/SoapApi/addServiceToAccount")
    ResponseEntity<?> addServiceToAccount(@RequestBody WsAddServiceToAccount request,
                                 @RequestParam(name = "mvnoid", required = false) Long mvnoid, @RequestHeader("Authorization") String token);

    @PutMapping("/api/v1/cms/SoapApi/UpdateAccount")
    ResponseEntity<?> updateAccount(@RequestBody wsUpdateAccount request,
                              @RequestParam(name = "mvnoid", required = false) Long mvnoid, @RequestHeader("Authorization") String token);

    @PostMapping( "/api/v1/cms/payment/approve")
    ResponseEntity<?> approvePayment(@RequestBody SearchPaymentPojo entity, @RequestHeader("Authorization") String token) ;

    @PutMapping("/api/v1/cms/SoapApi/removeSubscriberAccount/changeStatus")
    ResponseEntity<?> removeSubscriberCustomerStatus(@RequestBody String request, @RequestParam(name = "mvnoId",required = false) Long mvnoId, @RequestHeader("Authorization") String token);


    @PostMapping("/api/v1/cms/SoapApi/removeService")
    ResponseEntity<?> removeService(@RequestBody RemoveService request,
                                          @RequestParam(name = "mvnoid", required = false) Long mvnoid, @RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/cms/SoapApi/subscribeAddon")
    GenericDataDTO wsSubscribeAddon(@RequestBody WsSubscribeAddOn request,
                                          @RequestParam(name = "mvnoid", required = false) Long mvnoid, @RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/cms/SoapApi/subscribeTopUp")
    GenericDataDTO wsSubscribeTopUp(@RequestBody WsSubscribeTopUp request,
                                    @RequestParam(name = "mvnoid", required = false) Long mvnoid, @RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/cms/SoapApi/addSubscriberAcctXML")
    ResponseEntity<?> addSubscriberAcctXML(@RequestBody SubscriberAccount request,
                                           @RequestParam(name = "serviceArea",required = false) Long serviceAreaId,
                                           @RequestParam(name = "mvnoid", required = false) Long mvnoid, @RequestHeader("Authorization") String token,
                                           @RequestParam(name = "plan",required = false) String plan);

    @PostMapping("/api/v1/cms/SoapApi/changeAddonSubscription")
    GenericDataDTO changeAddOnSubscription(@RequestBody WsChangeAddOnSubscription request,
                                           @RequestParam(name = "mvnoid", required = false) Long mvnoid, @RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/cms/SoapApi/UpdateSubscriberAccount")
    ResponseEntity<?> updateSubcriberAccount(@RequestBody SubscriberAccount request,
                                    @RequestParam(name = "mvnoid", required = false) Long mvnoid, @RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/cms/SoapApi/changeTopUpSubscription")
    GenericDataDTO changeTopUpSubscription(@RequestBody WsChangeTopUpSubscription request,
                                           @RequestParam(name = "mvnoid", required = false) Long mvnoid, @RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/cms/SoapApi/applyServicesToSubAcct")
    ResponseEntity<?> changeSubService(@RequestBody ChangeServiceSubRequest request, @RequestParam Long mvnoid, @RequestHeader("Authorization") String token);

//
//    @PostMapping("/api/v1/cms/SoapApi/applyServicesToSubAcct")
//    ResponseEntity<?> CheckService(@RequestBody ChangeServiceSubReq request, @RequestParam Long mvnoid, @RequestHeader("Authorization") String token);

}
