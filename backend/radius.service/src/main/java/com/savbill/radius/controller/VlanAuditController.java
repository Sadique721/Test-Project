package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.VlanAudit;
import com.savbill.radius.services.impl.VlanAuditServiceImpl;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.PaginationRequestDTO;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/SavbillRadius/vlanAudit")
@RestController
public class VlanAuditController {
    @Autowired
    private APIResponseController apiResponseController;
    private static final Logger log = LoggerFactory.getLogger(VlanAuditController.class);
    @Autowired
    private Tracer tracer;
    @Autowired
    VlanAuditServiceImpl vlanAuditService;
    private static final String VLAN_AUDIT_LIST = "vlanAuditList";
    @ApiOperation(value = "Get list of VLANs in the system")
    @PostMapping("/list")
    @PreAuthorize("validatePermission(\"" + MenuConstants.GET_VLAN + "\")")
    public ResponseEntity<Map<String, Object>> vlanAuditList(@RequestBody PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            PageableResponse vlanList = vlanAuditService.findVlanAuditList(mvnoId, paginationDTO);

            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(VLAN_AUDIT_LIST, vlanList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching Vlan list :,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Vlan list," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }
    @ApiOperation(value = "Get list of VLANs audit in the system")
    @PostMapping("/getByVlanId")
    @PreAuthorize("validatePermission(\"" + MenuConstants.GET_VLAN + "\")")
    public ResponseEntity<Map<String, Object>> vlanAuditListByVlanId(@RequestBody PaginationDTO paginationDTO, @RequestParam(name = "vlanId", required = true) Long vlanId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            PageableResponse vlanList = vlanAuditService.findVlanAuditListByVlanId(vlanId, paginationDTO);

            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(VLAN_AUDIT_LIST, vlanList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching Vlan list :,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Vlan list," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PostMapping("/filter")
    public ResponseEntity<Map<String, Object>> findAllAudit( @RequestBody PaginationRequestDTO paginationDTO ,
                                                            @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID,traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID,traceContext.spanIdString());
        try {
            Page<VlanAudit> page = vlanAuditService.filterAudit(mvnoId, paginationDTO,request);
            Integer responseCode = 0;
            if(CollectionUtils.isEmpty(page.getContent())) {
                responseCode=RadiusConstants.NULL_VALUE;
                response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Request to fetch AcctCdr details," + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.NOT_FOUND.value());
            } else {
                responseCode=RadiusConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Request to fetch AcctCdr details," + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
                response.put("acctCdr",page);
            }
            return apiResponseController.apiResponse(responseCode, response);

        } catch (Exception e) {

            Integer responseCode = RadiusConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  fetch Email config details ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

}
