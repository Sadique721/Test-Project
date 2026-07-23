package com.savbill.notification.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.notification.entity.NotificationConfig;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.services.NotificationConfigService;
import com.savbill.notification.services.SmsConfigService;
import com.savbill.notification.snmp.SNMPCounters;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/SavbillNotification")
public class NotificationConfigController {

    private static final String SMSCONFIG_LIST = "smsConfigList";
    private static final String SMS_CONFIG = "smsConfig";
    //final Logger log = Logger.getLogger(SmsController.class);

    private final SNMPCounters snmpCounters = new SNMPCounters();

    @Autowired
    SmsConfigService smsConfigService;

    @Autowired
    NotificationConfigService notificationConfigService;
    @Autowired
    APIResponseController apiResponseController;

    @Autowired
    ApiDataValidator apiDataValidator;

    @Autowired
    TokenDataExtractor tokenDataExtractor;
    @Autowired
    Tracer tracer;


    @GetMapping("/notificationConfigs")

    public ResponseEntity<Map<String, Object>> findAllnoticiationConfig(
            @RequestParam(name = "mvnoId", required = true) Long mvnoId, @RequestParam(name = "buId", required = false) Long buId, HttpServletRequest request) throws IOException {
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
            if(buId == 0){
                buId = null;
            }
            List<NotificationConfig> notificationConfigList = notificationConfigService.findAllSmsConfig(mvnoId , buId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMSCONFIG_LIST, notificationConfigList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Notification config details fetch successfully ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            snmpCounters.incrementGetSmsConfigListSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable fetch notification Config" + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable fetch notification Config" + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {

            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetSmsConfigListFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable fetch notification Config" + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetSmsConfig();
        }
    }

    @PostMapping("/addNotificationConfig")

    public ResponseEntity<Map<String, Object>> addSmsConfig(
            @RequestParam(name = "smsUrl", required = true) String smsUrl,
            @RequestParam(name = "mvnoId", required = true) Long mvnoId,
            @RequestParam(name = "createdBy", required = true) String createdBy,
            @RequestParam(name = "buId", required = false) Long buId
            , HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {

            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
             if(buId == 0){
                 buId = null;
             }
            NotificationConfig notificationConfig = notificationConfigService.addNotificationConfig(smsUrl, mvnoId, createdBy,buId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG, notificationConfig);
            response.put(NotificationConstants.MESSAGE, "Notification Configuration has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Notification Configuration added successfully  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            snmpCounters.incrementCreateSmsConfigSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create SmsConfig " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create SmsConfig " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementCreateSmsConfigFailure();;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create SmsConfig " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalCreateSmsConfig();
        }
    }

    @PutMapping("/updateNotificationConfig")
    public ResponseEntity<Map<String, Object>> updateSmsConfig(@RequestBody NotificationConfig notificationConfig,
                                                               @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {
            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
            }
            NotificationConfig notificationConfig1 = notificationConfigService.updateNotificationConfig(notificationConfig, mvnoId,request);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG, notificationConfig1);
            response.put(NotificationConstants.MESSAGE, "Notification Configuration has been updated successfully.");
            snmpCounters.incrementUpdateSmsConfigSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to update Notification Configuration with id"+ notificationConfig.getNotificationconfigId()+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to update Notification Configuration with id"+ notificationConfig.getNotificationconfigId()+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementUpdateSmsConfigFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to update Notification Configuration with id"+ notificationConfig.getNotificationconfigId()+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalUpdateSmsConfig();
        }
    }

    @GetMapping("/findNotificationConfigById")
    public ResponseEntity<Map<String, Object>> findSmsById(@RequestParam(name = "notificationConfigId", required = true) Long notificationConfigId, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            NotificationConfig notificationConfig = notificationConfigService.findNotificationConfigById(notificationConfigId, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG, notificationConfig);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Notification config details fetch successfully, for sms config Id: " + notificationConfigId + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            snmpCounters.incrementGetSmsByIdSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch Notification Configuration with id"+ notificationConfigId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch Notification Configuration with id"+ notificationConfigId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetSmsByIdFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch Notification Configuration with id"+ notificationConfigId+" " + LogConstants.REQUEST_BY +tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetSmsById();
        }
    }
}
