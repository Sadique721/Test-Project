package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.entity.DictionaryValue;
import com.savbill.radius.helper.DictionaryValueDto;
import com.savbill.radius.helper.UpdateDictionaryValueDto;
import com.savbill.radius.services.DictionaryValueService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
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

@Api(value = "Dictionary Value Management", description = "REST APIs related to Dictionary value Entity!!!!", tags = "Dictionary Value")
@RestController
@RequestMapping("/SavbillRadius/dictionary/value")
public class DictionaryValueController {

    private static final String DICTIONARY_VALUE = "dictionaryValue";
    private static final String DICTIONARY_VALUE_LIST = "dictionaryValueList";

    @Autowired
    private DictionaryValueService dictionaryValueService;
    @Autowired
    private APIResponseController apiResponseController;
	@Autowired
	private Tracer tracer;
    private static final Logger log = LoggerFactory.getLogger(DictionaryValueController.class);
    @ApiOperation(value = "Get list of dictionary values in the system")
    @GetMapping("/findAll")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    ResponseEntity<Map<String, Object>> findAllDictionaryValues(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY_VALUE_LIST, dictionaryValueService.findAllDictionaryValues(mvnoId));
            log.debug("All  Dictionary values has been fetched successfully by " + MDC.get(RadiusConstants.USER_NAME));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Dictionary values has been fetched successfully ,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Throwable e) {
			apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching dictionary " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get dictionary values based on the given dictionary value id")
    @GetMapping("/findById")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_VALUE + "\")")
    ResponseEntity<Map<String, Object>> findDictionaryValueById(
            @RequestParam(name = "dictionaryValueId", required = true) Long dictionaryValueId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            response.put(DICTIONARY_VALUE, dictionaryValueService.findDictionaryValueById(dictionaryValueId, mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Dictionary values has been fetched successfully of dictionary id ,"+dictionaryValueId+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching dictionary value by dictionary attribute Id: " +dictionaryValueId+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get list of dictionary values based on the given name")
    @GetMapping("/findByName")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    ResponseEntity<Map<String, Object>> findDictionaryValueByName(
            @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            response.put(DICTIONARY_VALUE_LIST, dictionaryValueService.findByName(name, mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Dictionary values has been fetched successfully  of name,"+name+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while creating dictionary value by dictionary name: " +name+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Add new dictionary value")
    @PostMapping("/save")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_VALUE_CREATE + "\")")
    ResponseEntity<Map<String, Object>> saveDictionaryValue(
            @RequestBody DictionaryValueDto dictionaryValue,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY_VALUE, dictionaryValueService.saveDictionaryValue(dictionaryValue, mvnoId));
            response.put(RadiusConstants.MESSAGE, "Dictionary value has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Dictionary has been created successfully with name," +dictionaryValue.getName()+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while creating dictionary " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Update dictionary value based on the given dictionary value id")
    @PutMapping("/update")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_VALUE_EDIT + "\")")
    ResponseEntity<Map<String, Object>> updateDictionaryValue(
            @RequestBody UpdateDictionaryValueDto dictionaryValue,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY_VALUE, dictionaryValueService.updateDictionaryValue(dictionaryValue, mvnoId));
            response.put(RadiusConstants.MESSAGE, "Dictionary value has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Dictionary values has been updated successfully ,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating dictionary value by name " +dictionaryValue.getName()+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Delete dictionary value based on the given dictionary value id")
    @DeleteMapping("/delete")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','deleteAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_VALUE_DELETE + "\")")
    ResponseEntity<Map<String, Object>> deleteDictionaryValue(
            @RequestParam(name = "dictionaryValueId", required = true) Long dictionaryValueId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            dictionaryValueService.deleteDictionaryValue(dictionaryValueId, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Dictionary value has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Dictionary values has been Deleted  successfully with id ,"+dictionaryValueId+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting dictionary value by dictionary Id: " +dictionaryValueId+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get list of dictionary values based on the given dictionary attribute id")
    @GetMapping("/findByAttributeId")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_VALUE + "\")")
    ResponseEntity<Map<String, Object>> findByDictionaryAttributeId(
            @RequestParam(name = "dictionaryAttributeId", required = true) Long dictionaryAttributeId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY_VALUE_LIST,
                    dictionaryValueService.findByDictionaryAttributeId(dictionaryAttributeId, mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Dictionary values has been fetched  successfully with id ,"+dictionaryAttributeId+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching dictionary value by dictionary attribute Id: " +dictionaryAttributeId+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get list of dictionary values based on the given name,value and dictionary attribute id")
    @GetMapping("/searchDictionaryValue")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_VALUE + "\")")
    ResponseEntity<Map<String, Object>> searchDictionaryValue(
            @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "value", required = true) String value,
            @RequestParam(name = "dictionaryAttributeId", required = true) Long dictionaryAttributeId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<DictionaryValue> dictionaryValueList = dictionaryValueService.searchDictionaryValue(name, value, dictionaryAttributeId, mvnoId);
            Integer responseCode = 0;
            if (dictionaryValueList.isEmpty()) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put(DICTIONARY_VALUE_LIST, dictionaryValueList);
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Dictionary values has been fetced  successfully with attribute id ,"+dictionaryAttributeId+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while searching dictionary values: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }
}
