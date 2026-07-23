package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.entity.CoaDMProfileAttribute;
import com.savbill.radius.services.CoaDMProfileAttributeService;
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

@Api(value = "COA/DM Profile Attribute Management", description = "REST APIs related to COA/DM Profile Attribute Entity!!!!", tags = "COA/DM Profile Attribute")
@RestController
@RequestMapping("/SavbillRadius")
public class CoaDMProfileAttributeController {

    private static final String COA_PROFILE_ATTRIBUTE_LIST = "CoaDMProfileAttributeAttributeList";
    private static final String COA_PROFILE_ATTRIBUTE = "CoaDMProfileAttributeAttribute";
    private static final Logger log = LoggerFactory.getLogger(CoaDMProfileAttributeController.class);
    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    private CoaDMProfileAttributeService coaDMProfileAttributeService;
    @Autowired
    private Tracer tracer;

    @ApiOperation(value = "Get list of COA/DM Profile attributes in the system")
    @GetMapping("/coaDMProfileAttributes")
//    @PreAuthorize("@roleAccesses.hasPermission('coadm','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllCoaDMProfileAttributes(
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            List<CoaDMProfileAttribute> CoaDMProfileAttributeList = coaDMProfileAttributeService
                    .findAllCoaDMProfileAttributes(mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(COA_PROFILE_ATTRIBUTE_LIST, CoaDMProfileAttributeList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "COA/DM Profile attribute has been fetched successfully :," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching CoaDMProfileAttributes," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get COA/DM Profile attribute based on the given COA/DM Profile id")
    @GetMapping("/findCoaDMProfileAttByCoaDMProfileId")
//    @PreAuthorize("@roleAccesses.hasPermission('coadm','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findCoaDMProfileAttributeById(
            @RequestParam(name = "coaDMProfileId", required = true) Long coaDMProfileId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<CoaDMProfileAttribute> coaDMProfileAttributes = coaDMProfileAttributeService
                    .findCoaDMProfileAttributeByCoaDMProfileId(coaDMProfileId, mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(COA_PROFILE_ATTRIBUTE_LIST, coaDMProfileAttributes);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "COA/DM Profile attribute has been fetched successfully  for coam profile id:," + coaDMProfileId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching CoaDMProfileAttributes by coaDMProfileId:,"+ coaDMProfileId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Update COA/DM Profile attribute based on the COA/DM Profile id")
    @PutMapping("/updateCoaDMProfileAttribute")
//    @PreAuthorize("@roleAccesses.hasPermission('coadm','createUpdateAccess',#request.getHeader('requestFrom'))")
//	@PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_COA_DM_EDIT +"\")")
    public ResponseEntity<Map<String, Object>> updateCoaDMProfileAttribute(
            @RequestBody List<CoaDMProfileAttribute> coaDMProfileAttributeList,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId,
            @RequestParam(name = "coaDMId", required = true) Long coaDMId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<CoaDMProfileAttribute> coaDMProfileAttributeVoList = coaDMProfileAttributeService
                    .updateCoaDMProfileAttribute(coaDMProfileAttributeList, mvnoId, coaDMId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(COA_PROFILE_ATTRIBUTE, coaDMProfileAttributeVoList);
            response.put(RadiusConstants.MESSAGE, "COA/DM Profile attribute has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "COA/DM Profile attribute has been Updated successfully :," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating COA/DM Profile," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Delete COA/DM Profile attribute based on the given COA/DM Profile id")
    @DeleteMapping("/deleteCoaDMProfileAttribute")
//    @PreAuthorize("@roleAccesses.hasPermission('coadm','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> deleteCoaDMProfileAttribute(
            @RequestParam(name = "coaDMProfileAttributeId", required = true) Long coaDMProfileAttributeId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            coaDMProfileAttributeService.deleteCoaDMProfileAttributeById(coaDMProfileAttributeId, mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(RadiusConstants.MESSAGE, "COA/DM Profile attribute has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "COA/DM Profile attribute has been deleted successfully :," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Deleting COA/DM Profile," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }
}
