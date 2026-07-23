package com.savbill.integrationsystem.SOAPService.Interface;


import com.savbill.integrationsystem.RestApiService.logOnSubSession.LogOnSubSessionDTO;
import com.savbill.integrationsystem.SOAPService.wsGetBalance.GetBalanceRadiusDTO;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@FeignClient(name = "SAVBILLRADIUSBSS-SERVICE")
public interface RadiusClient {


    @GetMapping("/SavbillRadius/GetBalanceBySubscriberId/{subscriberId}/{mvnoId}")
    GenericDataDTO getBalanceFunction(@PathVariable("subscriberId") String subscriberId, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/GetAccountDetails/{username}/{mvnoId}")
    GenericDataDTO getAccountDetailsFunction(@PathVariable("username") String username, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/GetLiveUserLoginStatusBySubscriberId/{subscriberId}/{mvnoId}")
    GenericDataDTO getLiveUserLoginStatus(@PathVariable("subscriberId") String subscriberId, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/GetUserSessionsByIp/{ipAddress}/{mvnoId}")
    GenericDataDTO getUserSessions(@PathVariable("ipAddress") String ipAddress, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/GetUserSessionsByIpTimeZ/{ipAddress}/{mvnoId}")
    GenericDataDTO getUserSessionsTimeZ(@PathVariable("ipAddress") String ipAddress, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/getCustomerDetailsByUserName/{username}/{mvnoId}")
    GenericDataDTO getCustomerDetails(@PathVariable String username, @PathVariable Long mvnoId);

    @DeleteMapping("/SavbillRadius/liveUser/disconnect/{cdrId}")
    public ResponseEntity<Map<String, Object>> logOffUserSession(@PathVariable("cdrId") Long cdrId, @RequestParam(name = "mvnoId", required = false) Integer mvnoId, @RequestParam(name = "isDisconnect", required = false) Boolean isDisconnect, @RequestHeader("Authorization") String request);

    @GetMapping("/SavbillRadius/UpdateUerUsage/{username}/{mvnoId}/{usageBytes}")
    GenericDataDTO UpdateUerUsage(
            @PathVariable("username") String username,
            @PathVariable("mvnoId") Long mvnoId,
            @PathVariable("usageBytes") double usageBytes
    );

    @GetMapping("/SavbillRadius/checkKnownUser/{ipAddress}/{mvnoId}")
    GenericDataDTO checkUnKnownUser(
            @PathVariable("ipAddress") String ipAddress,
            @PathVariable("mvnoId") Long mvnoId
    );

    @GetMapping("/SavbillRadius/getAccountName/{ipAddress}/{mvnoId}")
    GenericDataDTO getAccountName(@PathVariable("ipAddress") String ipAddress, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/checkUserSessionInRadiusClient/{ipAddress}/{mvnoId}")
    GenericDataDTO checkUserSessionInRadiusClient(
            @PathVariable("ipAddress") String ipAddress,
            @PathVariable("mvnoId") Long mvnoId
    );

    @GetMapping("/SavbillRadius/LoggOffSubSession/{ipAddress}/{mvnoId}")
    GenericDataDTO LoggOffSubSession(
            @PathVariable("ipAddress") String ipAddress,
            @PathVariable("mvnoId") Long mvnoId
    );

    @PostMapping("/SavbillRadius/Device/customerLogin")
    ResponseEntity<Map<String, Object>> getLocationLockStatus(@RequestBody Map<String, String> payload,
                                                              @RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestHeader("Authorization") String token);

    @PostMapping("/SavbillRadius/Device/customerLogin")
    ResponseEntity<Map<String, Object>> getLocationLockStatus(@RequestBody Map<String, String> payload,
                                                              @RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestHeader("Authorization") String token, @RequestHeader("X-Forwarded-For") String framedIpAddress);

    @GetMapping("/SavbillRadius/LoggOffSubSessions/{userName}/{mvnoId}")
    GenericDataDTO LoggOffSubSessions(
            @PathVariable("userName") String username,
            @PathVariable("mvnoId") Long mvnoId
    );

    @GetMapping("/SavbillRadius/reAuthSession/{userName}/{mvnoId}")
    GenericDataDTO ReAuthSession(
            @PathVariable("userName") String username,
            @PathVariable("mvnoId") Long mvnoId

    );

    @GetMapping("/SavbillRadius/getMeteredVolumeUsageBysubscriberId/{subscriberId}/{mvnoId}")
    GenericDataDTO getMeteredVolumeUsage(@PathVariable("subscriberId") String subscriberId, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/GetCustQoutaDetailsBySubscriberId/{subscriberId}/{mvnoId}")
    GenericDataDTO getCustQoutaDetails(@PathVariable("subscriberId") String subscriberId, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/getSubAcctNameIsLoggedIn/{subscriberId}/{mvnoId}")
    GenericDataDTO getSubAcctNameIsLoggedIn(@PathVariable("subscriberId") String subscriberId, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/getSubscriberAccountByUserName/{username}/{mvnoId}")
    GenericDataDTO getSubscriberAccountDetails(@PathVariable String username, @PathVariable Long mvnoId);

    @GetMapping("/SavbillRadius/getLiveUserSize/{subscriberId}/{mvnoId}")
    ResponseEntity<Integer> getLiveUserSize(@PathVariable("subscriberId") String subscriberId, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/getCOAValidation/{ipAddress}/{mvnoId}")
    GenericDataDTO getCOAValidation(
            @PathVariable("ipAddress") String ipAddress,
            @PathVariable("mvnoId") Long mvnoId
    );

    @GetMapping("/SavbillRadius/SessionLoginStatus/{ipAddress}/{mvnoId}")
    GenericDataDTO SessionLoginStatus(
            @PathVariable("ipAddress") String ipAddress,
            @PathVariable("mvnoId") Long mvnoId
    );

    @PostMapping("/SavbillRadius/GetBalanceBySubscriberIdAndPlanName")
    GenericDataDTO getBalanceFunctionList(@RequestBody GetBalanceRadiusDTO dto);

    @GetMapping("/SavbillRadius/GetUserUsageSummarBySubscriberId/{subscriberId}/{mvnoId}")
    GenericDataDTO GetUserUsageSummery(@PathVariable("subscriberId") String subscriberId, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/CheckLiveUser/{ipAddress}/{mvnoId}")
    GenericDataDTO CheckLiveUser(@PathVariable("ipAddress") String ipAddress, @PathVariable("mvnoId") Long mvnoId);

    @PostMapping("/SavbillRadius/logOnSubSessionRadius/{mvnoId}")
    GenericDataDTO logOnSubSessionRadius(@RequestBody LogOnSubSessionDTO req, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/wsListTopUpSubscriptions/{subscriberId}/{mvnoId}")
    List<Object> GetListTopUpSubscriptions(@PathVariable("subscriberId") String subscriberId, @PathVariable("mvnoId") Long mvnoId);

    @GetMapping("/SavbillRadius/wsListAddOnSubscriptions/{subscriberId}/{mvnoId}")
    List<Object> GetListAddOnSubscriptions(@PathVariable("subscriberId") String subscriberId, @PathVariable("mvnoId") Long mvnoId);


}
