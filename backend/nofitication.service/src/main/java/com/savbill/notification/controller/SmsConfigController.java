package com.savbill.notification.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.notification.Mvno.repository.MvnoRepository;
import com.savbill.notification.Response.Response;
import com.savbill.notification.savbilliwfnotification.response.ResponseHandler;
import com.savbill.notification.entity.SmsConfig;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.PaginationRequestDTO;
import com.savbill.notification.helper.SearchSmsRespDto;
import com.savbill.notification.services.SmsConfigService;
import com.savbill.notification.snmp.SNMPCounters;
import com.savbill.notification.utils.ApiDataValidator;
import com.savbill.notification.utils.LogConstants;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@RequestMapping("/SavbillNotification")
public class SmsConfigController {

    private static final String SMSCONFIG_LIST = "smsConfigList";
    private static final String SMS_CONFIG = "smsConfig";
    //final Logger log = Logger.getLogger(SmsController.class);

    private final SNMPCounters snmpCounters = new SNMPCounters();

    @Autowired
    SmsConfigService smsConfigService;
    @Autowired
    APIResponseController apiResponseController;

    @Autowired
    ApiDataValidator apiDataValidator;

    @Autowired
    TokenDataExtractor tokenDataExtractor;
    @Autowired
    Tracer tracer;

    @Autowired
    MvnoRepository mvnoRepository;

