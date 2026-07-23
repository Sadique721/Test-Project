package com.savbill.radius.ippool.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.controller.APIResponseController;
import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.ippool.domain.IPPoolAllocationDtls;
import com.savbill.radius.ippool.model.IPPoolAllocationDtlsDTO;
import com.savbill.radius.ippool.service.IPPoolAllocationServiceImpl;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.savbill.radius.aaa.constant.MenuConstants.IP_GET;

@RestController
@RequestMapping("/SavbillRadius/ippool/allocation")
public class IPPoolAllocationController {
    private static final Logger log = LoggerFactory.getLogger(IPPoolAllocationController.class);

    private static final String IP_POOL_LIST = "ippoollist";
    private static final String IP_POOL = "ippool";
    @Autowired
    private IPPoolAllocationServiceImpl ipPoolAllocationService;

    @Autowired
    Tracer tracer;
    @Autowired
    private APIResponseController apiResponseController;

    @ApiOperation(value = "Get list of IP-Pool by search in the system")
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchIPPool(PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Long mvnoId, @RequestParam(name = "poolId", required = true) Long poolId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            PageableResponse<IPPoolAllocationDtlsDTO> page = ipPoolAllocationService.getListByPageAndSize(paginationDTO, poolId);
            int responseCode;
            if (CollectionUtils.isEmpty(page.getData())) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put(RadiusConstants.ERROR_MESSAGE, "No Record found.");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Fetching IP-Pool,"+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.NOT_FOUND.value());
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put(IP_POOL_LIST, page);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching IP-Pool," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            }
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error("Error while fetching IP-Pool  customers with name : "  + e.getMessage());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }
    @ApiOperation(value = "Get list of IP-Pools in the system")
    @GetMapping("/getAll")
    @PreAuthorize("validatePermission(\"" + IP_GET + "\")")
    public ResponseEntity<Map<String, Object>> findAllIPPool(@RequestParam(name = "poolId", required = true) Long poolId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<IPPoolAllocationDtls> ipPoolList = ipPoolAllocationService.findAll(poolId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(IP_POOL_LIST, ipPoolList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching IP-Pool list :,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching IP-Pool list," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get list of IP-Pools in the system")
    @GetMapping("/getByIp")
    @PreAuthorize("validatePermission(\"" + IP_GET + "\")")
    public ResponseEntity<Map<String, Object>> findByIPAddress(@RequestParam(name = "poolId", required = true) Long poolId, @RequestParam(name = "ipAddress", required = true) String ipAddress, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            IPPoolAllocationDtls ipPoolList = ipPoolAllocationService.findByIPAndPoolId(poolId, ipAddress);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(IP_POOL_LIST, ipPoolList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching IP-Pool list :,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching IP-Pool list," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

}
