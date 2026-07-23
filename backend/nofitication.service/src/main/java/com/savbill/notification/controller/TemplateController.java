package com.savbill.notification.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.notification.utils.*;
import com.savbill.notification.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.savbill.notification.exceptions.AuthException;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.helper.TemplateDto;
import com.savbill.notification.services.TemplateService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Template Management", description = "REST APIs related to Template Entity!!!!", tags = "Template")
@RestController
@Slf4j
@RequestMapping("/SavbillNotification/Template")
public class TemplateController {
    private static final String TEMPLATE = "template";
    private static final String TEMPLATE_LIST = "templateList";
    //private final Logger log = Logger.getLogger(TemplateController.class);
    @Autowired
    APIResponseController apiResponseController;

    @Autowired
    TemplateService templateService;

    @Autowired
    ApiDataValidator apiDataValidator;

    @Autowired
    TokenDataExtractor tokenDataExtractor;
    @Autowired
    private UpdateDiffFinder updateDiffFinder;
    @Autowired
    private Tracer tracer;


    @GetMapping("/all")
    @ApiOperation(value = "Get list of templates in the system")
    public ResponseEntity<Map<String, Object>> findAll(HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        ResponseEntity responseEntity = null;
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            response.put(TEMPLATE_LIST, templateService.findAll());
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Template details fetch successfully," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(NotificationConstants.SUCCESS, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  fetch Template  details ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  fetch Template  details ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  fetch Template  details ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(NotificationConstants.FAIL, response);
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
        }
    }

    @PostMapping("/save")
    @ApiOperation(value = "Add new template")
    public ResponseEntity<Map<String, Object>> saveTemplate(@RequestBody TemplateDto templateDto, HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        try {
            if ((request.getHeader("requestFrom") == null)) {
                tokenDataExtractor.getUsermvnoId(request);
            }
            response.put(TEMPLATE, templateService.saveTemplate(templateDto));
            response.put(NotificationConstants.MESSAGE, "Template has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Template created successfully  with name" + templateDto.getTemplateName() + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(NotificationConstants.SUCCESS, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            customException.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Template creation failed, for template name:  " + templateDto.getTemplateName() + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            authException.printStackTrace();
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Template creation failed, for template name:  " + templateDto.getTemplateName() + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            e.printStackTrace();
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Template creation failed, for template name:  " + templateDto.getTemplateName() + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(NotificationConstants.FAIL, response);
        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TYPE);
        }
    }

    @PutMapping("/update")
    @ApiOperation(value = "Update existing template")
    public ResponseEntity<Map<String, Object>> updateTemplate(@RequestBody List<TemplateDto> templateDtos, HttpServletRequest request, @RequestParam(name = "mvnoid", required = false) Long mvnoId) throws IOException {
        Map<String, Object> response = new HashMap<>();
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        try {
            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
            }
            response.put(TEMPLATE, templateService.udpateTemplate(templateDtos,request));
            response.put(NotificationConstants.MESSAGE, "Template has been updated successfully.");
            return apiResponseController.apiResponse(NotificationConstants.SUCCESS, response);
        } catch (CustomException customException) {
            customException.printStackTrace();
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Unable to Update Template, for template name:  " + templateDtos.get(0).getTemplateName() != null ? templateDtos.get(0).getTemplateName() : null + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            authException.printStackTrace();
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Unable to Update Template, for template name:  " + templateDtos.get(0).getTemplateName() != null ? templateDtos.get(0).getTemplateName() : null + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            e.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Unable to Update Template, for template name:  " + templateDtos.get(0).getTemplateName() != null ? templateDtos.get(0).getTemplateName() : null + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(NotificationConstants.FAIL, response);
        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TYPE);
        }
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "Delete existing template")
    public ResponseEntity<Map<String, Object>> deleteTemplate(
            @RequestParam(name = "templateId", required = true) Long templateId, HttpServletRequest request, @RequestParam(name = "mvnoid", required = false) Long mvnoId) throws IOException {
        Map<String, Object> response = new HashMap<>();
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        try {
            if ((request.getHeader("requestFrom") == null)) {
                long usermvnoid = tokenDataExtractor.getUsermvnoId(request);
                apiDataValidator.validateApiData(usermvnoid, mvnoId, templateId, NotificationConstants.Template_TABLENAME, NotificationConstants.Template_PRIMARYKEY);
            }
            templateService.deleteTemplate(templateId);
            response.put(NotificationConstants.MESSAGE, "Template has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Template is deleted Successfully  with id" + templateId + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(NotificationConstants.SUCCESS, response);
        } catch (CustomException customException) {
            apiResponseController.buildErrorMessageForResponse(response, customException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Unable to Delete Template, for template Id:  " + templateId + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + customException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(customException.getStatuscode(), response);
        } catch (AuthException authException) {
            apiResponseController.buildErrorMessageForResponse(response, authException);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Unable to Delete Template, for template Id:  " + templateId + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + authException.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(authException.getStatuscode(), response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Unable to Delete Template, for template Id:  " + templateId + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(NotificationConstants.FAIL, response);
        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TYPE);
        }
    }

    @GetMapping("/allbyMvnoIdandBuid")
    @ApiOperation(value = "Get list of templates in the system")

    public ResponseEntity<Map<String, Object>> findAllByMvnoIdAndBuId(HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        ResponseEntity responseEntity = null;
        try {
            Long usermvnoid = tokenDataExtractor.getMvnoId(request.getHeader("Authorization"));
            List<Long> buidlist = tokenDataExtractor.getBUId(request.getHeader("Authorization"));
            response.put(TEMPLATE_LIST, templateService.findAllByMvnoIdAndBuId(usermvnoid, buidlist));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Template List," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS);
            return apiResponseController.apiResponse(NotificationConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            e.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch Template details :  ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(NotificationConstants.FAIL, response);
        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TYPE);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTemplateById(@PathVariable("id") Long id,
                                                @RequestBody TemplateDto templateDto,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> apiResponse = new HashMap<>();
        try {
            apiResponse.put(TEMPLATE, templateService.updateTemplateById(templateDto, request, id));
            apiResponse.put(NotificationConstants.MESSAGE, "Template has been updated successfully.");
            return apiResponseController.apiResponse(NotificationConstants.SUCCESS, apiResponse);
        } catch (CustomException exception) {
            exception.printStackTrace();
            apiResponseController.buildErrorMessageForResponse(apiResponse, exception);
            exception.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Unable to Update template with id :"+id+" ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(exception.getStatuscode(), apiResponse);

        } catch (Exception exception) {
            exception.printStackTrace();
            apiResponseController.buildErrorMessageForResponse(apiResponse, exception);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Unable to Update template with id :"+id+" ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(NotificationConstants.INTERNAL_SERVER_ERROR, apiResponse);
        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TYPE);
        }
    }


    @GetMapping("/search")
    @ApiOperation(value = "Get list of templates in the system")
    public ResponseEntity<?> searchByName(@RequestParam(name = "templateName", required = false) String templateName, HttpServletRequest request) throws IOException {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(NotificationConstants.TRACE_ID, request.getHeader(NotificationConstants.TRACE_ID));

        MDC.put(NotificationConstants.SPAN_ID,traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {
            Long usermvnoid = tokenDataExtractor.getMvnoId(request.getHeader("Authorization"));
            List<Long> buidlist = tokenDataExtractor.getBUId(request.getHeader("Authorization"));
            if (templateName == null) {
                response.put(TEMPLATE_LIST, templateService.findAllByMvnoIdAndBuId(usermvnoid, buidlist));
                response.put(NotificationConstants.MESSAGE, "Fetching Template List.");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetching Template By name " + templateName != null ? templateName : null + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
                return apiResponseController.apiResponse(NotificationConstants.SUCCESS, response);
            } else {
                response.put(TEMPLATE_LIST, templateService.findAllByMvnoIdAndBuIdAndTemplatename(usermvnoid, buidlist, templateName));
                response.put(NotificationConstants.MESSAGE, "Fetching Template List.");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetching Template By name " + templateName != null ? templateName : null + " ," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
                return apiResponseController.apiResponse(NotificationConstants.SUCCESS, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Unable to Search Template, With template name:  " + templateName != null ? templateName : null + "," + LogConstants.REQUEST_BY + tokenDataExtractor.getUserName(request.getHeader("Authorization")) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(NotificationConstants.INTERNAL_SERVER_ERROR, response);

        } finally {
            MDC.remove(NotificationConstants.TRACE_ID);
            MDC.remove(NotificationConstants.SPAN_ID);
            MDC.remove(NotificationConstants.TYPE);
        }

    }
}
