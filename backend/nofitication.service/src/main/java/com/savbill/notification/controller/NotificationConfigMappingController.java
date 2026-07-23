package com.savbill.notification.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.notification.entity.NotificationConfigMapping;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.NotificationConfigMappingDto;
import com.savbill.notification.services.NotificationConfigMappingService;
import com.savbill.notification.services.SmsConfigMappingService;
import com.savbill.notification.utils.*;
import com.savbill.notification.utils.ApiDataValidator;
import com.savbill.notification.utils.LogConstants;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "SMS Config Mapping Management", description = "REST APIs related to SMS Config Mapping Entity!!!!", tags = "SMS Config Mapping")
@RestController
@Slf4j
@RequestMapping("/SavbillNotification")
public class NotificationConfigMappingController {
    private static final String SMS_CONFIG_MAPPING_LIST = "smsConfigMappingList";
    private static final String SMS_CONFIG_MAPPING = "smsConfigMapping";
    //final Logger log = Logger.getLogger(NotificationConfigMappingController.class);
    @Autowired
    ApiDataValidator apiDataValidator;
    @Autowired
    TokenDataExtractor tokenDataExtractor;
    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    private SmsConfigMappingService smsConfigMappingService;
    @Autowired
    private NotificationConfigMappingService notificationConfigMappingService;
    @Autowired
    Tracer tracer;

    @ApiOperation(value = "Get list of SMS Config Parameter in the system")
    @GetMapping("/notificationConfigMappings")

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
            List<NotificationConfigMapping> notificationConfigMappingList = notificationConfigMappingService.findAllNotificationConfigMapping(mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG_MAPPING_LIST, notificationConfigMappingList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Notification config mapping details fetch successfully:  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Notification config mapping List: " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Notification config mapping List: " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Notification config mapping List: " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get SMS Config Parameter based on the given SMS Config id")
    @GetMapping("/findNotificationConfigMappingByNotificationConfigId")

    public ResponseEntity<Map<String, Object>> findSmsConfigMappingBySmsConfigId(@RequestParam(name = "notificationConfigId", required = true) Long notificationConfigId, @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                                                 HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }

            List<NotificationConfigMapping> notificationConfigMappings = notificationConfigMappingService.findNotificationConfigMappingBySmsConfigId(notificationConfigId, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG_MAPPING_LIST, notificationConfigMappings);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Notification config mapping details with id "+notificationConfigId+" fetch successfully:  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Notification config mapping: for id "+notificationConfigId+ " " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Notification config mapping: for id "+notificationConfigId+ " " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Notification config mapping: for id "+notificationConfigId+ " " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Add SMS Config Parameter based on the SMS Config id")
    @PostMapping("/addNotificationConfigMapping")

    public ResponseEntity<Map<String, Object>> addSmsConfigMapping(@RequestBody List<NotificationConfigMappingDto> notificationConfigMappingDtoList, @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                                   HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {

            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }

            List<NotificationConfigMapping> notificationConfigMappingList = notificationConfigMappingService.saveNotificationConfigMapping(notificationConfigMappingDtoList, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG_MAPPING_LIST, notificationConfigMappingList);
            response.put(NotificationConstants.MESSAGE, "Notification config parameters has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Notification config mapping created successfully   ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS);
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to create Notification config mapping: " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to create Notification config mapping: " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to create Notification config mapping: " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Update SMS Config Parameter based on the SMS Config id")
    @PutMapping("/updateNotificationConfigMapping")

    public ResponseEntity<Map<String, Object>> updateSmsConfigMapping(@RequestBody List<NotificationConfigMappingDto> notificationConfigMappingDtoList, @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                                      HttpServletRequest request, @RequestParam(name = "smsConfigId", required = true) Long smsConfigId) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {

            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
            }

            List<NotificationConfigMapping> notificationConfigMappingVoList = notificationConfigMappingService.updateNotificationConfigMapping(notificationConfigMappingDtoList, mvnoId, smsConfigId,request);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG_MAPPING_LIST, notificationConfigMappingVoList);
            response.put(NotificationConstants.MESSAGE, "Notification config parameters has been updated successfully.");
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            customException.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to update Notification config mapping  for sms config id :" + smsConfigId + " " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            authException.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to update Notification config mapping  for sms config id :" + smsConfigId + " " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to update Notification config mapping  for sms config id :" + smsConfigId + " " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Delete SMS Config Parameter based on the given SMS Config id")
    @DeleteMapping("/deleteNotificationConfigMapping")

    public ResponseEntity<Map<String, Object>> deleteSmsConfigMapping(@RequestParam(name = "notificationConfigMappingId", required = true) Long notificationConfigMappingId, @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                                      HttpServletRequest request) throws IOException {

        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {

            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
            }
            notificationConfigMappingService.findNotificationConfigMappingBySmsConfigId(notificationConfigMappingId, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(NotificationConstants.MESSAGE, "Notification config parameter has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Notification config mapping deleted successfully with id "+notificationConfigMappingId+" fetch successfully:  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);

        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete Notification config mapping for sms config with id : "+notificationConfigMappingId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            customException.printStackTrace();
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            authException.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete Notification config mapping for sms config with id : "+notificationConfigMappingId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete Notification config mapping for sms config with id : "+notificationConfigMappingId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }
}
