package com.savbill.radius.SoapApi.Controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.SoapApi.Dto.AddOnSubscriptionListDto;
import com.savbill.radius.SoapApi.Dto.GenericDataDTO;
import com.savbill.radius.SoapApi.Dto.TopUpSubscriptionListDto;
import com.savbill.radius.SoapApi.Services.GetBalanceRadiusDTO;
import com.savbill.radius.SoapApi.Services.GetSoapDataForIntegrationService;
import com.savbill.radius.SoapApi.Services.LogOnSubSessionDTO;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.repository.CustomerRepository;
import com.savbill.radius.repository.LiveUserRepository;
import com.savbill.radius.services.CustomerService;
import com.savbill.radius.services.LiveUserService;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/SavbillRadius")
public class GetSoapDataForIntegrationController {

    @Autowired
    public GetSoapDataForIntegrationService getSoapDataForIntegrationService;

    @Autowired
    Tracer tracer;

    @Autowired
    private LiveUserRepository liveUserRepository;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    LiveUserService liverUserService;

    @GetMapping("/GetBalanceBySubscriberId/{subscriberId}/{mvnoId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.GETBALANCE + "\")")
    public GenericDataDTO GetBalanceBySubscriberId(@PathVariable String subscriberId, @PathVariable("mvnoId") Long mvnoId) {
        long startTime = System.currentTimeMillis();
        log.info("GetBalanceBySubscriberId Controller Level Method Start At:{}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = getSoapDataForIntegrationService.GetBalanceBYSubscriberId(subscriberId);
            log.info("GetBalanceBySubscriberId Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseMessage("Failure");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        genericDataDTO.setResponseMessage("Failure");
        genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        log.info("GetBalanceBySubscriberId Method Completed IN:{}MS With Response Message:{}", System.currentTimeMillis() - startTime, genericDataDTO.getResponseMessage());
        return genericDataDTO;
    }

    @GetMapping("/GetAccountDetails/{username}/{mvnoId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.GETACCOUNTDETAILS + "\")")
    public GenericDataDTO GetAccountDetails(@PathVariable String username, @PathVariable("mvnoId") Long mvnoId) {
        long Started = System.currentTimeMillis();
        log.info("GetAccountDetails Controller Level Method Start At:{}", new Date(Started));

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = getSoapDataForIntegrationService.GetAccountDetails(username);
            log.info("GetAccountDetails Method Completed IN:{}MS", System.currentTimeMillis() - Started);
            return genericDataDTO;
        } catch (Exception e) {
            log.info("GetAccountDetails Method Completed IN:{}MS With Response Message:{}", System.currentTimeMillis() - Started, genericDataDTO.getResponseMessage());
            e.printStackTrace();
        }
        return genericDataDTO;
    }

    @GetMapping("/GetLiveUserLoginStatusBySubscriberId/{subscriberId}/{mvnoId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.GETLOGINSTATUS + "\")")
    public GenericDataDTO GetLiveUserLoginStatus(@PathVariable String subscriberId, @PathVariable Long mvnoId) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.currentTimeMillis();
        log.info("GetLiveUserLoginStatus Controller Level Method Start At:{}", new Date(startTime));
        try {
            genericDataDTO = getSoapDataForIntegrationService.getLiveUserLoginStatus(subscriberId, mvnoId);
            log.info("GetLiveUserLoginStatus Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            log.info("GetLiveUserLoginStatus Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            e.printStackTrace();
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
        log.info("GetLiveUserLoginStatus Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
        return genericDataDTO;
    }

    @GetMapping("/GetUserSessionsByIp/{ipAddress}/{mvnoId}")
//    @PreAuthorize("validatePermission(\"" + MenuConstants.GETUSERSESSION + "\")")
    public GenericDataDTO GetUserSession(@PathVariable String ipAddress, @PathVariable("mvnoId") Long mvnoId) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("GetUserSession Controller Level Method Start At:{}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = getSoapDataForIntegrationService.GetUserSessionDetails(ipAddress, mvnoId);
            log.info("GetUserSession Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("GetUserSession Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            throw e;
        }
    }

    @GetMapping("/GetUserSessionsByIpTimeZ/{ipAddress}/{mvnoId}")
//    @PreAuthorize("validatePermission(\"" + MenuConstants.GETUSERSESSION + "\")")
    public GenericDataDTO GetUserSessionDetailsTimeZoneZ(@PathVariable String ipAddress, @PathVariable("mvnoId") Long mvnoId) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("GetUserSessionDetailsTimeZoneZ Controller Level Method Start At:{}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = getSoapDataForIntegrationService.GetUserSessionDetailsTimeZoneZ(ipAddress, mvnoId);
            log.info("GetUserSessionDetailsTimeZoneZ Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;

        } catch (Exception e) {
            log.info("GetUserSessionDetailsTimeZoneZ Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/getCustomerDetailsByUserName/{subscriberId}/{mvnoId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.GETCUSTOMERDETAILS + "\")")
    public GenericDataDTO getCustomerDetails(@PathVariable String subscriberId, @PathVariable Long mvnoId) {
        Map<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.currentTimeMillis();
        log.info("getCustomerDetails Controller Level Method Start At:{}", new Date(startTime));
        try {
            log.info("getCustomerDetails Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            genericDataDTO = getSoapDataForIntegrationService.getCustomerDetails(subscriberId, Math.toIntExact(mvnoId));
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseMessage("Failure");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        genericDataDTO.setResponseMessage("Failure");
        genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        log.info("getCustomerDetails Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
        return genericDataDTO;
    }

    @GetMapping("/UpdateUerUsage/{username}/{mvnoId}/{usageBytes}")
//    @PreAuthorize("validatePermission(\"" + MenuConstants.UPDATEUSERUSAGE + "\")")
    public GenericDataDTO UpdateUerUsage(@PathVariable("username") String username, @PathVariable("mvnoId") Long mvnoId, @PathVariable("usageBytes") double usageBytes) {
        long startTime = System.currentTimeMillis();
        log.info("UpdateUerUsage Controller Level Method Start At:{}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = getSoapDataForIntegrationService.UpdateUerUsage(username, usageBytes);
            log.info("UpdateUerUsage Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            GenericDataDTO errorResponse = new GenericDataDTO();
            log.info("UpdateUerUsage Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return errorResponse;
        }
    }

    @GetMapping("/LoggOffSubSession/{ipAddress}/{mvnoId}")
    @ApiOperation(value = "Logoff SubSession for a given IP address.")
    public ResponseEntity<GenericDataDTO> logoffSubSession(@PathVariable("ipAddress") String ipAddress, @PathVariable("mvnoId") Long mvnoId) {
        long startTime = System.currentTimeMillis();
        log.info("logoffSubSession Controller Level Method Start At:{}", new Date(startTime));
        try {
            GenericDataDTO response = getSoapDataForIntegrationService.LoggOffSubSession(ipAddress, mvnoId);
            log.info("logoffSubSession Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.info("logoffSubSession Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericDataDTO());
        }
    }

    @GetMapping("/SessionLoginStatus/{ipAddress}/{mvnoId}")
    public GenericDataDTO SessionLoginStatus(@PathVariable("ipAddress") String ipAddress, @PathVariable("mvnoId") Long mvnoId) {
        GenericDataDTO genericData = new GenericDataDTO();
        long startTime = System.currentTimeMillis();
        log.info("SessionLoginStatus Controller Level Method Start At:{}", new Date(startTime));
        try {
            genericData = getSoapDataForIntegrationService.SessionLoginStatus(ipAddress);
            log.info("SessionLoginStatus Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericData;
        } catch (Exception e) {
            log.info("SessionLoginStatus Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericData;
        }
    }

    @GetMapping("/getAccountName/{ipAddress}/{mvnoId}")
    public GenericDataDTO getAccountName(@PathVariable("ipAddress") String ipAddress, @PathVariable("mvnoId") Long mvnoId) {
        GenericDataDTO genericData = new GenericDataDTO();
        long startTime = System.currentTimeMillis();
        log.info("getAccountName Controller Level Method Start At:{}", new Date(startTime));
        try {
            genericData = getSoapDataForIntegrationService.GetAccountName(ipAddress);
            log.info("getAccountName Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericData;
        } catch (Exception e) {
            log.info("getAccountName Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericData;
        }
    }

    @GetMapping("/checkUserSessionInRadiusClient/{ipaddress}/{mvnoId}")
//    @PreAuthorize("validatePermission(\"" + MenuConstants.GETLOGINSTATUS + "\")")
    public GenericDataDTO checkUserSessionInRadiusClient(@PathVariable String ipaddress, @PathVariable Long mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.currentTimeMillis();
        log.info("checkUserSessionInRadiusClient Controller Level Method Start At:{}", new Date(startTime));
        try {
            genericDataDTO = getSoapDataForIntegrationService.checkLiveRadiusClient(ipaddress, Math.toIntExact(mvnoId));
            log.info("checkUserSessionInRadiusClient Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseMessage("Failure");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            log.info("checkUserSessionInRadiusClient Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        }
//        return genericDataDTO;
    }

    @GetMapping("/LoggOffSubSessions/{username}/{mvnoId}")
    @ApiOperation(value = "Logoff SubSessions for a given Username.")
    public GenericDataDTO logoffSubSessions(@PathVariable("username") String username, @PathVariable("mvnoId") Long mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.currentTimeMillis();
        log.info("logoffSubSessions Controller Level Method Start At:{}", new Date(startTime));
        try {
            genericDataDTO = getSoapDataForIntegrationService.LoggOffSubSessions(username, mvnoId);
            log.info("logoffSubSessions Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            log.info("logoffSubSessions Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        }
    }

    @GetMapping("/getMeteredVolumeUsageBysubscriberId/{subscriberId}/{mvnoId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.GETBALANCE + "\")")
    public GenericDataDTO getMeteredVolumeUsage(@PathVariable String subscriberId, @PathVariable("mvnoId") Long mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.currentTimeMillis();
        log.info("getMeteredVolumeUsage Controller Level Method Start At:{}", new Date(startTime));
        try {
            genericDataDTO = getSoapDataForIntegrationService.GetBalanceBYSubscriberId(subscriberId);
            log.info("getMeteredVolumeUsage Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseMessage("Failure");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        genericDataDTO.setResponseMessage("Failure");
        genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        log.info("getMeteredVolumeUsage Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
        return genericDataDTO;
    }

    @GetMapping("/reAuthSession/{username}/{mvnoId}")
    public GenericDataDTO ReAuthSession(@PathVariable("username") String username, @PathVariable("mvnoId") Long mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.currentTimeMillis();
        log.info("ReAuthSession Controller Level Method Start At:{}", new Date(startTime));
        try {
            genericDataDTO = getSoapDataForIntegrationService.GetReAuthSession(username, mvnoId);
            log.info("ReAuthSession Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            log.info("ReAuthSession Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        }
    }


    @GetMapping("/GetCustQoutaDetailsBySubscriberId/{subscriberId}/{mvnoId}")
    public GenericDataDTO GetCustQoutaDetails(@PathVariable String subscriberId, @PathVariable Long mvnoId) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.currentTimeMillis();
        log.info("GetCustQoutaDetails Controller Level Method Start At:{}", new Date(startTime));
        try {
            genericDataDTO = getSoapDataForIntegrationService.getCustQoutaDetails(subscriberId, Math.toIntExact(mvnoId));
            log.info("GetCustQoutaDetails Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
        log.info("GetCustQoutaDetails Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
        return genericDataDTO;
    }


    @GetMapping("/getSubAcctNameIsLoggedIn/{subscriberId}/{mvnoId}")
    public GenericDataDTO getSubAcctNameIsLoggedIn(@PathVariable String subscriberId, @PathVariable("mvnoId") Long mvnoId) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("getSubAcctNameIsLoggedIn Controller Level Method Start At:{}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = getSoapDataForIntegrationService.GetLogedInUsername(subscriberId);
            log.info("getSubAcctNameIsLoggedIn Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("getSubAcctNameIsLoggedIn Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            throw e;
        }
    }

    @GetMapping("/getSubscriberAccountByUserName/{subscriberId}/{mvnoId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.GETSUBSCRIBERACCOUNTDETAILS + "\")")
    public GenericDataDTO getSubscriberAccountDetails(@PathVariable String subscriberId, @PathVariable Long mvnoId) {
        Map<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.currentTimeMillis();
        log.info("getSubscriberAccountDetails Controller Level Method Start At:{}", new Date(startTime));
        try {
            genericDataDTO = getSoapDataForIntegrationService.getSubscriberAccDetails(subscriberId, Math.toIntExact(mvnoId));
            log.info("getSubscriberAccountDetails Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseMessage("Failure");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        genericDataDTO.setResponseMessage("Failure");
        genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        log.info("getSubscriberAccountDetails Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
        return genericDataDTO;
    }

    @GetMapping("/getLiveUserSize/{subscriberId}/{mvnoId}")
    public ResponseEntity<Integer> getLiveUserSize(@PathVariable String subscriberId, @PathVariable Long mvnoId) {
        long startTime = System.currentTimeMillis();
        log.info("getLiveUserSize Controller Level Method Start At:{}", new Date(startTime));
        try {
            Integer size = getSoapDataForIntegrationService.getAllLiveLogingUser(subscriberId);
            log.info("getLiveUserSize Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return ResponseEntity.ok(size);
        } catch (Exception e) {
            log.error("Error fetching live user size", e);
            log.info("getLiveUserSize Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
        }
    }

    @GetMapping("/getCOAValidation/{ipAddress}/{mvnoId}")
    @ApiOperation(value = "Logoff SubSession for a given IP address.")
    public GenericDataDTO getCOAValidation(@PathVariable("ipAddress") String ipAddress, @PathVariable("mvnoId") Long mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.currentTimeMillis();
        log.info("getCOAValidation Controller Level Method Start At:{}", new Date(startTime));
        try {
            GenericDataDTO response = getSoapDataForIntegrationService.getCOAValidation(ipAddress, mvnoId);
            genericDataDTO.setResponseMessage(response.getResponseMessage());
            genericDataDTO.setResponseCode(response.getResponseCode());
            log.info("getCOAValidation Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            log.info("getCOAValidation Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        }
    }

    @GetMapping("/checkKnownUser/{ipAddress}/{mvnoId}")
    public GenericDataDTO checkKnownUser(@PathVariable("ipAddress") String ipAddress, @PathVariable("mvnoId") Long mvnoId) {
        long startTime = System.currentTimeMillis();
        log.info("checkKnownUser Controller Level Method Start At:{}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = getSoapDataForIntegrationService.checkKnownUser(ipAddress, mvnoId);
            log.info("checkKnownUser Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            log.info("checkKnownUser Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        }
    }

    @PostMapping("/GetBalanceBySubscriberIdAndPlanName")
    public GenericDataDTO GetBalanceBySubscriberIdandPlanName(@RequestBody GetBalanceRadiusDTO dto) {
        long startTime = System.currentTimeMillis();
        log.info("GetBalanceBySubscriberIdandPlanName Controller Level Method Start At:{}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            String planeName = "";
            String subscriberId = "";
            String planId = "";
            if (Objects.nonNull(dto.getPlanName()) && !dto.getPlanName().isEmpty() && !dto.getPlanName().equalsIgnoreCase("?")) {
                planeName = dto.getPlanName();
            }
            if (Objects.nonNull(dto.getSubscriberId()) && !dto.getSubscriberId().isEmpty()) {
                subscriberId = dto.getSubscriberId();
            }
            if (dto.getPlanId() != null && Objects.nonNull(dto.getPlanId())) {
                planId = dto.getPlanId();
            }
            genericDataDTO = getSoapDataForIntegrationService.GetBalanceBYSubscriberIdlist(subscriberId, planeName, dto.getMvnoId(), planId);
            log.info("GetBalanceBySubscriberIdandPlanName Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseMessage("Failure");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        genericDataDTO.setResponseMessage("Failure");
        genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        log.info("GetBalanceBySubscriberIdandPlanName Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
        return genericDataDTO;
    }

    @GetMapping("/GetUserUsageSummarBySubscriberId/{subscriberId}/{mvnoId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.GETBALANCE + "\")")
    public GenericDataDTO GetUserUsageSummarBySubscriberId(@PathVariable String subscriberId, @PathVariable("mvnoId") Long mvnoId) {
        long startTime = System.currentTimeMillis();
        log.info("GetUserUsageSummarBySubscriberId Controller Level Method Start At:{}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = getSoapDataForIntegrationService.GetUserSummeryBYSubscriberIdlist(subscriberId, mvnoId);
            log.info("GetUserUsageSummarBySubscriberId Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseMessage("Failure");
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        genericDataDTO.setResponseMessage("Failure");
        genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        log.info("GetUserUsageSummarBySubscriberId Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
        return genericDataDTO;
    }

    @GetMapping("/CheckLiveUser/{ipAddress}/{mvnoId}")
    // @PreAuthorize("validatePermission(" + MenuConstants.GETBALANCE + "\")")
    public GenericDataDTO CheckLiveUser(@PathVariable String ipAddress, @PathVariable("mvnoId") Long mvnoId) {
        long startTime = System.currentTimeMillis();
        log.info("CheckLiveUser Controller Level Method Start At:{}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = getSoapDataForIntegrationService.GetLiveUser(ipAddress, mvnoId);
            log.info("CheckLiveUser Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            log.info("CheckLiveUser Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        }
    }

    @PostMapping("/logOnSubSessionRadius/{mvnoId}")
    public GenericDataDTO checkLivelogOnSubSessionRadiusUser(@RequestBody LogOnSubSessionDTO req, @PathVariable("mvnoId") Long mvnoId) {
        long startTime = System.currentTimeMillis();
        log.info("checkLivelogOnSubSessionRadiusUser Controller Level Method Start At:{}", new Date(startTime));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = getSoapDataForIntegrationService.getLogOnSubSession(req, mvnoId);
            log.info("checkLivelogOnSubSessionRadiusUser Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            log.info("checkLivelogOnSubSessionRadiusUser Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return genericDataDTO;
        }
    }

    @GetMapping("/wsListTopUpSubscriptions/{subscriberId}/{mvnoId}")
    public List<TopUpSubscriptionListDto> GetListTopUpSubscriptions(@PathVariable String subscriberId, @PathVariable Long mvnoId) {
        long startTime = System.currentTimeMillis();
        log.info("GetListTopUpSubscriptions Controller Level Method Start At:{}", new Date(startTime));
        try {
            List<TopUpSubscriptionListDto> list = getSoapDataForIntegrationService.GetListTopUpSubscriptions(subscriberId, Math.toIntExact(mvnoId));
            log.info("GetListTopUpSubscriptions Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
            return list;
        } catch (Exception e) {
            log.error("Exception occurred in GetListTopUpSubscriptions:{} ", e.getMessage());
        }
        log.info("GetListTopUpSubscriptions Method Completed IN:{}MS", System.currentTimeMillis() - startTime);
        return Collections.emptyList();
    }

    @GetMapping("/wsListAddOnSubscriptions/{subscriberId}/{mvnoId}")
    public List<AddOnSubscriptionListDto> GetListAddOnSubscriptions(@PathVariable String subscriberId, @PathVariable Long mvnoId) {
        long startTime = System.currentTimeMillis();
        log.info("GetListAddOnSubscriptions Controller Level Method Start At: {}", new Date(startTime));
        try {
            List<AddOnSubscriptionListDto> list = getSoapDataForIntegrationService.GetListAddOnSubscriptions(subscriberId, Math.toIntExact(mvnoId));
            log.info("GetListAddOnSubscriptions Method Completed IN: {}MS", System.currentTimeMillis() - startTime);
            return list;
        } catch (Exception e) {
            log.error("Exception occurred in GetListAddOnSubscriptions: {}", e.getMessage());
        }
        log.info("GetListAddOnSubscriptions Method Completed IN: {}MS", System.currentTimeMillis() - startTime);
        return Collections.emptyList();
    }

}
