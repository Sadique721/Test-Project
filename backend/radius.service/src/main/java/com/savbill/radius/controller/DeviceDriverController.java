package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.aaa.data.CustomerData;
import com.savbill.radius.entity.DeviceDriver;
import com.savbill.radius.helper.DeviceDriverDTO;
import com.savbill.radius.helper.DeviceDriverSearchDTO;
import com.savbill.radius.services.DeviceDriverService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.UpdateDiffFinder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Device Driver Management", description = "REST APIs related to Device  Driver Entity!!!!", tags = "Device Driver Management")
@RestController
@RequestMapping("/SavbillRadius/DeviceDriver")
public class DeviceDriverController {

    private static final String DEVICE = "deviceDriver";
    private static final String DEVICE_LIST = "deviceList";
    // private static final String COA_PROFILE_LIST = "coaProfileList";

    @Autowired
    DeviceDriverService deviceDriverService;
    @Autowired
    APIResponseController apiResponseController;
    @Autowired
    private Tracer tracer;
    @Autowired
    private UpdateDiffFinder updateDiffFinder;
    private static final Logger log = LoggerFactory.getLogger(DeviceDriverController.class);
    @ApiOperation(value = "Get list of devices driver in the system")
    @GetMapping("/all")
    @PreAuthorize("validatePermission(\"" + MenuConstants.DRIVER_MANAGEMENT + "\")")
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(name = "mvnoId", required = true) Integer mvnoId,
                                                       HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DEVICE_LIST, deviceDriverService.findAll(mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Devices has been fetched successfully,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Fetching Device Drivers" + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/findById")
    @ApiOperation(value = "Get device detail based on the given device id")
    @PreAuthorize("validatePermission(\"" + MenuConstants.DRIVER_MANAGEMENT + "\")")
    public ResponseEntity<Map<String, Object>> findById(@RequestParam("deviceId") Long deviceId,
                                                        @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DEVICE, deviceDriverService.findById(deviceId, mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Devices has been fetched successfully with id,"+deviceId   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Fetching Device Drivers  with id "+deviceId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }


    @PostMapping("/save")
    @ApiOperation(value = "Add new device")
    @PreAuthorize("validatePermission(\"" + MenuConstants.DRIVER_MANAGEMENT_CREATE + "\")")
    public ResponseEntity<Map<String, Object>> addDevice(@RequestBody DeviceDriverDTO deviceDto,
                                                         @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Boolean flag = deviceDriverService.validateByName(deviceDto.getName(), mvnoId);
            if (flag) {
                throw new RuntimeException("Config with this name already exist");
            }
            response.put(DEVICE, deviceDriverService.add(deviceDto, mvnoId));
            response.put(RadiusConstants.MESSAGE, "Device Driver has been added successfully");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Devices has been created successfully with name," +deviceDto.getName()  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while creating Device Drivers with name  " +deviceDto.getName() + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PutMapping("/update")
    @ApiOperation(value = "Update eixsting device data based on the device name")
    @PreAuthorize("validatePermission(\"" + MenuConstants.DRIVER_MANAGEMENT_EDIT + "\")")
    public ResponseEntity<Map<String, Object>> updateCustomer(@RequestBody DeviceDriverDTO deviceDto,
                                                              @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            DeviceDriver oldValues = deviceDriverService.findById(deviceDto.getDeviceDriverId(),mvnoId);
            String diff=updateDiffFinder.getUpdatedDiff(oldValues, new DeviceDriver(deviceDto));
            response.put(DEVICE, deviceDriverService.update(deviceDto, mvnoId));
            response.put(RadiusConstants.MESSAGE, "Device Driver has been updated successfully");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Devices has been Updated successfully, with updated" +diff  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while updating Device Drivers  " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "Delete existing device based on the given device profile name")
    @PreAuthorize("validatePermission(\"" + MenuConstants.DRIVER_MANAGEMENT_DELETE + "\")")
    public ResponseEntity<Map<String, Object>> deleteCustomer(
            @RequestParam(name = "deviceDriverId", required = true) Long deviceDriverId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            deviceDriverService.delete(deviceDriverId, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Device Driver has been deleted successfully");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Devices has been fetched successfully with id,"+deviceDriverId   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting Device Drivers with id "+deviceDriverId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get list of devices driver in the system by name")
    @PostMapping("/searchByName")
    @PreAuthorize("validatePermission(\"" + MenuConstants.DRIVER_MANAGEMENT + "\")")
    public ResponseEntity<Map<String, Object>> searchDeviceDriverByName(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestBody DeviceDriverSearchDTO deviceDriverSearchDTO,
                                                                        HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<DeviceDriver> deviceDriverList = deviceDriverService.getDeviceDriverByName(deviceDriverSearchDTO.getName(), mvnoId);
            response.put(DEVICE_LIST, deviceDriverList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Device Driver has been fetched Successfully with name,"+deviceDriverSearchDTO.getName()   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Fetching Device Drivers  " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "This api is use for verify user connect with AD")
    @PostMapping("/ldapAuthRequest")
    @PreAuthorize("validatePermission(\"" + MenuConstants.DRIVER_MANAGEMENT_CREATE + "\",\"" + MenuConstants.DRIVER_MANAGEMENT_EDIT + "\")")
    public ResponseEntity<Map<String, Object>> authRequestForLdap(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestBody DeviceDriverDTO deviceDriverDTO,
                                                                  HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Boolean ldapAuthResponse = deviceDriverService.authUser(deviceDriverDTO.getUserName(), deviceDriverDTO.getPassword(), deviceDriverDTO.getAddress());
            CustomerData isUserExist = deviceDriverService.isUserExist("test", "shailendra", "raol", 2);
            if (ldapAuthResponse) {
                response.put("msg", "Connection successful");
                response.put("isUserExist", isUserExist);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "LDAP auth request is successfull"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
                return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
            } else {
                response.put("msg", "Connection Failed");
               // log.debug("Devices driver has been fetched successfully by " + MDC.get(RadiusConstants.USER_NAME));
                return apiResponseController.apiResponse(RadiusConstants.EMPTY, response);
            }
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Fetching Device Drivers  " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }
}
