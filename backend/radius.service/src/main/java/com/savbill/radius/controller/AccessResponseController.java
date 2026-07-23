package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.dto.AccessResponseDto;
import com.savbill.radius.entity.AccessResponse;
import com.savbill.radius.services.AccessResponseService;
import com.savbill.radius.utils.CustomValidationException;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.PaginationRequestDTO;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(value = "Radius Customer Reply Management", description = "REST APIs related to Customer Reply Entity!!!!", tags = "Radius Customer Reply")
@RestController
@RequestMapping("/SavbillRadius")
public class AccessResponseController {

    private static final String ACCESS_RESPONSE_LIST = "accessResponseList";
    private static final String ACCESS_RESPONSE = "accessResponse";
    @Autowired
    AccessResponseService accessResponseService;

    @Autowired
    private APIResponseController apiResponseController;

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private Tracer tracer;

    @ApiOperation(value = "Add new Accessresponse")
    @PostMapping("/addAccessResponse")
    @PreAuthorize("validatePermission(\"" + MenuConstants.ACCESS_RESPONSE_CREATE + "\")")
    public ResponseEntity<Map<String, Object>> addAccessResponse(@RequestBody AccessResponseDto accessResponseDto, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        Integer responseCode = RadiusConstants.FAIL;
        try {
            AccessResponse accessResponse = accessResponseService.saveAccessResponse(accessResponseDto);
            responseCode = RadiusConstants.SUCCESS;
            response.put(ACCESS_RESPONSE, accessResponse);
            response.put(RadiusConstants.MESSAGE, "Access Response has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Access Response has been created successfully:,"  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
        }catch (CustomValidationException ce) {
            response.put(RadiusConstants.MESSAGE, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating Access Response ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
        } catch (Exception e) {
            responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating Access Response ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Add new Accessresponse")
    @PreAuthorize("validatePermission(\"" + MenuConstants.ACCESS_RESPONSE_EDIT + "\")")
    @PostMapping("/updateAccessResponse")
    public ResponseEntity<Map<String, Object>> updateAccessResponse(@RequestBody AccessResponseDto accessResponseDto, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        Integer responseCode = RadiusConstants.FAIL;
        try {
            AccessResponse accessResponse = accessResponseService.updateAccessResponse(accessResponseDto);
            responseCode = RadiusConstants.SUCCESS;
            response.put(ACCESS_RESPONSE, accessResponse);
            response.put(RadiusConstants.MESSAGE, "Access Response has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Access Response has been created successfully:,"  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
        }catch (CustomValidationException ce) {
            response.put(RadiusConstants.MESSAGE, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating Access Response ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
        } catch (Exception e) {
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating Access Response ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
        return apiResponseController.apiResponse(responseCode, response);
    }



    @PreAuthorize("validatePermission(\"" + MenuConstants.ACCESS_RESPONSE + "\")")
    @GetMapping("/accessResponse/{id}")
    public ResponseEntity<?> getaccessResponseById(@PathVariable Long id, HttpServletRequest req) throws Exception {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        Integer responseCode = RadiusConstants.FAIL;
        try {
            AccessResponse accessResponse = accessResponseService.findAccessResponsebyId(id);
            responseCode = RadiusConstants.SUCCESS;
            response.put(ACCESS_RESPONSE_LIST, accessResponse);
            response.put(RadiusConstants.MESSAGE, "Access Response fetch successfully.");
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Access Response has been fetched successfully:,"  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
        } catch (Exception e) {
            responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Access Response ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
        return apiResponseController.apiResponse(responseCode, response);
    }



    @PreAuthorize("validatePermission(\"" + MenuConstants.ACCESS_RESPONSE + "\")")
    @PostMapping("/accessResponse")
    public ResponseEntity<?> getaccessResponse( HttpServletRequest req, @RequestBody PaginationRequestDTO requestDTO) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        Integer responseCode = RadiusConstants.FAIL;
        Page<AccessResponse> accessResponse = null;
        try {
            accessResponse = accessResponseService.findAccessResponse(requestDTO);
            responseCode = RadiusConstants.SUCCESS;
            response.put(ACCESS_RESPONSE_LIST, accessResponse.getContent());
            response.put(RadiusConstants.MESSAGE, "Access Response fetch successfully.");
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Access Response has been fetched successfully:,"  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
        } catch (Exception e) {
            responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Access Response ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
        return apiResponseController.apiResponse(responseCode, response,accessResponse);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.ACCESS_RESPONSE_DELETE + "\")")
    @GetMapping("/deleteAccesresponse/{id}")
    public ResponseEntity<?> deleteaccessResponseById(@PathVariable Long id, HttpServletRequest req) throws Exception {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        Integer responseCode = RadiusConstants.FAIL;
        try {
            AccessResponse accessResponse = accessResponseService.deleteAccessResponsebyId(id);
            responseCode = RadiusConstants.SUCCESS;
            response.put(ACCESS_RESPONSE, accessResponse);
            response.put(RadiusConstants.MESSAGE, "Access Response fetch successfully.");
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Access Response has been fetched successfully:,"  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            } catch (Exception e) {
            responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Access Response ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
        return apiResponseController.apiResponse(responseCode, response);
    }
}
