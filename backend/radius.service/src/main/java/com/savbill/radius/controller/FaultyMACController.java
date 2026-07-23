package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.FaultyMAC;
import com.savbill.radius.services.CacheConfigService;
import com.savbill.radius.services.FaultyMACKService;
import com.savbill.radius.utils.CustomValidationException;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.UpdateDiffFinder;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/SavbillRadius/faultyMack")
public class FaultyMACController {
    private static final Logger log = LoggerFactory.getLogger(FaultyMACController.class);
    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    Tracer tracer;
    @Autowired
    FaultyMACKService faultyMACKService;
    @Autowired
    UpdateDiffFinder updateDiffFinder;
    @Autowired
    private CacheConfigService cacheConfigService;

    @ApiOperation(value = "Get list of customers in the system")
    @GetMapping("/list")
//	@PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST + "\")")
    public ResponseEntity<Map<String, Object>> findAllFaultyMack(PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Page<FaultyMAC> mackLists = faultyMACKService.getAll(mvnoId, paginationDTO, request);
            response.put("response", mackLists);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Mack has been fetched successfully:," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            response.put(RadiusConstants.MESSAGE, "Error While fetching mac.");
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Fetching Mack list" + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get list of customers in the system")
    @PostMapping("/save")
//	@PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST + "\")")
    public ResponseEntity<Map<String, Object>> Save(@RequestBody FaultyMAC faultyMAC, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            FaultyMAC mack = faultyMACKService.saveMack(faultyMAC);
            response.put(RadiusConstants.MESSAGE, "mack has been Created successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Mack  has been Created  successfully:," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            //cacheConfigService.reloadCache(AAAConstant.FAULTY_MAC_CACHE);
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (CustomValidationException ce) {
            response.put(RadiusConstants.MESSAGE, "Error While Creating mac.");
            apiResponseController.buildErrorMessageForResponse(response, ce);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Createing Mack " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(HttpStatus.NOT_ACCEPTABLE.value(), response);
        } catch (Exception e) {
            response.put(RadiusConstants.MESSAGE, "Error While Creating mac.");
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Createing Mack " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PutMapping("/updateMack")
    @ApiOperation(value = "Update eixsting customer based on the user name")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateMack(
            @RequestBody FaultyMAC faultyMAC,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            FaultyMAC oldresult = faultyMACKService.findById(faultyMAC.getId());
            FaultyMAC result = faultyMACKService.updateMack(faultyMAC);
            response.put("FaultyMAC", result);
            response.put(RadiusConstants.MESSAGE, "mack has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "mack has been Updated Successfully  with updated:," + updateDiffFinder.getUpdatedDiff(result, oldresult) + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            //cacheConfigService.reloadCache(AAAConstant.FAULTY_MAC_CACHE);
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (CustomValidationException ce) {
            response.put(RadiusConstants.MESSAGE, "Error While Updating mac.");
            apiResponseController.buildErrorMessageForResponse(response, ce);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating mack " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(HttpStatus.NOT_ACCEPTABLE.value(), response);
        } catch (Exception e) {
            response.put(RadiusConstants.MESSAGE, "Error While Updating mac.");
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating mack " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/findById")
    @ApiOperation(value = "Update eixsting customer based on the user name")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findByMacId(
            @RequestParam(name = "macId", required = true) Long macId,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            FaultyMAC mac = faultyMACKService.findById(macId);
            response.put("response", mac);
            response.put(RadiusConstants.MESSAGE, "mack has been fetched successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "mack has been Fetched successfully:," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            response.put(RadiusConstants.MESSAGE, "Error While fetching mac.");
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching mack " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @DeleteMapping("/deleteMac")
    @ApiOperation(value = "Delete existing customer based on the given user name")
//	 @PreAuthorize("@roleAccesses.hasPermission('radiusCustomer','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> deleteMac(
            @RequestParam(name = "mackId", required = true) String mackId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            faultyMACKService.deleteMack(mackId, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Mac has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Mack has been Deleted Successfully  with name:," + mackId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            //cacheConfigService.reloadCache(AAAConstant.FAULTY_MAC_CACHE);
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (CustomValidationException ce) {
            response.put(RadiusConstants.MESSAGE, "Error While deleting mac");
            apiResponseController.buildErrorMessageForResponse(response, ce);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Deleting mack with id " + mackId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(HttpStatus.NOT_ACCEPTABLE.value(), response);
        } catch (Exception e) {
            response.put(RadiusConstants.MESSAGE, "Error While deleting mac");
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Deleting mack with id " + mackId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping(value = "/uploadXL", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createMacData(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "mvnoId", required = false) String mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            faultyMACKService.uploadXl(file, Integer.parseInt(mvnoId));
            response.put(RadiusConstants.MESSAGE, "File has been uploaded successfully.");
            //cacheConfigService.reloadCache(AAAConstant.FAULTY_MAC_CACHE);
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            response.put(RadiusConstants.MESSAGE, "Error While uploading file");
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Uploading file " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

}
