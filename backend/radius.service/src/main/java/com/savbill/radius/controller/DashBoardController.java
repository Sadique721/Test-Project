package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.services.DashBoardService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Api(value = "Dashboard Management", description = "REST APIs for dashboard graphs data.", tags = "Dashboard")
@RestController
@RequestMapping("/SavbillRadius/dashboard")
public class DashBoardController {
    private static final String CONNECTED_USER = "connectedUser";
    private static final String CONSUMED_DATA_MAP = "consumedMap";
    private static final String AVG_SESSION_DATA_MAP = "avgSessionDataByDate";
    private static final String AUTH_FAIL_DATA = "authFailData";
    //    private static final Log log = LogFactory.getLog(DashboardServiceImpl.class);

    @Autowired
    Tracer tracer;
    @Autowired
    private DashBoardService dashBoardService;
    @Autowired
    private APIResponseController aPIResponseController;
    @Autowired
    private APIResponseController apiResponseController;
    private static final Logger log = LoggerFactory.getLogger(DBMappingController.class);
    @ApiOperation(value = "Get data for daily consume graph.")
    @GetMapping("/getDataForDailyConsumeGraph")
//    @PreAuthorize("@roleAccesses.hasPermission('cdrs','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getDataForDailyConsumeGraph(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "locationId", required = false) Long locationId, HttpServletRequest request) throws ParseException {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        Map<String, Integer> getDataForDailyConsumeGraph = new HashMap<>();
        try {
            if (Objects.isNull(locationId)) {
                getDataForDailyConsumeGraph = dashBoardService.getDailyConsumeDataOfLastSevenDays(mvnoId);
            } else {
                getDataForDailyConsumeGraph = dashBoardService.getDailyConsumeDataOfLastSevenDaysForPWSC(mvnoId, locationId);
            }
            response.put(CONSUMED_DATA_MAP, getDataForDailyConsumeGraph);
            Integer responseCode = RadiusConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Request to Fetch Daily consume data:,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Fetching Daily Consumed data for mvnoId"+mvnoId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get list of connected users in the system")
    @GetMapping("/getAllConnectedUser")

//    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getAllConnectedUser(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "locationId", required = false) Long locationId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        Long connectedUser;
        try {
            if (Objects.isNull(locationId)) {
                connectedUser = dashBoardService.connectedUser(mvnoId);
            } else {
                connectedUser = dashBoardService.connectedUserForPWSC(mvnoId, locationId);
            }
            response.put(CONNECTED_USER, connectedUser);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Request to Fetch all Connected users:,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return aPIResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching all connected user for mvnoId,"+mvnoId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return aPIResponseController.apiResponse(RadiusConstants.FAIL, response);

        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get Avg session time on particular date with respective mvnoId")
    @GetMapping("/getAverageSessionTimeByDate")
//    @PreAuthorize("@roleAccesses.hasPermission('cdrs','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getAverageSessionTimeByDate(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "locationId", required = false) Long locationId, HttpServletRequest request) throws ParseException {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        Map<String, Integer> getAvgSessionTimeByDate = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Integer responseCode = RadiusConstants.SUCCESS;
            if (Objects.isNull(locationId)) {
                getAvgSessionTimeByDate = dashBoardService.getAvgSessionTimeByDate(mvnoId);
            } else {
                getAvgSessionTimeByDate = dashBoardService.getAvgSessionTimeByDateForPWSC(mvnoId, locationId);
            }
            response.put(AVG_SESSION_DATA_MAP, getAvgSessionTimeByDate);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Average Sesion time on perticular date with respective  mvnoId:,"+mvnoId   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to fetch Average Session  time on perticular date wit data for mvnoId"+mvnoId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get authentication failure date with respective mvnoId")
    @GetMapping("/getAuthFailureData")
//    @PreAuthorize("@roleAccesses.hasPermission('audit','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getAuthFailureData(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "locationId", required = false) Long locationId, HttpServletRequest request) throws ParseException {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        Map<String, Integer> getAuthData = new HashMap<>();
        try {
            Integer responseCode = RadiusConstants.SUCCESS;
            if (Objects.isNull(locationId)) {
                getAuthData = dashBoardService.getAuthFailureData(mvnoId);
            } else {
                getAuthData = dashBoardService.getAuthFailureDataForPWSC(mvnoId, locationId);
            }
            response.put(AUTH_FAIL_DATA, getAuthData);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR +" Fetching Authontigation failure data:," +mvnoId  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Fetching Authentication failure data for mvnoId"+mvnoId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

}

