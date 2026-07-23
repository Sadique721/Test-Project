package com.savbill.notification.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.notification.Response.Response;
import com.savbill.notification.savbilliwfnotification.response.ResponseHandler;
import com.savbill.notification.savbilliwfnotification.service.impl.IwfEmailServiceImpl;
import com.savbill.notification.entity.Email;
import com.savbill.notification.entity.NotificationAudit;
import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.*;
import com.savbill.notification.helper.*;
import com.savbill.notification.services.EmailService;
import com.savbill.notification.services.impl.CustomerServiceImpl;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Api(value = "Email Management", description = "REST APIs related to Email Entity!!!!", tags = "Email")
@RestController
@Slf4j
@RequestMapping("/SavbillNotification")
public class EmailController {
    private static final String EMAIL_LIST = "emailList";
    private static final String EMAIL = "email";
    //final Logger log = Logger.getLogger(EmailController.class);

    private final SNMPCounters snmpCounters = new SNMPCounters();

    @Autowired
    EmailService emailService;
    @Autowired
    APIResponseController apiResponseController;

    @Autowired
    ApiDataValidator apiDataValidator;

    @Autowired
    TokenDataExtractor tokenDataExtractor;
    @Autowired
    Tracer tracer;
    @Autowired
    private CustomerServiceImpl customerService;
    /**
     * IWF Service/ Repository Autowired
     */
    @Autowired
    private IwfEmailServiceImpl iwfEmailService;

