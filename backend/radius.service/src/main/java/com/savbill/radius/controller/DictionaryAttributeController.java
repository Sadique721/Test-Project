package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.entity.DictionaryAttribute;
import com.savbill.radius.helper.DictionaryAttributeDto;
import com.savbill.radius.helper.UpdateDictionaryAttributeDto;
import com.savbill.radius.services.DictionaryAttributeService;
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

@Api(value = "Dictionary Attribute Management", description = "REST APIs related to Dictionary Attribute Entity!!!!", tags = "Dictionary Attribute")
@RestController
@RequestMapping("/SavbillRadius/dictionary/attribute")
public class DictionaryAttributeController {

    private static final String DICTIONARY_ATTRIBUTE = "dictionaryAttribute";
    private static final String DICTIONARY_ATTRIBUTE_LIST = "dictionaryAttributeList";

    @Autowired
    private DictionaryAttributeService dictionaryAttributeService;
    @Autowired
    private APIResponseController apiResponseController;
	@Autowired
	private Tracer tracer;
    private static final Logger log = LoggerFactory.getLogger(DictionaryAttributeController.class);
    @ApiOperation(value = "Get list of dictionary attributes in the system")
    @GetMapping("/findAll")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    ResponseEntity<Map<String, Object>> findAllDictionaryAttributes(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY_ATTRIBUTE_LIST, dictionaryAttributeService.findAllDictionaryAttributes(mvnoId));
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionary Attributes has been fetched successfully,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Throwable e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetchingDirectory attributes: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get dictionary attribute based on the given dictionary attribute id")
    @GetMapping("/findById")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_ATTRIBUTES + "\")")
    ResponseEntity<Map<String, Object>> findDictionaryAttributeById(
            @RequestParam(name = "dictionaryAttributeId", required = true) Long dictionaryAttributeId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY_ATTRIBUTE, dictionaryAttributeService.findDictionaryAttributeById(dictionaryAttributeId, mvnoId));
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionary Attributes has been fetched successfully with id,"+dictionaryAttributeId+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching directory attribute with id : "+dictionaryAttributeId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get list of dictionary attributes baased on the given dictionary attribute name")
    @GetMapping("/findByName")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    ResponseEntity<Map<String, Object>> findDictionaryAttributeByName(
            @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY_ATTRIBUTE_LIST, dictionaryAttributeService.findByName(name, mvnoId));
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionary Attributes has been fetched successfully with name,"+name+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching attribute with name: "+name + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Add new dictionary attribute")
    @PostMapping("/save")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_ATTRIBUTES_CREATE + "\")")
    ResponseEntity<Map<String, Object>> saveDictionaryAttribute(
            @RequestBody DictionaryAttributeDto dictionaryAttribute,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY_ATTRIBUTE, dictionaryAttributeService.saveDictionaryAttribute(dictionaryAttribute, mvnoId));
            response.put(RadiusConstants.MESSAGE, "Dictionary attribute has been added successfully.");
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionary Attributes has been created successfully with name,"+dictionaryAttribute.getName()+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while creating attribute categories: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Update dictionary attribute based on the given dictionary attribute id")
    @PutMapping("/update")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_ATTRIBUTES_EDIT + "\")")
    ResponseEntity<Map<String, Object>> updateDictionaryAttribute(
            @RequestBody UpdateDictionaryAttributeDto dictionaryAttribute,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put(DICTIONARY_ATTRIBUTE,
                    dictionaryAttributeService.updateDictionaryAttribute(dictionaryAttribute, mvnoId));
            response.put(RadiusConstants.MESSAGE, "Dictionary attribute has been updated successfully.");
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionary Attributes has been Updated successfully,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to update ditionary attribute: "+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Delete dictionary attribute based on the given dictionary attribute id")
    @DeleteMapping("/delete")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','deleteAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_ATTRIBUTES_DELETE + "\")")
    ResponseEntity<Map<String, Object>> deleteDictionaryAttribute(
            @RequestParam(name = "dictionaryAttributeId", required = true) Long dictionaryAttributeId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            dictionaryAttributeService.deleteDictionaryAttribute(dictionaryAttributeId, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Dictionary attribute has been deleted successfully.");
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionary Attributes has been deleted successfully with id,"+dictionaryAttributeId+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting attribute categories: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get list of dictionary attributes based on the given dictionary id")
    @GetMapping("/findByDictionaryId")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_ATTRIBUTES + "\")")
    ResponseEntity<Map<String, Object>> findDictionaryAttributeByDictionaryId(
            @RequestParam(name = "dictionaryId", required = true) Long dictionaryId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(DICTIONARY_ATTRIBUTE_LIST, dictionaryAttributeService.findByDictionaryId(dictionaryId, mvnoId));
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionary Attributes has been fetched successfully with id,"+dictionaryId+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching attribute categories: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get list of attribute categories")
    @GetMapping("/getAttributeCategories")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_DICT_ATTRIBUTES + "\")")
    ResponseEntity<Map<String, Object>> getAttributeCategories(HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put("attributeCategoryList", dictionaryAttributeService.getAttributeCategories());
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionary Attribute categories has been fetched successfully,"+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching attribute categories: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }

    @ApiOperation(value = "Get list of dictionary attributes based on the given name and dictionaryId")
    @GetMapping("/searchAttribute")
//    @PreAuthorize("@roleAccesses.hasPermission('dictionary','readAccess',#request.getHeader('requestFrom'))")
    ResponseEntity<Map<String, Object>> searchDictionaryAttribute(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "dictionaryId", required = false) Long dictionaryId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<DictionaryAttribute> dictionaryAttributeList = dictionaryAttributeService.searchDictionaryAttribute(name, dictionaryId, mvnoId);
            Integer responseCode = 0;
            if (dictionaryAttributeList.isEmpty()) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
				log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Dictionary Attributes has been fetched successfully with name"+name+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.NOT_FOUND.value());
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put(DICTIONARY_ATTRIBUTE_LIST, dictionaryAttributeList);
				log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Dictionary Attributes has been fetched successfully with name,"+name+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            }
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while searching dictionary attribute by name: "+name + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
    }
}