    /**
     * API: Find All SMS Configuration Without Pagination
     *
     * @param mvnoId
     * @param buId
     * @param serviceType
     * @param request
     * @return
     * @throws AuthException
     * @throws IOException
     */
    @GetMapping("/smsConfigs")
    public ResponseEntity<Map<String, Object>> findAllSmsConfig(
            @RequestParam(name = "mvnoId", required = true) Long mvnoId, @RequestParam(name = "buId", required = false) Long buId, @RequestParam(name = "serviceType", required = false, defaultValue = "BSS") String serviceType, HttpServletRequest request) throws AuthException, IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        ResponseEntity responseEntity = null;
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            if (buId == 0) {
                buId = null;
            }
            List<SmsConfig> smsConfigList = smsConfigService.findAllSmsConfig(mvnoId, buId, serviceType);
            if (!smsConfigList.isEmpty()) {
                smsConfigList.stream()
                        .filter(smsConfig -> smsConfig.getMvnoId() != null)
                        .forEach(smsConfig -> mvnoRepository.findById(smsConfig.getMvnoId())
                                .ifPresent(mvno -> smsConfig.setMvnoName(mvno.getName())));
            }
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMSCONFIG_LIST, smsConfigList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " SMS config details fetch successfully  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            snmpCounters.incrementGetSmsConfigListSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch SMS config list  " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            customException.printStackTrace();
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch SMS config list  " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            authException.printStackTrace();
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetSmsConfigListFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch SMS config list  " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetSmsConfig();
        }
    }

    /**
     * API:- Create SMS Configaration
     *
     * @param smsUrl
     * @param mvnoId
     * @param configStatus
     * @param createdBy
     * @param buId
     * @param serviceType
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/addSmsConfig")
    public ResponseEntity<Map<String, Object>> addSmsConfig(
            @RequestParam(name = "smsUrl", required = true) String smsUrl,
            @RequestParam(name = "mvnoId", required = true) Long mvnoId,
            @RequestParam(name = "configStatus", required = true) Boolean configStatus,
            @RequestParam(name = "createdBy", required = true) String createdBy,
            @RequestParam(name = "buId", required = false) Long buId,
            @RequestParam(name = "serviceType", required = false) String serviceType,
            HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            if (buId == 0) {
                buId = null;
            }
            SmsConfig smsConfigVo = smsConfigService.addSmsConfig(smsUrl, mvnoId, createdBy, buId, configStatus, serviceType);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG, smsConfigVo);
            response.put(NotificationConstants.MESSAGE, NotificationConstants.API_Response_Message.CREATED_SUCCESSFULLY);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " SMS config details Created  successfully  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS);
            snmpCounters.incrementCreateSmsConfigSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create SMS config  " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            customException.printStackTrace();
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create SMS config  " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            authException.printStackTrace();
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementCreateSmsConfigFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create SMS config  " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalCreateSmsConfig();
        }
    }

    /**
     * API: Update SMS Configuration
     *
     * @param smsConfig
     * @param mvnoId
     * @param request
     * @return
     * @throws IOException
     */
    @PutMapping("/updateSmsConfig")
    public ResponseEntity<Map<String, Object>> updateSmsConfig(@RequestBody SmsConfig smsConfig, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {
            if ((request.getHeader("requestFrom") == null)) {

                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
                apiDataValidator.validateApiData(usermvnoid, mvnoId, smsConfig.getSmsUrl(), NotificationConstants.SmsConfig_TABLENAME, NotificationConstants.SmsConfig_PRIMARYKEY);
            }
            SmsConfig smsConfigVo = smsConfigService.updateSmsConfig(smsConfig, mvnoId, request);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG, smsConfigVo);
            response.put(NotificationConstants.MESSAGE, NotificationConstants.API_Response_Message.UPDATED_SUCCESSFULLY);
            snmpCounters.incrementUpdateSmsConfigSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update SMS config with id: " + smsConfig.getSmsConfigId() + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            customException.printStackTrace();
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update SMS config with id: " + smsConfig.getSmsConfigId() + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            authException.printStackTrace();
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementUpdateSmsConfigFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update SMS config with id: " + smsConfig.getSmsConfigId() + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalUpdateSmsConfig();
        }
    }

    /**
     * API: Find SMS Configuration By Id
     *
     * @param smsConfigId
     * @param mvnoId
     * @param request
     * @return
     * @throws AuthException
     * @throws IOException
     */
    @GetMapping("/findSmsConfigById")
    public ResponseEntity<Map<String, Object>> findSmsById(@RequestParam(name = "smsConfigId", required = true) Long smsConfigId, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws AuthException, IOException {
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
            SmsConfig smsConfig = smsConfigService.findSmsConfigById(smsConfigId, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(SMS_CONFIG, smsConfig);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " SMS config details with id :" + smsConfigId + " fetch successfully  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            snmpCounters.incrementGetSmsByIdSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "SMS config details fetch failed, for sms config Id: " + smsConfigId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            customException.printStackTrace();
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "SMS config details fetch failed, for sms config Id: " + smsConfigId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            authException.printStackTrace();
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetSmsByIdFailure();
            e.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "SMS config details fetch failed, for sms config Id: " + smsConfigId + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetSmsById();
        }
    }

    /**
     * API: Get SMS Configuration With Pagination
     *
     * @param page
     * @param size
     * @param mvnoId
     * @param buId
     * @param serviceType
     * @param request
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "Fetch Transaction :: SMS Configuration - GET Method",
            notes = "For fetching the all sms configuration with pagination")
    @GetMapping("/getSMSConfigList")
    public ResponseEntity<Object> getSmsConfigurationWithPagination(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "5", required = false) Integer size,
            @RequestParam(name = "mvnoId", required = true) Long mvnoId,
            @RequestParam(name = "buId", required = false) Long buId,
            @RequestParam(name = "serviceType", required = true) String serviceType,
            HttpServletRequest request) throws IOException {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            /** Call Get SMS Configuration With Pagination Method */
            Page<SmsConfig> smsConfigs = smsConfigService.getSmsConfigWithPagination(page, size, mvnoId, buId, serviceType);
            if (!smsConfigs.isEmpty()) {
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " SMS Config fetched successfully," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY, HttpStatus.OK, smsConfigs);
            } else {
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " No SMS Config found," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.NO_CONTENT.value());
                return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.NO_RECORDS_FOUND, HttpStatus.OK, smsConfigs);
            }
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable fetch sms config " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NOT_FOUND, null);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @GetMapping("/SerchSmsByurl")
    public ResponseEntity<Object> SearchSmsByurl(@RequestParam(name = "smsConfigurl", required = true) String smsConfingurl,
                                                 @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                 @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                                 @RequestParam(name = "size", defaultValue = "5", required = false) Integer size,
                                                 HttpServletRequest request
    ) throws AuthException, IOException {
        try {
            Page<SearchSmsRespDto> respDto = smsConfigService.SerchSmsConfig(smsConfingurl, mvnoId, page, size);
            return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY, HttpStatus.OK, respDto);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NO_CONTENT, null);
        }
    }

    @PostMapping("/SearchSmsConfig")
    public ResponseEntity<Object> getFilteredSmsconfig(
            @RequestBody PaginationRequestDTO requestDTO,
            @RequestParam(name = "mvnoId") Long mvnoId,
            @RequestParam(name = "serviceType") String serviceType,
            HttpServletRequest request) throws IOException {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());

        try {
            log.debug("Fetching SmsConfig By Url...");
            if (smsConfigService.validation(requestDTO)) {
                Page<SearchSmsRespDto> dtos = smsConfigService.SmsConfig(requestDTO, mvnoId, serviceType);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " SMS config fetched successfully," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                if (dtos == null || dtos.isEmpty()) {
                    log.debug("No Records with Given Field:{}");
                    return ResponseEntity.ok(
                            Response.builder()
                                    .responseTime(LocalDateTime.now())
                                    .status(HttpStatus.NO_CONTENT)
                                    .statusCode(HttpStatus.NO_CONTENT.value())
                                    .message(NotificationConstants.API_Response_Message.NO_RECORDS_FOUND)
                                    .data(null)
                                    .build()
                    );
                } else {
                    log.debug(" Total SmsCongig:{}");
                    Map<String, Object> map = new HashMap<String, Object>();
                    if (dtos != null) {
                        map.put("SmsConfig", dtos);
                    }
                    return ResponseEntity.ok(
                            Response.builder()
                                    .responseTime(LocalDateTime.now())
                                    .status(HttpStatus.OK)
                                    .statusCode(HttpStatus.OK.value())
                                    .message(NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY)
                                    .data(map)
                                    .build()
                    );
                }  //     return ResponseHandler.generateResponse("Data fetched Successfully: " + dtos.getSize(), HttpStatus.OK, dtos);
            } else {
                return ResponseEntity.ok(
                        Response.builder()
                                .responseTime(LocalDateTime.now())
                                .status(HttpStatus.RESET_CONTENT)
                                .statusCode(HttpStatus.RESET_CONTENT.value())
                                .method("SmsConfig.getSmsConfig")
                                .executionMessage("Implemented business logic of service class method")
                                .message("Please enter proper value")
                                .data(null)
                                .build()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable fetch sms config " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
//            return ResponseHandler.generateResponse(e.getMessage(),HttpStatus.NO_CONTENT,null);
            log.error("Exception occurred: {}");
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
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }
}
