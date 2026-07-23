package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.entity.MacAddressMapping;
import com.savbill.radius.entity.MacAddressMappingDto;
import com.savbill.radius.services.MacAddressMappingService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Api(value = "Mac Address Mapping Management", description = "REST APIs related to Mac Address Mapping Entity!!!!", tags = "Mac Address Mapping")
@RestController
@RequestMapping("/SavbillRadius")
public class MacAddressMappingController {
    private static final String MAC_ADDRESS_MAPPING_LIST = "macAddressMappingList";
    //private static final String MAC_ADDRESS_MAPPING = "macAddressMapping";
    private static final Logger log = LoggerFactory.getLogger(MacAddressMappingController.class);
    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    private MacAddressMappingService macAddressMappingService;
    @Autowired
    private Tracer tracer;

    @ApiOperation(value = "Get list of Macc Address in the system")
    @GetMapping("/macAddressMappings")
    public ResponseEntity<Map<String, Object>> findAllMacAddressMappings(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<MacAddressMapping> macAddressMappingList = macAddressMappingService.findAllMacAddressMapping();
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(MAC_ADDRESS_MAPPING_LIST, macAddressMappingList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Mack address mapping has been fetched successfully,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching MacAddress mapping,: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get Mac Address Parameter based on the given Customer id")
    @GetMapping("/findMacAddressMappingByCustomerId")
    public ResponseEntity<Map<String, Object>> findMacAddressMappingByCustomerId(@RequestParam(name = "customerId", required = true) Long customerId,HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<MacAddressMapping> macAddressMappings = macAddressMappingService.findMacAddressMappingByCustomerId(customerId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(MAC_ADDRESS_MAPPING_LIST, macAddressMappings);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Mack address mapping has been fetched successfully,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching MacAddress for customer,: "+customerId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Save Mac Address based on the Customer id")
    @PutMapping("/saveMacAddressMapping")
    public ResponseEntity<Map<String, Object>> saveMacAddressMapping(@RequestBody MacAddressMappingDto smsConfigMappingList,HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            MacAddressMapping macAddressMappingVoList = macAddressMappingService.saveMacAddressMapping(smsConfigMappingList);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(MAC_ADDRESS_MAPPING_LIST, macAddressMappingVoList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Mack address mapping has been created successfully,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating  MacAddress mapping,: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Update Mac Address based on the Customer id")
    @PutMapping("/updateMacAddressMapping")
    public ResponseEntity<Map<String, Object>> updateMacAddressMapping(@RequestBody List<MacAddressMapping> smsConfigMappingList,HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<MacAddressMapping> macAddressMappingVoList = macAddressMappingService.updateMacAddressMapping(smsConfigMappingList);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(MAC_ADDRESS_MAPPING_LIST, macAddressMappingVoList);
            response.put(RadiusConstants.MESSAGE, "Mac Address has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Mack address mapping has been Updated successfully,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while updating  MacAddress mapping,: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Delete Mac Address based on the given Mac Address id")
    @DeleteMapping("/deleteMacAddressMapping")
    public ResponseEntity<Map<String, Object>> deleteMacAddressMapping(@RequestParam(name = "macAddressId", required = true) Long macAddressId,HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            macAddressMappingService.deleteMacAddressMappingById(macAddressId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(RadiusConstants.MESSAGE, "Mac Address has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Mack address mapping has been deleted successfully with id,"+macAddressId+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating  MacAddress with id,: " +macAddressId+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get Mac Address Parameter based on the given Customer username")
    @GetMapping("/findMacAddressMappingByUserName")
    public ResponseEntity<Map<String, Object>> findMacAddressMappingByUserName(@RequestParam(name = "username", required = true) String userName, @RequestParam(name = "mvnoId", required = true) int mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<HashMap<String, Object>> macAddressMappings = macAddressMappingService.findMacAddressMappingByUserName(userName, mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(MAC_ADDRESS_MAPPING_LIST, macAddressMappings);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Mack address mapping has been fetched successfully of username,"+userName+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting MacAddress,: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Delete mac address mappings base on given mapping id(s)")
    @DeleteMapping("/deleteMacAddressByids")
    public ResponseEntity<Map<String, Object>> deleteMacAddressMappingByUserName(@RequestParam(name = "ids", required = true) Set<Long> mac, @RequestParam(name = "mvnoId", required = true) int mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            String result = macAddressMappingService.deleteMacAddressByUserNameAndMac(mac);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put("message", result);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Mack  mapping has been fetched successfully of username,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, "Error while deleting MacAddress, Please try again after some time.");
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting MacAddress,: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

}
