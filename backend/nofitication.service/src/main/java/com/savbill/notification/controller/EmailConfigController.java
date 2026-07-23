package com.savbill.notification.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.notification.Mvno.repository.MvnoRepository;
import com.savbill.notification.Response.Response;
import com.savbill.notification.savbilliwfnotification.response.ResponseHandler;
import com.savbill.notification.entity.EmailConfig;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.EmailConfigDto;
import com.savbill.notification.helper.PaginationRequestDTO;
import com.savbill.notification.helper.PasswordDto;
import com.savbill.notification.helper.UpdateEmailConfigDto;
//import com.savbill.notification.rabbitmq.MessageSender;
import com.savbill.notification.repository.EmailConfigRepository;
import com.savbill.notification.services.EmailConfigService;
import com.savbill.notification.snmp.SNMPCounters;
import com.savbill.notification.utils.*;
import com.savbill.notification.utils.*;
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
public class EmailConfigController {
    private static final String EMAILCONFIG_LIST = "emailConfigList";
    private static final String EMAIL_CONFIG = "emailConfig";
    //private final Logger log = Logger.getLogger(EmailConfigController.class);
    private final SNMPCounters snmpCounters = new SNMPCounters();
    @Autowired
    EmailConfigService emailConfigService;
    @Autowired
    APIResponseController apiResponseController;
    @Autowired
    ApiDataValidator apiDataValidator;
    @Autowired
    TokenDataExtractor tokenDataExtractor;
//    @Autowired
//    MessageSender messageSender;
    @Autowired
    EmailConfigRepository emailConfigRepository;
    @Autowired
    private UpdateDiffFinder updateDiffFinder;

    @Autowired
    private Tracer tracer;

    @Autowired
    private MvnoRepository mvnoRepository;

    /**
     * API: Get All Email Configuration Without Pagination
     *
     * @param mvnoId
     * @param buId
     * @param request
     * @return
     */
    @ApiOperation(value = "Fetch Transaction :: SourceMaster - GET Method", notes = "For retrieving the all active source master records without pagination")
    @GetMapping("/emailConfigs")
    public ResponseEntity<Map<String, Object>> findAllEmailConfig(
            @RequestParam(name = "mvnoId", required = true) Long mvnoId,
            @RequestParam(name = "buId", required = false) Long buId,
            @RequestParam(name = "serviceType", required = true) String serviceType,
            HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        ResponseEntity responseEntity = null;
        try {
            /** To Validate Token */
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            /** Call Find All Email Configuration Method */
            List<EmailConfig> emailConfigList = emailConfigService.findAllEmailConfig(mvnoId, buId, serviceType);
            if (!emailConfigList.isEmpty()) {
                emailConfigList.stream()
                        .filter(emailConfig -> emailConfig.getMvnoId() != null)
                        .forEach(emailConfig -> mvnoRepository.findById(emailConfig.getMvnoId())
                                .ifPresent(mvno -> emailConfig.setMvnoName(mvno.getName())));
            }
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(EMAILCONFIG_LIST, emailConfigList);
            snmpCounters.incrementGetEmailConfigListSuccess();
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to fetch Email config details," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  fetch Email config details ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            customException.printStackTrace();
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  fetch Email config details ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            authException.printStackTrace();
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetEmailConfigListFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  fetch Email config details " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            snmpCounters.incrementTotalGetEmailConfigList();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetEmailConfigList();
        }
    }

