package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.helper.DeviceDto;
import com.savbill.radius.services.DeviceService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(value = "Device Management", description = "REST APIs related to Device Entity!!!!", tags = "Device Management")
@RestController
@RequestMapping("/SavbillRadius/Device")
public class DeviceController {
    private static final String DEVICE = "device";
    private static final String DEVICE_LIST = "deviceList";
    // private static final String COA_PROFILE_LIST = "coaProfileList";
    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);
    @Autowired
    DeviceService deviceService;
    @Autowired
    APIResponseController apiResponseController;
    @Autowired
    Tracer tracer;

    @ApiOperation(value = "Get list of devices in the system")
    @GetMapping("/all")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusDevice','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DEVICE + "\")")
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(name = "mvnoId", required = true) Integer mvnoId,
                                                       HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DEVICE_LIST, deviceService.findAll(mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Devices has been fetched successfully," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch device  " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/findById")
    @ApiOperation(value = "Get device detail based on the given device id")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusDevice','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DEVICE + "\")")
    public ResponseEntity<Map<String, Object>> findById(@RequestParam("deviceId") Long deviceId,
                                                        @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DEVICE, deviceService.findById(deviceId, mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Devices has been fetched successfully with id," + deviceId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch device With id " + deviceId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/findByName")
    @ApiOperation(value = "Get list of devices based on the given device profile name")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusDevice','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DEVICE + "\")")
    public ResponseEntity<Map<String, Object>> findByName(@RequestParam("deviceProfileName") String deviceProfileName,
                                                          @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DEVICE_LIST, deviceService.findByName(deviceProfileName, mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Devices has been fetched successfully," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Devices has been fetched successfully with device profile name," + deviceProfileName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch device with name  " + deviceProfileName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping("/save")
    @ApiOperation(value = "Add new device")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusDevice','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DEVICE_CREATE + "\")")
    public ResponseEntity<Map<String, Object>> addDevice(@RequestBody DeviceDto deviceDto,
                                                         @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DEVICE, deviceService.add(deviceDto, mvnoId));
            response.put(RadiusConstants.MESSAGE, "Device has been added successfully");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Devices has been created successfully with name," + deviceDto.getDeviceProfileName() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable Create Device with name " + deviceDto.getDeviceProfileName() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PutMapping("/update")
    @ApiOperation(value = "Update eixsting device data based on the device name")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusDevice','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DEVICE_EDIT + "\")")
    public ResponseEntity<Map<String, Object>> updateCustomer(@RequestBody DeviceDto deviceDto,
                                                              @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DEVICE, deviceService.update(deviceDto, mvnoId, request));
            response.put(RadiusConstants.MESSAGE, "Device has been updated successfully");
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable update device with name " + deviceDto.getDeviceProfileName() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            apiResponseController.buildErrorMessageForResponse(response, e);
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "Delete existing device based on the given device profile name")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusDevice','deleteAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DEVICE_DELETE + "\")")
    public ResponseEntity<Map<String, Object>> deleteCustomer(
            @RequestParam(name = "deviceProfileName", required = true) String deviceProfileName,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            deviceService.delete(deviceProfileName, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Device has been deleted successfully");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Devices has been deleted successfully with device profile name," + deviceProfileName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to delete device with name  " + deviceProfileName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            apiResponseController.buildErrorMessageForResponse(response, e);
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/changeStatus")
    @ApiOperation(value = "Change device status based on the given device profile name and status value")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusDevice','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DEVICE_STATUS + "\")")

    public ResponseEntity<Map<String, Object>> changeDeviceStatus(
            @RequestParam(name = "deviceProfileName", required = true) String deviceProfileName,
            @RequestParam(name = "status", required = true) String status,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            String message = deviceService.changeDeviceStatus(deviceProfileName, status, mvnoId, request);
            response.put(RadiusConstants.MESSAGE, message);
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while updating Device status with device name " + deviceProfileName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping("/getDeviceData")
    @ApiOperation(value = "Get device data which match with given payload")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusDevice','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getDeviceData(@RequestBody Map<String, String> payload,
                                                             @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            if (CollectionUtils.isEmpty(payload)) {
                response.put(RadiusConstants.MESSAGE, "Unable to get device data. Reason: No parameter is received in request");
                return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
            }
            String framed_ip_address = request.getHeader("X-Forwarded-For");
            log.info("framed_ip_address from request: " + framed_ip_address);
            payload.put("Framed-ip-address", framed_ip_address);
            Map<String, Object> result = deviceService.getDeviceData(payload, mvnoId, true);
            if (result.get("status").equals(500)) {
                if (result.get("errorMessage") != null)
                    response.put(RadiusConstants.MESSAGE, result.get("errorMessage"));
                else
                    response.put(RadiusConstants.MESSAGE, "No Expression satisfied for Device Profile");
                return apiResponseController.apiResponse(RadiusConstants.EMPTY, response);
            }
            if (result.get("status").equals(404)) {
                response.put(RadiusConstants.MESSAGE, "Unable to process Device Request. Reason: No Device Profile Configured");
                return apiResponseController.apiResponse(RadiusConstants.NULL_VALUE, response);
            }
            if (result.get("status").equals(417)) {
                response.put(RadiusConstants.MESSAGE, result.get("error"));
                return apiResponseController.apiResponse(RadiusConstants.NULL_VALUE, response);
            }
            response.put(RadiusConstants.MESSAGE, result.get("device"));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Devices data has been fetched successfully," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch Device data " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
            printipAddress(request);
        }
    }

    @PostMapping("/customerLogin")
    @ApiOperation(value = "Customer Login")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusDevice','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> customerLogin(@RequestBody Map<String, String> payload,
                                                             @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            if (CollectionUtils.isEmpty(payload)) {
                response.put(RadiusConstants.MESSAGE, "Unable to get device data. Reason: No parameter is received in request");
                return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
            }
            String framed_ip_address = request.getHeader("X-Forwarded-For");
            log.info("framed_ip_address from request: " + framed_ip_address);
            payload.put("Framed-ip-address", framed_ip_address);
            Map<String, Object> result = deviceService.getDeviceDataForLogin(payload, mvnoId, false);
            if (result.get("status").equals(500)) {
                if (result.get("errorMessage") != null)
                    response.put(RadiusConstants.MESSAGE, result.get("errorMessage"));
                else
                    response.put(RadiusConstants.MESSAGE, "No Expression satisfied for Device Profile");
                return apiResponseController.apiResponse(RadiusConstants.EMPTY, response);
            }
            if (result.get("status").equals(404)) {
                response.put(RadiusConstants.MESSAGE, "Unable to process Device Request. Reason: No Device Profile Configured");
                return apiResponseController.apiResponse(RadiusConstants.NULL_VALUE, response);
            }
            if (result.get("status").equals(417)) {
                response.put(RadiusConstants.MESSAGE, result.get("error"));
                return apiResponseController.apiResponse(RadiusConstants.NULL_VALUE, response);
            }
            if (result.get("status").equals(407)) {
                response.put(RadiusConstants.MESSAGE, result.get("error"));
                return apiResponseController.apiResponse(407, response);
            }
            if (result.get("status").equals(412)) {
                response.put(RadiusConstants.MESSAGE, result.get("error"));
                return apiResponseController.apiResponse(412, response);
            }
            response.put(RadiusConstants.MESSAGE, result.get("device"));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Devices data has been fetched successfully," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch Device data " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
            printipAddress(request);
        }
    }

    public void printipAddress(HttpServletRequest request) {
        try {
            final String[] HEADERS_TO_TRY = {
                    "X-Forwarded-For",
                    "Proxy-Client-IP",
                    "WL-Proxy-Client-IP",
                    "HTTP_X_FORWARDED_FOR",
                    "HTTP_X_FORWARDED",
                    "HTTP_X_CLUSTER_CLIENT_IP",
                    "HTTP_CLIENT_IP",
                    "HTTP_FORWARDED_FOR",
                    "HTTP_FORWARDED",
                    "HTTP_VIA",
                    "REMOTE_ADDR"};

            for (String header : HEADERS_TO_TRY) {
                String ip = request.getHeader(header);
                if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                    log.debug("client ip: " + header + " : " + ip);
                }
            }
        } catch (Exception ex) {
            // skip ++
        }

    }
}
