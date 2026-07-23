package com.savbill.notification.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.notification.Response.Response;
import com.savbill.notification.savbilliwfnotification.dto.EventTemplateBindingDTO;
import com.savbill.notification.savbilliwfnotification.response.ResponseHandler;
import com.savbill.notification.savbilliwfnotification.service.IwfEventTempBindService;
import com.savbill.notification.helper.PaginationRequestDTO;
import com.savbill.notification.helper.searchDTO.responseDtos.EventTempBindSearchDTO;
import com.savbill.notification.helper.searchDTO.responseDtos.EventTemplateDTO;
import com.savbill.notification.utils.LogConstants;
import com.savbill.notification.utils.LogInfo;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.TokenDataExtractor;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
@RequestMapping("/SavbillNotification")
@Slf4j
public class EventTempBindController {

    public final String ENTITYNAME = "EventTemplateBind ";
    @Autowired
    IwfEventTempBindService iwfEventTempBindService;
    @Autowired
    TokenDataExtractor extractor;
    @Autowired
    Tracer tracer;

    /**
     * IWF Notification Event Template Bind Save API
     *
     * @param eventTemplateBindingDTO
     * @return
     */
    @ApiOperation(value = "Save Transaction :: Event Template Binding - POST Method", notes = "For save the event template binding")
    @PostMapping(value = "/event_temp_bind/save")
    public ResponseEntity<Object> saveEventTempBind(@RequestBody EventTemplateBindingDTO eventTemplateBindingDTO, HttpServletRequest request) throws IOException {
        String userName = extractor.getUserName(request.getHeader(LogConstants.LogConstant.AUTHORIZATION));
        LogInfo logInfo = LogInfo.extractLogInfo(request, LogConstants.LogConstant.CREATE_TYPE, userName);
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = traceContext.traceIdString();
        String spanId = traceContext.spanIdString();
        MDC.put(LogConstants.LogConstant.TRACE_ID, traceId);
        MDC.put(LogConstants.LogConstant.SPAN_ID, spanId);
        try {
            /** Call Validate Event Template Bind Method */
            iwfEventTempBindService.validateEventTempBind(eventTemplateBindingDTO);
            /** Call Save Event Template Bind Method */
            eventTemplateBindingDTO = iwfEventTempBindService.saveEventTempBind(eventTemplateBindingDTO, request);
            log.info(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.SUCCESS_STATUS,
                    HttpStatus.CREATED.value(),
                    ENTITYNAME + NotificationConstants.API_Response_Message.CREATED_SUCCESSFULLY
            );
            return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.CREATED_SUCCESSFULLY, HttpStatus.CREATED, eventTemplateBindingDTO);
        } catch (Exception e) {
            log.error(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NOT_ACCEPTABLE, null);
        } finally {
            MDC.remove(LogConstants.LogConstant.TRACE_ID);
            MDC.remove(LogConstants.LogConstant.SPAN_ID);
        }
    }

    /**
     * IWF Notification Event Template Bind Update API
     *
     * @param eventId
     * @param eventTemplateBindingDTO
     * @return
     */
    @ApiOperation(value = "Update Transaction :: Event Template Binding - PUT Method", notes = "For update the event template binding")
    @PutMapping("/event_temp_bind/update")
    public ResponseEntity<Object> updateEventTempBind(@RequestParam(name = "eventid", required = true) Long eventId, @RequestBody EventTemplateBindingDTO eventTemplateBindingDTO, HttpServletRequest request) throws IOException {
        String userName = extractor.getUserName(request.getHeader(LogConstants.LogConstant.AUTHORIZATION));
        LogInfo logInfo = LogInfo.extractLogInfo(request, LogConstants.LogConstant.UPDATE_TYPE, userName);
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = traceContext.traceIdString();
        String spanId = traceContext.spanIdString();
        MDC.put(LogConstants.LogConstant.TRACE_ID, traceId);
        MDC.put(LogConstants.LogConstant.SPAN_ID, spanId);
        try {
            /** Call Validate Event Template Bind Method */
            iwfEventTempBindService.validateEventTempBind(eventTemplateBindingDTO);
            /** Call Update Event Template Bind Method */
            eventTemplateBindingDTO = iwfEventTempBindService.updateEventTempBind(eventId, eventTemplateBindingDTO, request);
            log.info(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.SUCCESS_STATUS,
                    HttpStatus.OK.value(),
                    ENTITYNAME + NotificationConstants.API_Response_Message.UPDATED_SUCCESSFULLY
            );
            return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.UPDATED_SUCCESSFULLY, HttpStatus.OK, eventTemplateBindingDTO);
        } catch (Exception e) {
            log.error(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NOT_ACCEPTABLE, null);
        } finally {
            MDC.remove(LogConstants.LogConstant.TRACE_ID);
            MDC.remove(LogConstants.LogConstant.SPAN_ID);
        }
    }

    /**
     * IWF Notification Event Template Bind Remove/ Delete API
     *
     * @param eventId
     * @return
     */
    @ApiOperation(value = "Remove Transaction :: Event Template Binding - DELETE Method", notes = "For removing the event template binding")
    @DeleteMapping("/event_temp_bind/delete")
    public ResponseEntity<Object> deleteEventTempBind(@RequestParam(name = "eventid", required = true) Long eventId, HttpServletRequest request) throws IOException {
        String userName = extractor.getUserName(request.getHeader(LogConstants.LogConstant.AUTHORIZATION));
        LogInfo logInfo = LogInfo.extractLogInfo(request, LogConstants.LogConstant.DELETE_TYPE, userName);
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = traceContext.traceIdString();
        String spanId = traceContext.spanIdString();
        MDC.put(LogConstants.LogConstant.TRACE_ID, traceId);
        MDC.put(LogConstants.LogConstant.SPAN_ID, spanId);
        try {
            /** Call Remove/ Delete Event Template Bind Method */
            iwfEventTempBindService.removeEventTemplateBind(eventId, request);
            log.info(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.SUCCESS_STATUS,
                    HttpStatus.OK.value(),
                    ENTITYNAME + NotificationConstants.API_Response_Message.DELETED_SUCCESSFULLY
            );
            return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.DELETED_SUCCESSFULLY, HttpStatus.OK, eventId);
        } catch (Exception e) {
            log.error(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NOT_ACCEPTABLE, null);
        } finally {
            MDC.remove(LogConstants.LogConstant.TRACE_ID);
            MDC.remove(LogConstants.LogConstant.SPAN_ID);
        }
    }

    /**
     * IWF Notification Event Template Bind Get All with Pagination API
     *
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "Fetch Transaction :: Event Template Binding - GET Method", notes = "For fetching the all event template binding with pagination")
    @GetMapping("/event_temp_bind/getEventTempBindList")
    public ResponseEntity<Object> getEventTempBindWithPagination(@RequestParam(name = "page", defaultValue = "0", required = false) Integer page, @RequestParam(name = "size", defaultValue = "5", required = false) Integer size, HttpServletRequest request) throws IOException {
        String userName = extractor.getUserName(request.getHeader(LogConstants.LogConstant.AUTHORIZATION));
        LogInfo logInfo = LogInfo.extractLogInfo(request, LogConstants.LogConstant.FETCH_TYPE, userName);
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = traceContext.traceIdString();
        String spanId = traceContext.spanIdString();
        MDC.put(LogConstants.LogConstant.TRACE_ID, traceId);
        MDC.put(LogConstants.LogConstant.SPAN_ID, spanId);
        try {
            /** Call Get Event Template Bind With Pagination Method */
            Page<EventTemplateBindingDTO> eventTemplateBindingDTOPage = iwfEventTempBindService.getEventTempBindPagination(page, size, request, false);
            if (!eventTemplateBindingDTOPage.isEmpty()) {
                log.info(logInfo.getLogMessage(),
                        logInfo.getRequestFrom(),
                        userName,
                        logInfo.getIpAddress(),
                        logInfo.getType(),
                        LogConstants.LogConstant.SUCCESS_STATUS,
                        HttpStatus.OK.value(),
                        ENTITYNAME + NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY
                );
                return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY, HttpStatus.OK, eventTemplateBindingDTOPage);
            } else {
                log.error(logInfo.getLogMessage(),
                        logInfo.getRequestFrom(),
                        userName,
                        logInfo.getIpAddress(),
                        logInfo.getType(),
                        LogConstants.LogConstant.FAIL_STATUS,
                        HttpStatus.NO_CONTENT.value(),
                        ENTITYNAME + NotificationConstants.API_Response_Message.NO_RECORDS_FOUND
                );
                return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.NO_RECORDS_FOUND, HttpStatus.OK, eventTemplateBindingDTOPage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.EXPECTATION_FAILED, null);
        } finally {
            MDC.remove(LogConstants.LogConstant.TRACE_ID);
            MDC.remove(LogConstants.LogConstant.SPAN_ID);
        }
    }

    /**
     * IWF Notification Event Template Bind Get By Event Id API
     *
     * @param eventId
     * @return
     */
    @ApiOperation(value = "Fetch Transaction :: Event Template Binding - GET Method", notes = "For fetching the active event template binding by eventId")
    @GetMapping("/event_temp_bind/getBy")
    public ResponseEntity<Object> getEventTempBindByEventId(@RequestParam(name = "eventid", required = true) Long eventId, HttpServletRequest request) throws IOException {
        String userName = extractor.getUserName(request.getHeader(LogConstants.LogConstant.AUTHORIZATION));
        LogInfo logInfo = LogInfo.extractLogInfo(request, LogConstants.LogConstant.FETCH_TYPE, userName);
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = traceContext.traceIdString();
        String spanId = traceContext.spanIdString();
        MDC.put(LogConstants.LogConstant.TRACE_ID, traceId);
        MDC.put(LogConstants.LogConstant.SPAN_ID, spanId);
        try {
            /** Call Get Event Template Bind By Id Method */
            EventTemplateBindingDTO eventTemplateBindingDTO = iwfEventTempBindService.getEventTempBindById(eventId);
            log.info(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.SUCCESS_STATUS,
                    HttpStatus.OK.value(),
                    ENTITYNAME + NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY
            );
            return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY, HttpStatus.OK, eventTemplateBindingDTO);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NOT_FOUND, null);
        } finally {
            MDC.remove(LogConstants.LogConstant.TRACE_ID);
            MDC.remove(LogConstants.LogConstant.SPAN_ID);
        }
    }

    /**
     * IWF Notification Event Template Bind Get By Event Name with Pagination
     *
     * @param page
     * @param size
     * @param criteriaMap
     * @return
     */
    @ApiOperation(value = "Fetch Transaction :: Event Template Binding - POST Method", notes = "For fetching the active event template binding by event name with pagination")
    @PostMapping("/event_temp_bind/filterByName")
    public ResponseEntity<Object> filterSources(
            @RequestBody PaginationRequestDTO requestDTO,
            @RequestParam Long mvnoId,
            HttpServletRequest request)
            throws IOException {
        String userName = extractor.getUserName(request.getHeader(LogConstants.LogConstant.AUTHORIZATION));
        LogInfo logInfo = LogInfo.extractLogInfo(request, LogConstants.LogConstant.FETCH_TYPE, userName);
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = traceContext.traceIdString();
        String spanId = traceContext.spanIdString();
        MDC.put(LogConstants.LogConstant.TRACE_ID, traceId);
        MDC.put(LogConstants.LogConstant.SPAN_ID, spanId);
        try {
            /** Call Filter Event Template Bind By Name Method */
            Page<EventTempBindSearchDTO> eventTempBindSearchDTOS = iwfEventTempBindService.filterEventTempBindByName(requestDTO, mvnoId);
            if (!eventTempBindSearchDTOS.isEmpty()) {
                log.info(logInfo.getLogMessage(),
                        logInfo.getRequestFrom(),
                        userName,
                        logInfo.getIpAddress(),
                        logInfo.getType(),
                        LogConstants.LogConstant.SUCCESS_STATUS,
                        HttpStatus.OK.value(),
                        ENTITYNAME + NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY
                );
                return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY, HttpStatus.OK, eventTempBindSearchDTOS);
            } else {
                log.error(logInfo.getLogMessage(),
                        logInfo.getRequestFrom(),
                        userName,
                        logInfo.getIpAddress(),
                        logInfo.getType(),
                        LogConstants.LogConstant.FAIL_STATUS,
                        HttpStatus.NO_CONTENT.value(),
                        ENTITYNAME + NotificationConstants.API_Response_Message.NO_RECORDS_FOUND
                );
                return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.NO_RECORDS_FOUND, HttpStatus.NO_CONTENT, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.EXPECTATION_FAILED, null);
        } finally {
            MDC.remove(LogConstants.LogConstant.TRACE_ID);
            MDC.remove(LogConstants.LogConstant.SPAN_ID);
        }
    }

    /**
     * Get All Event Template Bind API
     *
     * @return
     */
    @ApiOperation(value = "Fetch Transaction :: Event Template Binding - GET Method", notes = "For fetching the active event template binding without pagination")
    @GetMapping("/event_temp_bind/all")
    public ResponseEntity<Object> getAllActiveEventTempBind(HttpServletRequest request) throws IOException {
        String userName = extractor.getUserName(request.getHeader(LogConstants.LogConstant.AUTHORIZATION));
        LogInfo logInfo = LogInfo.extractLogInfo(request, LogConstants.LogConstant.FETCH_TYPE, userName);
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = traceContext.traceIdString();
        String spanId = traceContext.spanIdString();
        MDC.put(LogConstants.LogConstant.TRACE_ID, traceId);
        MDC.put(LogConstants.LogConstant.SPAN_ID, spanId);
        try {
            /** Call Get All Event Template Bind List Without Pagination Method */
            List<EventTemplateBindingDTO> eventTemplateBindingDTOList = iwfEventTempBindService.getEventTempBindList(request, true);
            if (!eventTemplateBindingDTOList.isEmpty()) {
                log.info(logInfo.getLogMessage(),
                        logInfo.getRequestFrom(),
                        userName,
                        logInfo.getIpAddress(),
                        logInfo.getType(),
                        LogConstants.LogConstant.SUCCESS_STATUS,
                        HttpStatus.OK.value(),
                        ENTITYNAME + NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY
                );
                return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY, HttpStatus.OK, eventTemplateBindingDTOList);
            } else {
                log.error(logInfo.getLogMessage(),
                        logInfo.getRequestFrom(),
                        userName,
                        logInfo.getIpAddress(),
                        logInfo.getType(),
                        LogConstants.LogConstant.FAIL_STATUS,
                        HttpStatus.NO_CONTENT.value(),
                        ENTITYNAME + NotificationConstants.API_Response_Message.NO_RECORDS_FOUND
                );
                return ResponseHandler.generateResponse(NotificationConstants.API_Response_Message.NO_RECORDS_FOUND, HttpStatus.OK, eventTemplateBindingDTOList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.NOT_FOUND, null);
        } finally {
            MDC.remove(LogConstants.LogConstant.TRACE_ID);
            MDC.remove(LogConstants.LogConstant.SPAN_ID);
        }
    }

    @PostMapping("/event_temp_bind/eventsfilter")
    public ResponseEntity<Object> searchByEventFilter(@RequestParam(name = "mvnoId") Long mvnoId, @RequestParam(name = "serviceType") String serviceType, @RequestBody PaginationRequestDTO paginationDTO, HttpServletRequest request) throws IOException {
        String userName = extractor.getUserName(request.getHeader(LogConstants.LogConstant.AUTHORIZATION));
        LogInfo logInfo = LogInfo.extractLogInfo(request, LogConstants.LogConstant.FETCH_TYPE, userName);
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = traceContext.traceIdString();
        String spanId = traceContext.spanIdString();
        MDC.put(LogConstants.LogConstant.TRACE_ID, traceId);
        MDC.put(LogConstants.LogConstant.SPAN_ID, spanId);
        try {
            if (iwfEventTempBindService.validation(paginationDTO)) {
                Page<EventTemplateDTO> eventTemplateBindingDTOPage = iwfEventTempBindService.filterEventTempBind(paginationDTO, mvnoId, serviceType);
                if (eventTemplateBindingDTOPage == null || eventTemplateBindingDTOPage.isEmpty()) {
                    log.error(logInfo.getLogMessage(),
                            logInfo.getRequestFrom(),
                            userName,
                            logInfo.getIpAddress(),
                            logInfo.getType(),
                            LogConstants.LogConstant.FAIL_STATUS,
                            HttpStatus.NO_CONTENT.value(),
                            ENTITYNAME + NotificationConstants.API_Response_Message.NO_RECORDS_FOUND
                    );
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
                    Map<String, Object> map = new HashMap<String, Object>();
                    if (eventTemplateBindingDTOPage != null) {
                        map.put("EventTempBind", eventTemplateBindingDTOPage);
                    }
                    log.info(logInfo.getLogMessage(),
                            logInfo.getRequestFrom(),
                            userName,
                            logInfo.getIpAddress(),
                            logInfo.getType(),
                            LogConstants.LogConstant.SUCCESS_STATUS,
                            HttpStatus.OK.value(),
                            ENTITYNAME + NotificationConstants.API_Response_Message.FETCHED_SUCCESSFULLY
                    );
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
                                .method("EventTempBindController.getEventTempBind")
                                .executionMessage("Implemented business logic of service class method")
                                .message("Please enter proper value")
                                .data(null)
                                .build()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(logInfo.getLogMessage(),
                    logInfo.getRequestFrom(),
                    userName,
                    logInfo.getIpAddress(),
                    logInfo.getType(),
                    LogConstants.LogConstant.FAIL_STATUS,
                    HttpStatus.EXPECTATION_FAILED.value(),
                    e.getMessage()
            );
            Map<String, Object> map = new HashMap<String, Object>();
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
            MDC.remove(LogConstants.LogConstant.TRACE_ID);
            MDC.remove(LogConstants.LogConstant.SPAN_ID);
        }
    }
}