    /**
     * API: Add/ Create/ Save Email Configuration
     *
     * @param emailConfigDto
     * @param mvnoId
     * @param buId
     * @param request
     * @return
     */
    @ApiOperation(value = "Save Transaction :: Email Configuration - POST Method", notes = "For saving the source master record")
    @PostMapping("/addEmailConfig")
    public ResponseEntity<Map<String, Object>> addEmailConfig(@RequestBody EmailConfigDto emailConfigDto,
                                                              @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                              @RequestParam(name = "buId", required = false) Long buId,
                                                              HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        try {
            /** To Validate Token*/
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            /** Call Validate Email Configuration Data Method */
            emailConfigService.validateEmailConfigData(emailConfigDto, mvnoId);
            /** Call To Validate SMTP Configuration Data Method */
            if (emailConfigDto.getServiceType().equalsIgnoreCase(CommonConstants.SERVICE_TYPE_IWF)) {
                emailConfigService.validateSMTPAuthentication(emailConfigDto.isSmtpAuth(), emailConfigDto.getAuthType(), emailConfigDto.getHostServer(), emailConfigDto.getPort(), emailConfigDto.getUserName(), emailConfigDto.getPassword());
            }
            /** Call Add Email Configuration Method */
            EmailConfig emailConfigVo = emailConfigService.addEmailConfig(emailConfigDto, mvnoId, buId);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request for Email Config Creation  successfully," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            response.put(EMAIL_CONFIG, emailConfigVo);
            response.put(NotificationConstants.MESSAGE, NotificationConstants.API_Response_Message.CREATED_SUCCESSFULLY);
            return apiResponseController.apiResponse(NotificationConstants.SUCCESS, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  Create Email config ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            customException.printStackTrace();
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  Create Email config," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            authException.printStackTrace();
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementCreateEmailConfigFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  Create Email config," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalCreateEmailConfig();
        }
    }

    @ApiOperation(value = "Update Transaction :: Email Configuration - PUT Method", notes = "For updating the source master record")
    @PutMapping("/updateEmailConfig")
    public ResponseEntity<Map<String, Object>> updateEmailConfig(@RequestBody UpdateEmailConfigDto emailConfigDto,
                                                                 @RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                                 @RequestParam(name = "buId", required = false) Long buId,
                                                                 HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            /** To Validate Token */
            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
                apiDataValidator.validateApiData(usermvnoid, mvnoId, emailConfigDto.getUserName(), NotificationConstants.EmailConfig_TABLENAME, NotificationConstants.EmailConfig_UPDATEKEY);
            }
            EmailConfig emailConfig = emailConfigRepository.findById(emailConfigDto.getEmailConfigId()).orElse(null);
            /** Call Validate Email Configuration Data On Update Method */
            emailConfigService.validateEmailConfigDataOnUpdate(emailConfigDto, mvnoId);
            /** Call To Validate SMTP Configuration Data Method */
            if (emailConfigDto.getServiceType().equalsIgnoreCase(CommonConstants.SERVICE_TYPE_IWF)) {
                emailConfigService.validateSMTPAuthentication(emailConfigDto.isSmtpAuth(), emailConfigDto.getAuthType(), emailConfigDto.getHostServer(), emailConfigDto.getPort(), emailConfigDto.getUserName(), emailConfigDto.getPassword());
            }
            String difference = updateDiffFinder.getUpdatedDiff(emailConfig, new EmailConfig(emailConfigDto, mvnoId));
            /** Call Update Email Configuration Method */
            EmailConfig emailConfigVo = emailConfigService.updateEmailConfig(emailConfigDto, mvnoId, buId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(EMAIL_CONFIG, emailConfigVo);
            response.put(NotificationConstants.MESSAGE, NotificationConstants.API_Response_Message.UPDATED_SUCCESSFULLY);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request for Update Email Configuration is successfull whith data " + difference + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Email Configuration failed, for configId : " + emailConfigDto.getUserName() + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            apiResponseController.buildErrorMessageForResponse(response, customException);
            customException.printStackTrace();
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Email Configuration failed, for configId : " + emailConfigDto.getUserName() + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            authException.printStackTrace();
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementUpdateEmailConfigFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Email Configuration failed, for configId : " + emailConfigDto.getUserName() + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            snmpCounters.incrementUpdateEmailConfigFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalUpdateEmailConfig();
        }
    }