    @ApiOperation(value = "Send email")
    @PostMapping("/sendEmail")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestParam(name = "emailid", required = true) Long emailId, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            String encodeToken = request.getHeader("Authorization");
            Integer mvnoIdFromToken = tokenDataExtractor.getMvnoId(encodeToken).intValue();
            List<Long> buIds = tokenDataExtractor.getBUId(encodeToken);
            emailService.reSendEmail(emailId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(NotificationConstants.MESSAGE, "Email has been Sent successfully.");
            snmpCounters.incrementSendEmailSuccess();
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to send Email to " + emailId + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            customException.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Send Email to Email Id " + emailId + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            authException.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Send Email to Email Id " + emailId + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            e.printStackTrace();
            snmpCounters.incrementSendEmailFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Send Email to Email Id " + emailId + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalSendEmail();
        }
    }

    @ApiOperation(value = "Get list of emails in the system")
    @GetMapping("/emails")
    public ResponseEntity<Map<String, Object>> findAllEmails(@ModelAttribute PaginationDTO paginationDTO, @RequestParam(name = "eventId", required = false) Long eventId, @RequestParam(name = "status", required = false) String status, @RequestParam(name = "emailAddress", required = false) String emailAddress, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        ResponseEntity responseEntity = null;
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            String token = request.getHeader("Authorization");

            Long mvnoId = tokenDataExtractor.getMvnoId(request.getHeader("Authorization"));

            List<Long> buIdList = tokenDataExtractor.getBUId(request.getHeader("Authorization"));

            PageableResponse<EmailDataDTO> page = emailService.findAllEmails(eventId, status, emailAddress, mvnoId, paginationDTO, buIdList);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(page.getData())) {
                responseCode = HttpStatus.OK.value();
                if (!Objects.isNull(eventId) && !StringUtils.isBlank(emailAddress) && !StringUtils.isBlank(status)) {
                    response.put(NotificationConstants.MESSAGE, "No record found with the given event type , email address : " + emailAddress + " and status : " + status);
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to Fetch email," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                } else if (!Objects.isNull(eventId) && !StringUtils.isBlank(emailAddress) && StringUtils.isBlank(status)) {
                    response.put(NotificationConstants.MESSAGE, "No record found with the given event type  and  email address : " + emailAddress);
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to Fetch email," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                } else if (!Objects.isNull(eventId) && StringUtils.isBlank(emailAddress) && !StringUtils.isBlank(status)) {
                    response.put(NotificationConstants.MESSAGE, "No record found with the given event type and status : " + status);
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to Fetch email," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                } else if (Objects.isNull(eventId) && !StringUtils.isBlank(emailAddress) && !StringUtils.isBlank(status)) {
                    response.put(NotificationConstants.MESSAGE, "No record found with email address : " + emailAddress + " and status : " + status);
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to Fetch email," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                } else {
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to Fetch email," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                    response.put(NotificationConstants.MESSAGE, "No Records Found.");
                }
            } else {
                responseCode = NotificationConstants.SUCCESS;
                response.put(EMAIL_LIST, page);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Request to Fetch email," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
                snmpCounters.incrementGetEmailListSuccess();
            }
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            customException.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Email" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            authException.printStackTrace();
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Email" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            e.printStackTrace();
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetEmailListFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Email" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetEmailList();
        }
    }

    @ApiOperation(value = "Get email based on the given email id")
    @GetMapping("/findEmailById")
    public ResponseEntity<Map<String, Object>> findEmailById(@RequestParam(name = "emailid", required = true) Long emailId, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            Email emailVo = emailService.findEmailById(emailId, mvnoId, false);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(EMAIL, emailVo);
            snmpCounters.incrementGetEmailByIdSuccess();
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Email fetch successfully, for email id: " + emailId + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Email for Id " + emailId + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Email for Id " + emailId + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetEmailByIdFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Email for Id " + emailId + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetEmailById();
        }
    }

    @ApiOperation(value = "Get list of emails based on the given source name")
    @GetMapping("/findEmailBySourceName")
    public ResponseEntity<Map<String, Object>> findEmailBySourceName(@RequestParam(name = "eventId", required = false) Long eventId, @RequestParam(name = "status", required = false) String status, @RequestParam(name = "emailAddress", required = false) String emailAddress, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            List<EmailDataDTO> emailList = emailService.findEmailBySourceName(eventId, status, emailAddress, mvnoId);
            Integer Response = 0;
            if (emailList.isEmpty()) {
                Response = NotificationConstants.NULL_VALUE;
                response.put(NotificationConstants.MESSAGE, "No Records Found!");
                response.put("emailList", new ArrayList<>());
                return apiResponseController.apiResponse(Response, response);
            }
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(EMAIL_LIST, emailList);
            snmpCounters.incrementGetEmailBySourceNameSuccess();
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Email fetch successfully, for email-id: " + emailAddress + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Email for email-id " + emailAddress + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Email for email-id " + emailAddress + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetEmailBySourceNameFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Fetch Email for email-id " + emailAddress + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalGetEmailBySourceName();
        }
    }

    @ApiOperation(value = "Add new email")
    @PostMapping("/addEmail")
    public ResponseEntity<Map<String, Object>> addEmail(@RequestBody EmailDto email, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {

            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            email.setCreatedBy(tokenDataExtractor.getUserName(request.getHeader("Authorization")));
            Email emailVo = emailService.saveEmail(email, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(EMAIL, emailVo);
            response.put(NotificationConstants.MESSAGE, "Email has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Email created successfully, new email :  " + email.getEmailAddress() + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            snmpCounters.incrementCreateEmailSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create New Email " + email.getEmailAddress() + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            authException.printStackTrace();
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create New Email " + email.getEmailAddress() + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Create New Email " + email.getEmailAddress() + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementCreateEmailFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalCreateEmail();
        }
    }

    @ApiOperation(value = "Update existing email data")
    @PutMapping("/updateEmail")
    public ResponseEntity<Map<String, Object>> updateEmail(@RequestBody UpdateEmailDto updateEmailDto, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        try {
            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
                apiDataValidator.validateApiData(usermvnoid, mvnoId, updateEmailDto.getEmailId(), NotificationConstants.Email_TABLENAME, NotificationConstants.Email_PRIMARYKEY);
            }
            Long staffmvnoId = tokenDataExtractor.getMvnoId(request.getHeader("Authorization"));
            List<Long> buIdList = tokenDataExtractor.getBUId(request.getHeader("Authorization"));
            if (buIdList.size() > 1) {
                throw new CustomException("You are not allowed to perform this action, Please contact your system administrator", NotificationConstants.EXPECTATION_FAILED);
            } else if (buIdList.size() == 1) {
                Email emailVo = emailService.updateEmail(updateEmailDto, staffmvnoId, buIdList.get(0), request);
                Integer responseCode = NotificationConstants.SUCCESS;
                response.put(EMAIL, emailVo);
                response.put(NotificationConstants.MESSAGE, "Email has been updated successfully.");
                snmpCounters.incrementUpdateEmailSuccess();
                return apiResponseController.apiResponse(responseCode, response);
            } else {
                updateEmailDto.setLastModifiedBy(tokenDataExtractor.getUserName(request.getHeader("Authorization")));
                Email emailVo = emailService.updateEmail(updateEmailDto, staffmvnoId, null, request);
                Integer responseCode = NotificationConstants.SUCCESS;
                response.put(EMAIL, emailVo);
                response.put(NotificationConstants.MESSAGE, "Email has been updated successfully.");
                snmpCounters.incrementUpdateEmailSuccess();
                return apiResponseController.apiResponse(responseCode, response);
            }
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update Email  with Id " + updateEmailDto.getEmailAddress() + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update Email  with Id " + updateEmailDto.getEmailAddress() + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementUpdateEmailFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Update Email  with Id " + updateEmailDto.getEmailAddress() + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalUpdateEmail();
        }
    }

    @ApiOperation(value = "Delete existing email data based on the given email id")
    @DeleteMapping("/deleteEmail")
    public ResponseEntity<Map<String, Object>> deleteEmail(@RequestParam(name = "emailid", required = true) Long emailId, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_DELETE);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
                apiDataValidator.validateApiData(usermvnoid, mvnoId, emailId, NotificationConstants.Email_TABLENAME, NotificationConstants.Email_PRIMARYKEY);
            }
            emailService.deleteEmailById(emailId, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(NotificationConstants.MESSAGE, "Email has been deleted successfully.");
            snmpCounters.incrementDeleteEmailSuccess();
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Email with ID: " + emailId + " is deleted Successfully," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            customException.printStackTrace();
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to delete Email  with Id " + emailId + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            authException.printStackTrace();
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to delete Email  with Id " + emailId + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementDeleteEmailFailure();
            e.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to delete Email  with Id " + emailId + "" + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalDeleteEmail();
        }
    }

    @ApiOperation(value = "Send email with Attachment")
    @PostMapping("/sendEmailwithAttachment")
    public ResponseEntity<Map<String, Object>> sendEmailwithAttachment(@RequestParam(name = "emailid", required = true) Long emailId, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        ResponseEntity<Map<String, Object>> responseEntity = null;
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            emailService.sendEmailwithAttachments(emailId, mvnoId);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put(NotificationConstants.MESSAGE, "Email has been Sent successfully.");
            snmpCounters.incrementSendEmailSuccess();
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Attachment for  Email with ID: " + emailId + " is successfull ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Send Email With Attachment " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Send Email With Attachment " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementSendEmailFailure();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Send Email With Attachment " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            snmpCounters.incrementTotalSendEmail();
        }
    }

    public void sendEMailWithAttach(String attachment) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                FileSystemResource file = new FileSystemResource(new File(attachment));
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.addAttachment("test.jpg", file);
            }
        };
    }

    @ApiOperation(value = "Get list of  all dunning history")
    @PostMapping("/findByCustomerUsername")
    //@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<?> findAllByPartnerOrCustomer(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        try {
            Page<NotificationAudit> getAllCustomerNotificationHistory = customerService.findAllCustomerNotificationHistory(requestDTO);
            Integer responseCode = NotificationConstants.SUCCESS;
            response.put("customerNotificationHistory", getAllCustomerNotificationHistory);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetching Dunning History is successfully," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch Dunning History " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            Integer responseCode = NotificationConstants.FAIL;
            response.put(NotificationConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TYPE);
        }
    }

    /**
     * IWF Notification Email Get Source Master API
     *
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "Fetch Audits", notes = "For retrieving the all email audits records with pagination")
    @GetMapping("/email/get-email-audits")
    public ResponseEntity<Object> getSourceMastersWithPagination(@RequestParam @Valid int page,
                                                                 @RequestParam @Valid int pageSize,
                                                                 @RequestParam @Valid Long mvnoId,
                                                                 HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Page<Email> emails = iwfEmailService.getEmailAudits(page, pageSize, mvnoId);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetching all email audits," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return ResponseHandler.generateResponse("Total Sources Found: " + emails.getSize(), HttpStatus.OK, emails);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch email audit records " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            e.printStackTrace();
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NOT_FOUND, null);
        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TYPE);
        }
    }


    @PostMapping("/search/emails")
    public ResponseEntity<Object> searchEmailAudit(@RequestParam(name = "mvnoId", required = true) Long mvnoId,
                                                   @RequestParam(name = "serviceType", required = true) String serviceType,
                                                   @RequestBody PaginationRequestDTO dto,
                                                   HttpServletRequest request) throws IOException {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));
        MDC.put(NotificationConstants.SPAN_ID, traceContext.spanIdString());
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);

        try {
            log.debug("Ferching Email...");
            if (emailService.validation(dto)) {
                Page<EmailDto> respDtos = emailService.searchEmailAudit(dto, mvnoId, serviceType);
//            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Fetching all email audits," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
                //    return ResponseHandler.generateResponse("Data fetched Succefully",HttpStatus.OK,respDtos);
                if (respDtos == null || respDtos.isEmpty()) {
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
                    log.debug(" Total Email found:{}");
                    Map<String, Object> map = new HashMap<String, Object>();
                    if (respDtos != null) {
                        map.put("Email", respDtos);
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
                                .method("EmailController.getEmail")
                                .executionMessage("Implemented business logic of service class method")
                                .message("Please enter proper value")
                                .data(null)
                                .build()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch email audit records " + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
//            return ResponseHandler.generateResponse(e.getMessage(),HttpStatus.NO_CONTENT,"NO RECORD FOUND");
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
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TYPE);
        }
    }
}
