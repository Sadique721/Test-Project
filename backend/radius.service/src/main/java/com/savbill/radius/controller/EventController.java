package com.savbill.radius.controller;

import java.util.HashMap;
import java.util.Map;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.utils.LogConstants;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.radius.entity.Event;
import com.savbill.radius.helper.EventDto;
import com.savbill.radius.services.EventService;
import com.savbill.radius.utils.RadiusConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;

@Api(value = "Event Management", description = "REST APIs related to Event Entity!!!!", tags = "Event")
@RestController
@RequestMapping("/SavbillRadius/Event")
public class EventController {
    private static final String EVENT = "event";
    private static final String EVENT_LIST = "eventList";
    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    @Autowired
    APIResponseController apiResponseController;
    @Autowired
    EventService eventService;
    @Autowired
    private Tracer tracer;

    @ApiOperation(value = "Get event based on the given event id")
    @GetMapping("/findEventById")
    public ResponseEntity<Map<String, Object>> findEventById(
            @RequestParam(name = "eventid", required = true) Long eventId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Event eventVo = eventService.findEventById(eventId, mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(EVENT, eventVo);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Event has been fetched successfully with id ," +eventId+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching  event by id: "+eventId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/all")
    @ApiOperation(value = "Get list of events in the system")
    public ResponseEntity<Map<String, Object>> findAllEvents(@RequestParam(name = "mvnoId", required = true) Integer mvnoId,HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(EVENT_LIST, eventService.findAllEvents(mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Event has been fetched successfully," + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching  event by name: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            apiResponseController.buildErrorMessageForResponse(response, e);
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping("/save")
    @ApiOperation(value = "Add new event")
    public ResponseEntity<Map<String, Object>> saveEvent(
            @RequestBody EventDto eventDto,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId,HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(EVENT, eventService.saveEvent(eventDto, mvnoId));
            response.put(RadiusConstants.MESSAGE, "Event has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Event has been created successfully, with name"+eventDto.getEventName() + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while creating  event by name: "+eventDto.getEventName() + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            apiResponseController.buildErrorMessageForResponse(response, e);
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    //Please do not uncomment without any discussion

//	@PutMapping("/update")
//	@ApiOperation(value = "Update existing event")
//	public ResponseEntity<Map<String,Object>> updateEvent(@RequestBody EventDto eventDto)
//	{
//		Map<String,Object> response = new HashMap<>();
//		try
//		{
//			response.put(EVENT, eventService.udpateEvent(eventDto));
//			response.put(RadiusConstants.MESSAGE, "Event has been updated successfully.");
//			return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
//		}
//		catch (Exception e) 
//		{
//			apiResponseController.buildErrorMessageForResponse(response, e);
//	        return apiResponseController.apiResponse(RadiusConstants.FAIL,response);
//		}
//	}

    @DeleteMapping("/delete")
    @ApiOperation(value = "Delete existing event")
    public ResponseEntity<Map<String, Object>> deleteEvent(
            @RequestParam(name = "eventId", required = true) Long eventId,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId,HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            eventService.deleteEvent(eventId, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Event has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Event has been deleted successfully, with id"+eventId + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting  event by id: "+eventId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/findByName")
    @ApiOperation(value = "Find events by name")
    public ResponseEntity<Map<String, Object>> findByName(
            @RequestParam(name = "eventName", required = true) String eventName,
            @RequestParam(name = "mvnoId", required = true) Integer mvnoId,HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(EVENT_LIST, eventService.findByName(eventName, mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Event has been fetched successfully with name,"+eventName + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching  event by name: "+eventName + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            apiResponseController.buildErrorMessageForResponse(response, e);
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }
}
