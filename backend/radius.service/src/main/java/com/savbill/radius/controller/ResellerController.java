package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.AcctCdr;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.services.ExcelExportService;
import com.savbill.radius.services.ResellerService;
import com.savbill.radius.services.impl.AcctCdrServiceImpl;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(value = "Reseller Management", description = "REST APIs related to Reseller Entity!!!!", tags = "Reseller")
@RestController
@RequestMapping("/SavbillRadius/Reseller")
public class ResellerController {



    @Autowired
    private ResellerService resellerService;
    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    private ExcelExportService excelExportService;
    @Autowired
    private Tracer tracer;
    private static final Logger log = LoggerFactory.getLogger(AcctCdrServiceImpl.class);
    @GetMapping("/AcctCdr/all")
    @PreAuthorize("@roleAccesses.hasPermission('cdrs','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllAcctCdrs(PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "locationId", required = false) Long locationId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Page<AcctCdr> page = resellerService.findAllAcctCdr(mvnoId, paginationDTO, locationId);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(page.getContent())) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put("errorMessage", "No Records found");
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put("acctCdr", page);
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Acctcdr has been fetched successfully" + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Acctcdrs: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/AcctCdr/findByUserName")
    @PreAuthorize("@roleAccesses.hasPermission('cdrs','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAcctCdrByUserName(PaginationDTO paginationDTO, @RequestParam(name = "userName", required = false) String userName, @RequestParam(name = "framedIpAddress", required = false) String framedIpAddress, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "locationId", required = false) Long locationId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            if (paginationDTO.getSize() < 1) {
                response.put(RadiusConstants.ERROR_MESSAGE, "Page size must not be less than one!");
                return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
            }
            Page<AcctCdr> page = resellerService.findAcctCrdByUserName(userName, framedIpAddress, mvnoId, paginationDTO, locationId);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(page.getContent())) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put("errorMessage", "No Records found");
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put("acctCdr", page);
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "AcctCdr is fetched successfully  for username"+userName + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Acctcdr for username: "+userName + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @DeleteMapping("/AcctCdr/delete")
    @PreAuthorize("@roleAccesses.hasPermission('cdrs','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> deleteAcctCdr(@RequestParam(name = "cdrid", required = true) Long cdrId, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "locationId", required = false) Long locationId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            resellerService.deleteAcctCdrById(cdrId, mvnoId, locationId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(RadiusConstants.MESSAGE, "AcctCdr has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "AcctCdr is Deleted successfully  by id"+cdrId + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting Acctcdr by id: "+ cdrId+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/AcctCdr/cdrDetail")
    @PreAuthorize("@roleAccesses.hasPermission('cdrs','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCdrDetail(@RequestParam(name = "cdrId", required = true) Long cdrId, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "locationId", required = false) Long locationId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put("cdrDetail", resellerService.findAcctCdrById(cdrId, mvnoId, locationId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Fetching Acctcdr with id "+cdrId + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Acctcdr by id: "+cdrId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            apiResponseController.buildErrorMessageForResponse(response, e);
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Excel Export")
    @GetMapping(value = "/AcctCdr/exportExcel")
    @PreAuthorize("@roleAccesses.hasPermission('cdrs','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> exportExcel(@RequestParam(name = "userName", required = false) String userName, @RequestParam(name = "framedIp", required = false) String framedIp, @RequestParam(name = "fromDate", required = false) String fromDate, @RequestParam(name = "toDate", required = false) String toDate, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "locationId", required = false) Long locationId, HttpServletResponse httpResponse, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            httpResponse.setContentType("application/octet-stream");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=CDRUsers" + currentDateTime + ".xlsx";
            httpResponse.setHeader(headerKey, headerValue);
            PaginationDTO paginationDTO = new PaginationDTO();
            if (fromDate != null) {
                paginationDTO.setFromDate(fromDate);
            }
            if (fromDate != null) {
                paginationDTO.setToDate(toDate);
            }
            Page<AcctCdr> page = resellerService.findAcctCrdByUserName(userName, framedIp, mvnoId, paginationDTO, locationId);
            if (CollectionUtils.isEmpty(page.getContent())) {
                throw new IllegalArgumentException("No record found");
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " live user has been Exported Successfully " + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            excelExportService.exportExcel(page.getContent(), httpResponse);
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Exporting Acctcdr by username: "+userName + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            apiResponseController.buildErrorMessageForResponse(response, e);
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get list of live users in the system")
    @GetMapping("/LiveUser/all")
    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getAll(PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request, @RequestParam(name = "locationId", required = false) Long locationId) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Page<LiveUser> liveUsers = resellerService.getAll(mvnoId, paginationDTO, locationId);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(liveUsers.getContent())) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put("errorMessage", "No Records found");
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put("liveUser", liveUsers);
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " live user has been fetched suceesfully  " + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Live Users: " + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Delete live used based on the given cdrId")
    @DeleteMapping("/LiveUser/delete")
    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> delete(@RequestParam(name = "cdrid", required = true) Long cdrId, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request, @RequestParam(name = "locationId", required = false) Long locationId) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            resellerService.delete(cdrId, mvnoId, locationId);
            response.put(RadiusConstants.MESSAGE, "liveuser has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " live user has been deleted suceesfully with cdrid "+cdrId + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting Live users: "+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get live user based on the given user name")
    @GetMapping("/LiveUser/getByUserName")
    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findByUserName(PaginationDTO paginationDTO, @RequestParam(name = "userName", required = false) String userName, @RequestParam(name = "framedIpAddress", required = false) String framedIpAddress, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request, @RequestParam(name = "locationId", required = false) Long locationId) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Page<LiveUser> liveUsers = resellerService.findByUserName(userName, framedIpAddress, mvnoId, paginationDTO, locationId);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(liveUsers.getContent())) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put("errorMessage", "No Records found");
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put("liveUser", liveUsers);
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Fetching live users  BY username"+userName + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Live user  by username: "+userName + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get LiveUser detail")
    @GetMapping("/LiveUser/liveUserDetail")
    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getLiveUserDetail(@RequestParam(name = "cdrID", required = true) Long cdrId, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request, @RequestParam(name = "locationId", required = false) Long locationId) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put("liveUserDetail", resellerService.findLiveUserById(cdrId, mvnoId, locationId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Fetching live users by cdrid :"+cdrId + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Live user by cdrId: "+cdrId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Excel Export")
    @GetMapping(value = "/LiveUser/exportExcel")
    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> exportExcel(@RequestParam(name = "userName", required = false) String userName, @RequestParam(name = "framedIp", required = false) String framedIp, HttpServletResponse httpResponse, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, @RequestParam(name = "locationId", required = false) Long locationId,HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            httpResponse.setContentType("application/octet-stream");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());

            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=LiveUsers" + currentDateTime + ".xlsx";
            httpResponse.setHeader(headerKey, headerValue);
            Page<LiveUser> page = resellerService.findByUserName(userName, framedIp, mvnoId, null, locationId);
            if (CollectionUtils.isEmpty(page.getContent())) {
                throw new IllegalArgumentException("No record found");
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Fetching live users "+userName + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            excelExportService.exportExcelLiveUsers(page.getContent(), httpResponse);
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Exporting live user: "+userName + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

}
