package com.savbill.notification.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.notification.Response.Response;
import com.savbill.notification.savbilliwfnotification.response.ResponseHandler;
import com.savbill.notification.savbilliwfnotification.service.IWFSMSService;
import com.savbill.notification.entity.Sms;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.*;
import com.savbill.notification.helper.*;
import com.savbill.notification.services.SmsService;
import com.savbill.notification.snmp.SNMPCounters;
import com.savbill.notification.utils.ApiDataValidator;
import com.savbill.notification.utils.LogConstants;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Api(value = "SMS Management", description = "REST APIs related to SMS Entity!!!!", tags = "SMS")
@RestController
@Slf4j
@RequestMapping("/SavbillNotification")
public class SmsController {
    private static final String SMS_LIST = "smsList";
    private static final String SMS = "sms";
    //final Logger log = Logger.getLogger(SmsController.class);

    private final SNMPCounters snmpCounters = new SNMPCounters();

    @Autowired
    SmsService smsService;
    @Autowired
    APIResponseController apiResponseController;

    @Autowired
    ApiDataValidator apiDataValidator;

    @Autowired
    TokenDataExtractor tokenDataExtractor;

    @Autowired
    Tracer tracer;

    @Autowired
    IWFSMSService iwfsmsService;


    @ApiOperation(value = "Send sms")
    @PostMapping("/sendSms")
    public ResponseEntity<Map<String, Object>> sendSms(@RequestParam(name = "smsid", required = true) Long smsId, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        ResponseEntity responseEntity = null;
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            List<Long> buIdList = tokenDataExtractor.getBUId(request.getHeader("Authorization"));
            if (buIdList.size() > 0) {
                smsService.sendSms(smsId, mvnoId, buIdList.get(0), request);
            } else {
                smsService.sendSms(smsId, mvnoId, null, request);
            }
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(NotificationConstants.MESSAGE, "SMS has been Sent successfully.");
            snmpCounters.incrementSendSmsSuccess();
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " SMS Sent successfully :  " + smsId + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Send  SMS for id " + smsId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Send  SMS for id " + smsId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementSendSmsFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Send  SMS for id " + smsId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalSendSms();
        }
    }


    @ApiOperation(value = "Get list of sms in the system")
    @GetMapping("/smss")
    public ResponseEntity<Map<String, Object>> findAllSmss(@ModelAttribute PaginationDTO paginationDTO, @RequestParam(name = "eventId", required = false) Long eventId, @RequestParam(name = "status", required = false) String status, @RequestParam(name = "mobileNo", required = false) String mobileNo, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        ResponseEntity responseEntity = null;

        try {

            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }

            Long mvnoId = tokenDataExtractor.getMvnoId(request.getHeader("Authorization"));

            List<Long> buIdList = tokenDataExtractor.getBUId(request.getHeader("Authorization"));

            PageableResponse<SmsDataDTO> page = smsService.findAllSmss(eventId, status, mobileNo, mvnoId, paginationDTO, buIdList);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(page.getData())) {
                responseCode = NotificationConstants.SUCCESS;
                if (!Objects.isNull(eventId) && !StringUtils.isBlank(mobileNo) && !StringUtils.isBlank(status)) {
                    response.put(NotificationConstants.MESSAGE, "No record found with the given event type , mobile : " + mobileNo + " and status : " + status);
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to  Fetch SMS for mobile number " + mobileNo + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                } else if (!Objects.isNull(eventId) && !StringUtils.isBlank(mobileNo) && StringUtils.isBlank(status)) {
                    response.put(NotificationConstants.MESSAGE, "No record found with the the given event type and  mobile : " + mobileNo);
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to  Fetch SMS for mobile number " + mobileNo + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                } else if (!Objects.isNull(eventId) && StringUtils.isBlank(mobileNo) && !StringUtils.isBlank(status)) {
                    response.put(NotificationConstants.MESSAGE, "No record found with the the given event type  and status : " + status);
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to  Fetch SMS for mobile number " + mobileNo + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                } else if (Objects.isNull(eventId) && !StringUtils.isBlank(mobileNo) && !StringUtils.isBlank(status)) {
                    response.put(NotificationConstants.MESSAGE, "No record found with mobile : " + mobileNo + " and status : " + status);
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to  Fetch SMS for mobile number " + mobileNo + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                } else {
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to  Fetch SMS for mobile number " + mobileNo + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                    response.put(NotificationConstants.ERROR_MESSAGE, "No Records Found.");
                }
            } else {
                responseCode = NotificationConstants.SUCCESS;
                response.put(SMS_LIST, page);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to  Fetch SMS for mobile number " + mobileNo + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                snmpCounters.incrementGetSmsListSuccess();
            }

            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch  SMS List for mobile number " + mobileNo + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch  SMS List for mobile number " + mobileNo + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetSmsListFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch  SMS List for mobile number " + mobileNo + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementGetTotalSmsList();
        }
    }

    @ApiOperation(value = "Get sms based on the given sms id")
    @GetMapping("/findSmsById")
    public ResponseEntity<Map<String, Object>> findSmsById(@RequestParam(name = "smsid", required = true) Long smsId, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {

            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }

            Sms smsVo = smsService.findSmsById(smsId, mvnoId, false);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS, smsVo);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "SMS details Fetched successfully for smsid " + smsId + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            snmpCounters.incrementGetSmsByIdSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch  SMS details for Sms-id number " + smsId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch  SMS details for Sms-id number " + smsId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetSmsByIdFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch  SMS details for Sms-id number " + smsId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetSmsById();
        }
    }

    @ApiOperation(value = "Get list of sms based on the given source name")
    @GetMapping("/findSmsBySourceName")

    public ResponseEntity<Map<String, Object>> findSmsBySourceName(@RequestParam(name = "eventId", required = false) Long eventId, @RequestParam(name = "status", required = false) String status, @RequestParam(name = "mobileNo", required = false) String mobileNo, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {

            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }

            List<SmsDataDTO> smsList = smsService.findSmsBySourceName(eventId, status, mobileNo, mvnoId);
            Integer Response = 0;
            if (smsList.isEmpty()) {
                Response = NotificationConstants.NULL_VALUE;
                response.put(NotificationConstants.MESSAGE, "No Records Found!");
                response.put("smsList", new ArrayList<>());
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "SMS details fetch successfull " + mobileNo + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                return apiResponseController.apiResponse(Response, response);

            }
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_LIST, smsList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "SMS details fetch successfully, for mobile number " + mobileNo + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            snmpCounters.incrementGetSmsBySourceNameSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch  SMS details for mobile number " + mobileNo + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch  SMS details for mobile number " + mobileNo + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetSmsBySourceNameFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch  SMS details for mobile number " + mobileNo + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetSmsBySourceName();
        }
    }

    @ApiOperation(value = "Add new sms")
    @PostMapping("/addSms")
    public ResponseEntity<Map<String, Object>> addSms(@RequestBody SmsDto sms, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        Long mvnoId = null;
        List<Long> buIdList = new ArrayList<>();
        Integer responseCode = null;
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            mvnoId = tokenDataExtractor.getMvnoId(request.getHeader("Authorization"));

            buIdList = tokenDataExtractor.getBUId(request.getHeader("Authorization"));
            sms.setCreatedBy(tokenDataExtractor.getUserName(request.getHeader("Authorization")));

            if (buIdList != null && buIdList.size() > 1) {
                log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create SMS Because f multiple BU  " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR);
                throw new CustomException(NotificationConstants.AVOID_SAVE_MULTIPLE_BU, NotificationConstants.FAIL);
            } else if (buIdList.size() == 0) {
                Sms smsVo = smsService.saveSms(sms, mvnoId, null);
                responseCode = NotificationConstants.SUCCESS;
                response.put(SMS, smsVo);
                response.put(NotificationConstants.MESSAGE, "SMS has been added successfully.");
                snmpCounters.incrementCreateSmsSuccess();
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "SMS added Successfully ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                return apiResponseController.apiResponse(responseCode, response);
            } else if (buIdList.size() == 1) {
                Sms smsVo = smsService.saveSms(sms, mvnoId, buIdList.get(0));
                responseCode = NotificationConstants.SUCCESS;
                response.put(SMS, smsVo);
                response.put(NotificationConstants.MESSAGE, "SMS has been added successfully.");
                snmpCounters.incrementCreateSmsSuccess();
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "SMS added Successfully ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                return apiResponseController.apiResponse(responseCode, response);
            }
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create SMS  " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create SMS  " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementCreateSmsFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create SMS  " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);

        }

        return apiResponseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Update existing sms data")
    @PutMapping("/updateSms")

    public ResponseEntity<Map<String, Object>> updateSms(@RequestBody UpdateSmsDto updateSmsDto, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Long mvnoId = tokenDataExtractor.getMvnoId(request.getHeader("Authorization"));

            List<Long> buIdList = tokenDataExtractor.getBUId(request.getHeader("Authorization"));
            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
                apiDataValidator.validateApiData(usermvnoid, mvnoId, updateSmsDto.getSmsId(), NotificationConstants.Sms_TABLENAME, NotificationConstants.Sms_PRIMARYKEY);
            }
            if (buIdList.size() > 1) {
                throw new CustomException("You are not allowed to perform this action, Please contact your system administrator", NotificationConstants.EXPECTATION_FAILED);
            } else if (buIdList.size() == 1) {
                Sms smsVo = smsService.updateSms(updateSmsDto, mvnoId, buIdList.get(0), request);
                Integer responseCode = NotificationConstants.SUCCESS;
                response.put(SMS, smsVo);
                response.put(NotificationConstants.MESSAGE, "SMS has been updated successfully.");
                snmpCounters.incrementUpdateSmsSuccess();
                return apiResponseController.apiResponse(responseCode, response);
            } else {
                Sms smsVo = smsService.updateSms(updateSmsDto, mvnoId, null, request);
                Integer responseCode = NotificationConstants.SUCCESS;
                response.put(SMS, smsVo);
                response.put(NotificationConstants.MESSAGE, "SMS has been updated successfully.");
                snmpCounters.incrementUpdateSmsSuccess();
                return apiResponseController.apiResponse(responseCode, response);
            }
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update SMS with id" + updateSmsDto.getSmsId() + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update SMS with id" + updateSmsDto.getSmsId() + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementUpdateSmsFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update SMS with id" + updateSmsDto.getSmsId() + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalUpdateSms();
        }
    }

    @ApiOperation(value = "Delete existing sms data based on the given sms id")
    @DeleteMapping("/deleteSms")

    public ResponseEntity<Map<String, Object>> deleteSms(@RequestParam(name = "smsid", required = true) Long smsId, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {

            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
                apiDataValidator.validateApiData(usermvnoid, mvnoId, smsId, NotificationConstants.Sms_TABLENAME, NotificationConstants.Sms_PRIMARYKEY);
            }
            smsService.deleteSmsById(smsId, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(NotificationConstants.MESSAGE, "SMS has been deleted successfully.");
            snmpCounters.incrementDeleteSmsSuccess();
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Sms with id " + smsId + " deleted successfully  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS);
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete   SMS with id" + smsId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete   SMS with id" + smsId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementDeleteSmsFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete   SMS with id" + smsId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalDeleteSms();
        }
    }

    /**
     * API:- IWF Notification SMS Get Source Master API
     *
     * @param page
     * @param pageSize
     * @param request
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "Fetch Audits", notes = "For retrieving the all sms audits records with pagination")
    @GetMapping("/sms/get-sms-audits")
    public ResponseEntity<Object> getSourceMastersWithPagination(@RequestParam @Valid int page,
                                                                 @RequestParam @Valid int pageSize,
                                                                 @RequestParam @Valid Long mvnoId,
                                                                 HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Page<Sms> emails = iwfsmsService.getSmsAudits(page, pageSize, mvnoId);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetching all email audits," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return ResponseHandler.generateResponse("Total Sources Found: " + emails.getSize(), HttpStatus.OK, emails);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch sms audit records " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NOT_FOUND, null);
        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TYPE);
        }
    }

    /**
     * IWF Notification SMS Get By By Filter with Pagination
     */
    @ApiOperation(value = "Fetch Transaction :: SMS Search Filter - POST Method", notes = "For fetching the active sms by filters with pagination")
    @PostMapping("/get-filtered-sms")
    public ResponseEntity<Object> filterSources(
            @RequestBody PaginationRequestDTO requestDTO,
            @RequestParam(name = "mvnoId") Long mvnoId,
            @RequestParam(name = "serviceType") String serviceType,
            HttpServletRequest request)
            throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());

        try {
            log.debug("fetching by Sms");
            if (smsService.validation(requestDTO)) {
                /** Call Filter Event Template Bind By Name Method */
                Page<SmsDataDTO> dtos = smsService.searchSmsAudit(requestDTO, mvnoId, serviceType);
                Map<String, Object> map = new HashMap<String, Object>();
                if (dtos != null) {
                    map.put("Sms", dtos);
                }
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetching all Sms audits," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                if (dtos == null || dtos.isEmpty()) {
                    log.debug("No Records with Given Field:{}");
                    return ResponseEntity.ok(
                            Response.builder()
                                    .responseTime(LocalDateTime.now())
                                    .status(HttpStatus.NO_CONTENT)
                                    .statusCode(HttpStatus.NO_CONTENT.value())
                                    .message("No records found")
                                    .data(map)
                                    .build()
                    );
                } else {
                    log.debug(" Total Smsfound:{}");
                    return ResponseEntity.ok(
                            Response.builder()
                                    .responseTime(LocalDateTime.now())
                                    .status(HttpStatus.OK)
                                    .statusCode(HttpStatus.OK.value())
                                    .message(NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY)
                                    .data(map)
                                    .build()
                    );
                }
            } else {
                return ResponseEntity.ok(
                        Response.builder()
                                .responseTime(LocalDateTime.now())
                                .status(HttpStatus.RESET_CONTENT)
                                .statusCode(HttpStatus.RESET_CONTENT.value())
                                .method("SmsController.getSms")
                                .executionMessage("Implemented business logic of service class method")
                                .message("Please enter proper value")
                                .data(null)
                                .build()
                );
            }
            //  return ResponseHandler.generateResponse("Total SmsAudit Found: " + dtos.getSize(), HttpStatus.OK, dtos);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch sms audit records " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
//            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.EXPECTATION_FAILED, "NO RECORD FOUND");
            log.error("Exception occurred");
            return ResponseEntity.ok(
                    Response.builder()
                            .responseTime(LocalDateTime.now())
                            .status(HttpStatus.EXPECTATION_FAILED)
                            .statusCode(HttpStatus.EXPECTATION_FAILED.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TYPE);
        }
    }
}
