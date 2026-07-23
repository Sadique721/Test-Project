package com.savbill.notification.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import brave.*;
import brave.propagation.TraceContext;
import com.savbill.notification.utils.*;
import com.savbill.notification.utils.ApiDataValidator;
import com.savbill.notification.utils.LogConstants;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.notification.entity.SmsConfigMapping;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.SmsConfigMappingDto;
import com.savbill.notification.services.SmsConfigMappingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "SMS Config Mapping Management", description = "REST APIs related to SMS Config Mapping Entity!!!!", tags = "SMS Config Mapping")
@RestController
@Slf4j
@RequestMapping("/SavbillNotification")
public class SmsConfigMappingController {
    //final Logger log = Logger.getLogger(SmsConfigMappingController.class);
    private static final String SMS_CONFIG_MAPPING_LIST = "smsConfigMappingList";
    private static final String SMS_CONFIG_MAPPING = "smsConfigMapping";

    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private SmsConfigMappingService smsConfigMappingService;

    @Autowired
    ApiDataValidator apiDataValidator;

    @Autowired
    TokenDataExtractor tokenDataExtractor;
    @Autowired
    Tracer tracer;


    @ApiOperation(value = "Get list of SMS Config Parameter in the system")
    @GetMapping("/smsConfigMappings")

    public ResponseEntity<Map<String, Object>> findAllSmsConfigMappings(@RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                                        HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        ResponseEntity responseEntity = null;
        try {

            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            List<SmsConfigMapping> smsConfigMappingList = smsConfigMappingService.findAllSmsConfigMapping(mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG_MAPPING_LIST, smsConfigMappingList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " fetched Sms config mapping list successfully  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch SMS Configuration List :" + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch SMS Configuration List :" + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch SMS Configuration List :" + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get SMS Config Parameter based on the given SMS Config id")
    @GetMapping("/findSmsConfigMappingBySmsConfigId")

    public ResponseEntity<Map<String, Object>> findSmsConfigMappingBySmsConfigId(@RequestParam(name = "smsConfigId", required = true) Long smsConfigId, @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                                                 HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        ResponseEntity responseEntity = null;
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }

            List<SmsConfigMapping> smsConfigMappings = smsConfigMappingService.findSmsConfigMappingBySmsConfigId(smsConfigId, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG_MAPPING_LIST, smsConfigMappings);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Fetching Sms config mapping List ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Find   SMS Configuration  with id :"+smsConfigId+"" + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Find   SMS Configuration  with id :"+smsConfigId+"" + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Find   SMS Configuration  with id :"+smsConfigId+"" + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Add SMS Config Parameter based on the SMS Config id")
    @PostMapping("/addSmsConfigMapping")

    public ResponseEntity<Map<String, Object>> addSmsConfigMapping(@RequestBody List<SmsConfigMappingDto> smsConfigMappingDtoList, @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                                   HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {

            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }

            List<SmsConfigMapping> smsConfigMappingVoList = smsConfigMappingService.saveSmsConfigMapping(smsConfigMappingDtoList, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG_MAPPING_LIST, smsConfigMappingVoList);
            response.put(NotificationConstants.MESSAGE, "Sms config parameters has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Sms config mapping created successfully  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create  SMS Configuration " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create  SMS Configuration " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create  SMS Configuration " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Update SMS Config Parameter based on the SMS Config id")
    @PutMapping("/updateSmsConfigMapping")

    public ResponseEntity<Map<String, Object>> updateSmsConfigMapping(@RequestBody List<SmsConfigMappingDto> smsConfigMappingDtoList, @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                                      HttpServletRequest request, @RequestParam(name = "smsConfigId", required = true) Long smsConfigId) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {

            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
                apiDataValidator.validateApiData(usermvnoid, mvnoId, smsConfigId, NotificationConstants.SmsConfigMapping_TABLENAME, NotificationConstants.SmsConfigMapping_UPDATEKEY);
            }
            List<SmsConfigMapping> smsConfigMappingVoList = smsConfigMappingService.updateSmsConfigMapping(smsConfigMappingDtoList, mvnoId, smsConfigId,request);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG_MAPPING_LIST, smsConfigMappingVoList);
            response.put(NotificationConstants.MESSAGE, "SMS config parameters has been updated successfully.");
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update  SMS Configuration with id"+ smsConfigId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update  SMS Configuration with id"+ smsConfigId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update  SMS Configuration with id"+ smsConfigId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Delete SMS Config Parameter based on the given SMS Config id")
    @DeleteMapping("/deleteSmsConfigMapping")

    public ResponseEntity<Map<String, Object>> deleteSmsConfigMapping(@RequestParam(name = "smsConfigMappingId", required = true) Long smsConfigMappingId, @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                                      HttpServletRequest request) throws IOException {

        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {

            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
                apiDataValidator.validateApiData(usermvnoid, mvnoId, smsConfigMappingId, NotificationConstants.SmsConfigMapping_TABLENAME, NotificationConstants.SmsConfigMapping_PRIMARYKEY);
            }

            smsConfigMappingService.deleteSmsConfigMappingById(smsConfigMappingId, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(NotificationConstants.MESSAGE, "SMS config parameter has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Notification config deleted successfully, for sms config Id: " + smsConfigMappingId + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);

        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete  SMS Configuration with id"+ smsConfigMappingId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            authException.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete  SMS Configuration with id"+ smsConfigMappingId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete  SMS Configuration with id"+ smsConfigMappingId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return apiResponseController.apiResponse(responseCode, response);
        }
        finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }
}
