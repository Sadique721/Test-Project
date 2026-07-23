package com.savbill.integrationsystem.NewNMSIntegration.controller;

import brave.Tracer;
import brave.propagation.TraceContext;

import com.savbill.integrationsystem.NewNMSIntegration.dto.IntegrationSpecificParamDTO;
import com.savbill.integrationsystem.NewNMSIntegration.dto.WifiConfigRequestDTO;
import com.savbill.integrationsystem.NewNMSIntegration.dto.WifiConfigGetDetailDTO;
import com.savbill.integrationsystem.NewNMSIntegration.entity.NmsIntegration;
import com.savbill.integrationsystem.NewNMSIntegration.message.NMSIntegrationMessage;
import com.savbill.integrationsystem.NewNMSIntegration.service.APIIntegrationService;
import com.savbill.integrationsystem.NewNMSIntegration.service.NmsIntegrationService;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import io.swagger.annotations.ApiOperation;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.NMS_INTEGRATION)
public class NMSIntegrationController {

    @Autowired
    private NmsIntegrationService nmsIntegrationService;

    @Autowired
    private Tracer tracer;

    @Autowired
    private APIIntegrationService apiIntegrationService;


    private static final Logger LOGGER = LoggerFactory.getLogger(NMSIntegrationController.class);


    @ApiOperation(value = "This API will fetch NmsIntegration by ID")
    @GetMapping("/getByCustomerId")
    public ResponseEntity<?> getNmsIntegrationById(
            @RequestParam(name = "customerid", required = true) Long customerId,
            HttpServletRequest req) {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = 0;
        MDC.put("type", "Fetch");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        try {
            LOGGER.info("Request from: {}, Request for: Fetch NmsIntegration by ID: {}, Request By: {}, Status: START",
                    req.getHeader("requestFrom"), customerId, "DummyUser");

            List<NmsIntegration> nmsIntegrations = nmsIntegrationService.getByCustomerId(customerId);
            response.put("data", nmsIntegrations);
            RESP_CODE = 200; // Success code
            // Log success
            LOGGER.info("Request from: {}, Request for: Fetch NmsIntegration by ID: {}, Status: SUCCESS",
                    req.getHeader("requestFrom"), customerId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            RESP_CODE = 0;
            LOGGER.error("Request from: {}, Request for: Fetch NmsIntegration by ID: {}, Status: FAILED, Error: {}",
                    req.getHeader("requestFrom"), customerId, ex.getMessage());
            response.put("errorMessage", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        } catch (Exception ex) {
            RESP_CODE = 0;
            LOGGER.error("Request from: {}, Request for: Fetch NmsIntegration by ID: {}, Status: FAILED, Error: {}",
                    req.getHeader("requestFrom"), customerId, ex.getMessage());
            response.put("errorMessage", "An unexpected error occurred.");
            return ResponseEntity.status(500).body(response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @PostMapping("/NMSProvisioning")
    private String NMSProvisioning(@RequestBody NMSIntegrationMessage nmsIntegrationMessage) {
        String apiStatus = "";
        MDC.put("type", "CREATE");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        try {
            return apiStatus = apiIntegrationService.addONUIntegration(nmsIntegrationMessage);
            //logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Testing Audit: "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception e) {
            e.getMessage();
            return apiStatus = "Something went wrong !!" + e.getMessage();
            // logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Testing Audit:  "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

    }

    @PostMapping("/NMSUpdateWANConfig")
    private String NMSUpdateWANConfig(@RequestBody NMSIntegrationMessage nmsIntegrationMessage) {
        String apiStatus = "";
        MDC.put("type", "CREATE");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        try {
            return apiStatus = apiIntegrationService.updateWANConfig(nmsIntegrationMessage);
            //logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Testing Audit: "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception e) {
            e.getMessage();
            return apiStatus = "Something went wrong !!" + e.getMessage();
            // logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Testing Audit:  "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

    }

    @PostMapping("/NMSWifiConfig")
    private ResponseEntity<?> NMSWifiConfig(@RequestBody WifiConfigGetDetailDTO wifiConfigGetDetailDTO, HttpServletRequest req) {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = 0;
        String apiStatus = "FAILED"; // Default to failed
        MDC.put("type", "CREATE");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        try {
            NMSIntegrationMessage nmsIntegrationMessage = apiIntegrationService.getNMSIntegrationMessage(wifiConfigGetDetailDTO);
            // **Handle case where `nmsIntegrationMessage` is null**
            if (nmsIntegrationMessage == null) {
                RESP_CODE = 400; // Bad Request
                apiStatus = "NMS Integration Message not found.";
                LOGGER.warn("Request from: {}, Request for: NMS Wifi Config failed with Serial number: {}, Status: FAILED, Error: {}",
                        req.getHeader("requestFrom"), wifiConfigGetDetailDTO.getSerialNumber(), apiStatus);
                response.put("responseCode", RESP_CODE);
                response.put("responseMessage", apiStatus);
                return ResponseEntity.status(400).body(response);
            }
            // **Proceed when `nmsIntegrationMessage` is valid**
            apiStatus = apiIntegrationService.wifiConfig(nmsIntegrationMessage, wifiConfigGetDetailDTO.getSsidUsername(), wifiConfigGetDetailDTO.getSsidPassword(), wifiConfigGetDetailDTO.getWorkingFrequency());
            RESP_CODE = 200; // Success code
            response.put("responseCode", RESP_CODE);
            response.put("responseMessage", apiStatus);
            LOGGER.info("Request from: {}, Request for: NMS Wifi Config Success with Serial number: {}, Status: SUCCESS",
                    req.getHeader("requestFrom"), wifiConfigGetDetailDTO.getSerialNumber());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            RESP_CODE = 404;
            apiStatus = ex.getMessage();
            LOGGER.error("Request from: {}, Request for: NMS Wifi Config failed with Serial number: {}, Status: FAILED, Error: {}",
                    req.getHeader("requestFrom"), wifiConfigGetDetailDTO.getSerialNumber(), ex.getMessage());
            response.put("responseCode", RESP_CODE);
            response.put("responseMessage", apiStatus);
            return ResponseEntity.status(404).body(response);
        } catch (Exception ex) {
            RESP_CODE = 500;
            apiStatus = "An unexpected error occurred.";
            LOGGER.error("Request from: {}, Request for: NMS Wifi Config failed with Serial number: {}, Status: FAILED, Error: {}",
                    req.getHeader("requestFrom"), wifiConfigGetDetailDTO.getSerialNumber(), ex.getMessage());
            response.put("responseCode", RESP_CODE);
            response.put("responseMessage", apiStatus);
            return ResponseEntity.status(500).body(response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {

        }
        return loggedInUser;
    }

    @ApiOperation(value = "This API acknowledges and processes NMS Integration")
    @GetMapping("/acknowledge")
    public String acknowledgeIntegration(@RequestParam(name = "id", required = true) Long id, HttpServletRequest req) {
        String apiStatus = "";
        HashMap<String, Object> response = new HashMap<>();
        List<IntegrationSpecificParamDTO> integrationParams = new ArrayList<>();
        Integer RESP_CODE = 0;
        MDC.put("type", "Acknowledge");
        MDC.put("userName", "DummyUser");
        MDC.put("traceId", req.getHeader("traceId"));
        try {
            LOGGER.info("Request from: {}, Request for: Acknowledge Integration by ID: {}, Request By: {}, Status: START",
                    req.getHeader("requestFrom"), id, "DummyUser");
            return apiStatus = nmsIntegrationService.acknowledgeInIntegration(id);
        } catch (ResourceNotFoundException e) {
            e.getMessage();
            return apiStatus = "Something went wrong !!" + e.getMessage();
        } finally {
            org.apache.log4j.MDC.remove("type");
            org.apache.log4j.MDC.remove("userName");
            org.apache.log4j.MDC.remove("traceId");
            org.apache.log4j.MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "This API will fetch NmsIntegration by ID")
    @PostMapping("/getWifiConfig")
    public ResponseEntity<?> getWifiConfigDetails(@RequestBody WifiConfigRequestDTO wifiConfigRequestDTO, HttpServletRequest req) {
        Map<String, Object> response = new HashMap<>();
        int RESP_CODE = 0; // It seems this isn’t used, so you might consider removing it.
        MDC.put("type", "Fetch");
        MDC.put("userName", "DummyUser");
        MDC.put("traceId", req.getHeader("traceId"));
        String requestFrom = req.getHeader("requestFrom");
        Long customerId = wifiConfigRequestDTO.getCustomerId();  // Assuming customerId comes from DTO
        try {
            LOGGER.info("Request from: {}, Request for: Fetch Wifi Config by ID: {}, Status: START", requestFrom, customerId);
            WifiConfigGetDetailDTO wifiConfigGetDetailDTO = nmsIntegrationService.getDetailDTO(wifiConfigRequestDTO);
            RESP_CODE = 200;
            response.put("data", wifiConfigGetDetailDTO);
            response.put("responseCode", RESP_CODE);
            LOGGER.info("Request from: {}, Request for: Fetch Wifi Config by ID: {}, Status: SUCCESS", requestFrom, customerId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            RESP_CODE = 404;
            LOGGER.error("Request from: {}, Request for: Fetch Wifi Config by ID: {}, Status: FAILED, Error: {}", requestFrom, customerId, ex.getMessage());
            response.put("responseCode", RESP_CODE);
            response.put("errorMessage", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception ex) {
            RESP_CODE = 500;
            LOGGER.error("Request from: {}, Request for: Fetch Wifi Config by ID: {}, Status: FAILED, Error: {}", requestFrom, customerId, ex.getMessage());
            response.put("responseCode", RESP_CODE);
            response.put("errorMessage", "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
        }
    }
}