    @ApiOperation(value = "Update Transaction :: Email Configuration - PUT Method", notes = "For change password the source master record")
    @PutMapping("/changePassword")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody PasswordDto passwordDto, @RequestParam("mvnoid") Long mvnoId,
                                                              HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            /** To Validate Token */
            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
                apiDataValidator.validateApiData(usermvnoid, mvnoId, passwordDto.getConfirmNewPassword(), NotificationConstants.EmailConfig_TABLENAME, NotificationConstants.EmailConfig_PRIMARYKEY);
            }
            /** Call Change Password Method */
            emailConfigService.changePassword(passwordDto);
            response.put(NotificationConstants.MESSAGE, "Password has been updated successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request for Change Password is successfull," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            snmpCounters.incrementUpdateEmailConfigPasswordSuccess();
            return apiResponseController.apiResponse(NotificationConstants.SUCCESS, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update Password for Username " + passwordDto.getUserName() != null ? passwordDto.getUserName() : null + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            customException.printStackTrace();
            snmpCounters.incrementUpdateEmailConfigPasswordFailure();
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update Password for Username " + passwordDto.getUserName() != null ? passwordDto.getUserName() : null + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            authException.printStackTrace();
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update Password for Username " + passwordDto.getUserName() != null ? passwordDto.getUserName() : null + " " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return apiResponseController.apiResponse(NotificationConstants.FAIL, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalUpdateEmailConfigPassword();
        }
    }

    /**
     * API: Find Email Configuration By Id
     *
     * @param emailConfigId
     * @param mvnoId
     * @param request
     * @return
     */
    @ApiOperation(value = "Get Transaction :: Email Configuration - GET Method", notes = "For find email configuration by id the source master record")
    @GetMapping("/findEmailConfigById")
    public ResponseEntity<Map<String, Object>> findEmailById(@RequestParam(name = "emailConfigId", required = true) Long emailConfigId, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            /** To Validate Token */
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            /** Call Find Email Configuration By Id Method*/
            EmailConfig emailConfig = emailConfigService.findEmailConfigById(emailConfigId, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(EMAIL_CONFIG, emailConfig);
            snmpCounters.incrementGetEmailByIdSuccess();
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to fetch Email config details successfully," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch email Configuration" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            customException.printStackTrace();
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch email Configuration" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            authException.printStackTrace();
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetEmailByIdFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch email Configuration" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TRACE_ID);
            snmpCounters.incrementTotalGetEmailById();
        }
    }

    /**
     * IWF Notification Email Config Delete API
     *
     * @return
     * @paramid
     */
    @ApiOperation(value = "Delete Transaction :: Email Configuration - DELETE Method", notes = "For delete email configuration by id the source master record")
    @DeleteMapping("/removeEmailConfiguration")
    public ResponseEntity<Map<String, Object>> removeEmailConfiguration(@RequestParam(name = "emailConfigId", required = true) Long emailConfigId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            /** To Validate Token */
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            /** Call Find Email Configuration By Id Method*/
            emailConfigService.removeEmailConfigById(emailConfigId);
            Integer responseCode = NotificationConstants.SUCCESS;
            snmpCounters.incrementGetEmailByIdSuccess();
            response.put(NotificationConstants.MESSAGE, NotificationConstants.API_Response_Message.DELETED_SUCCESSFULLY);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to delete Email config details is successfull," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete email Configuration" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete email Configuration" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetEmailByIdFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Delete email Configuration" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetEmailById();
        }
    }

    /**
     * IWF Notification Email Config Filter by Name API
     *
     * @param criteriaMap
     * @return
     */
    @ApiOperation(value = "Filter Transaction :: SourceMaster - POST Method", notes = "Searching for some specific source master records using wildcard expression with pagination")
    @PostMapping("/filterByName")
    public ResponseEntity<Map<String, Object>> filterSources(@RequestBody Map<String, Object> criteriaMap, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            /** To Validate Token */
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            /** Call Filter Email Configuration By Name Method */
            Page<EmailConfigDto> emailConfigDtos = emailConfigService.filterEmailConfigByName(criteriaMap);
            response.put(EMAIL_CONFIG, emailConfigDtos);
            Integer responseCode = NotificationConstants.SUCCESS;
            snmpCounters.incrementGetEmailByIdSuccess();
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Email config details fatching successfully, for config : " + criteriaMap + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch email Configuration" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch email Configuration" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetEmailByIdFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch email Configuration " + criteriaMap + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetEmailById();
        }
    }

    /**
     * API: Get Email Configuration With Pagination
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
    @ApiOperation(value = "Fetch Transaction :: Email Configuration - GET Method",
            notes = "For fetching the all email configuration with pagination")
    @GetMapping("/getEmailConfigList")
    public ResponseEntity<Object> getEmailConfigurationWithPagination(
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
            /** Call Get Email Configuration With Pagination Method */
            Page<EmailConfigDto> emailConfigDtos = emailConfigService.getEmailConfigWithPagination(page, size, mvnoId, buId, serviceType);
            if (!emailConfigDtos.isEmpty()) {
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Email Config fetched successfully," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                return ResponseHandler.generateResponse("Total Sources Found: " + emailConfigDtos.getSize(), HttpStatus.OK, emailConfigDtos);
            } else {
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " No Email Config found," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.NO_CONTENT.value());
                return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.NO_RECORDS_FOUND, HttpStatus.OK, emailConfigDtos);
            }
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable fetch email Config " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NOT_FOUND, null);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Fetch Transaction :: EmailConfig Search Filter - POST Method", notes = "For fetching the active sms by filters with pagination")
    @PostMapping("/get-filtered-emailconfig")
    public ResponseEntity<Object> getFilteredEmailconfig(
            @RequestBody PaginationRequestDTO requestDTO,
            @RequestParam(name = "mvnoId") Long mvnoId,
            @RequestParam(name = "serviceType") String serviceType,
            HttpServletRequest request)
            throws IOException {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());

        try {
            log.debug("Fetching EmailConfig...");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " EmailConfig fetched successfully," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            if (emailConfigService.validation(requestDTO)) {
                Page<EmailConfigDto> dtos = emailConfigService.searchEmailConfig(requestDTO, mvnoId, serviceType);
                if (dtos == null || dtos.isEmpty()) {
                    log.debug("No Records with Given Field:{}");
                    return ResponseEntity.ok(
                            Response.builder()
                                    .responseTime(LocalDateTime.now())
                                    .status(HttpStatus.NO_CONTENT)
                                    .statusCode(HttpStatus.NO_CONTENT.value())
                                    .message("No records found")
                                    .data(null)
                                    .build()
                    );
                } else {
                    log.debug(" Total EmailConfig found:{}");
                    Map<String, Object> map = new HashMap<String, Object>();
                    if (dtos != null) {
                        map.put("EmailConfig", dtos);
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
                }
            } else {
                return ResponseEntity.ok(
                        Response.builder()
                                .responseTime(LocalDateTime.now())
                                .status(HttpStatus.RESET_CONTENT)
                                .statusCode(HttpStatus.RESET_CONTENT.value())
                                .method("EmailConfigController.getEmailConfig")
                                .executionMessage("Implemented business logic of service class method")
                                .message("Please enter proper value")
                                .data(null)
                                .build()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable fetch EmailConfig " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
//           return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.EXPECTATION_FAILED, null);
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
