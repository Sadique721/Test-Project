package com.savbill.radius.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.entity.Dictionary;
import com.savbill.radius.utils.LogConstants;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.radius.helper.DictionaryDto;
import com.savbill.radius.helper.UpdateDictionaryDto;
import com.savbill.radius.services.DictionaryService;
import com.savbill.radius.utils.RadiusConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Dictionary Management", description = "REST APIs related to Dictionary Entity!!!!", tags = "Dictionary")
@RestController
@RequestMapping("/SavbillRadius/dictionary")
public class DictionaryController {

    private static final String DICTIONARY = "dictionary";
    private static final String DICTIONARY_LIST = "dictionaryList";

    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    private Tracer tracer;
    private static final Logger log = LoggerFactory.getLogger(DictionaryController.class);
    @ApiOperation(value = "Get list of dictionaries in the system")
    @GetMapping("/findAll")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT + "\")")
    ResponseEntity<Map<String, Object>> findAllDictionaries(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY_LIST, dictionaryService.findAllDictionaries(mvnoId));
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionaries has been fetched successfully ,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Throwable e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching dictionaries"+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get dictionary based on the given dictionary id")
    @GetMapping("/findById")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    ResponseEntity<Map<String, Object>> findDictionaryById(
            @RequestParam(name = "dictionaryId", required = true) Long dictionaryId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY, dictionaryService.findDictionaryById(dictionaryId, mvnoId));
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionaries has been fetched successfully with id,"+dictionaryId+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetcing dictionaries by id"+dictionaryId+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get list of dictionaries based on the vendor")
    @GetMapping("/findByVednor")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    ResponseEntity<Map<String, Object>> findDictionaryByVendor(
            @RequestParam(name = "vendor", required = true) String vendor,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);;
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            response.put(DICTIONARY_LIST, dictionaryService.findByVendor(vendor, mvnoId));
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionaries has been fetched successfully by vendor ,"+vendor+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching dictionaries by vendor"+vendor + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Add new dictionary")
    @PostMapping("/save")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_CREATE + "\")")
    ResponseEntity<Map<String, Object>> saveDictionary(
            @RequestBody DictionaryDto dictionary,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY, dictionaryService.saveDictionary(dictionary, mvnoId));
            response.put(RadiusConstants.MESSAGE, "Dictionary has been added successfully.");
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionaries has been added successfully ,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating  dictionary" + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Update existing dictionary data based on the given dictionary id")
    @PutMapping("/update")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_EDIT + "\")")
    ResponseEntity<Map<String, Object>> updateDictionary(
            @RequestBody UpdateDictionaryDto dictionary,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY, dictionaryService.updateDictionary(dictionary, mvnoId,request));

            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while updating dictionaries" +dictionary.getDictionaryId()+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Delete dictionary based on the given dictionary id")
    @DeleteMapping("/delete")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','deleteAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_DELETE + "\")")
    ResponseEntity<Map<String, Object>> deleteDictionary(
            @RequestParam(name = "dictionaryId", required = true) Long dictionaryId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            dictionaryService.deleteDictionary(dictionaryId, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Dictionary has been deleted successfully.");
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionaries has been deleed successfully wirh id ,"+dictionaryId+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting dictonaries  " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get list of vendor types in the system")
    @GetMapping("/getVendorType")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT + "\")")
    ResponseEntity<Map<String, Object>> getVendorType(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put("vendorTypeList", dictionaryService.getVendorType());
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "vendor type  has been fetched successfully ,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Fetching Vendors  " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get list of dictionaries based on the given vendor,vandorId and vendorType")
    @GetMapping("/searchDictionary")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    ResponseEntity<Map<String, Object>> searchDictionary(
            @RequestParam(name = "vendor", required = false) String vendor,
            @RequestParam(name = "vendorId", required = false) String vendorId,
            @RequestParam(name = "vendorType", required = false) String vendorType,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<Dictionary> dictionaryList = dictionaryService.searchDictionary(vendor, vendorId, vendorType, mvnoId);
            Integer responseCode = 0;
            if (dictionaryList.isEmpty()) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put(DICTIONARY_LIST, dictionaryList);

            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionaries has been fetched successfully by vendor ,"+vendor+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Throwable e) {
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetchingDirectory attributes: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            apiResponseController.buildErrorMessageForResponse(response, e);
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }
}
