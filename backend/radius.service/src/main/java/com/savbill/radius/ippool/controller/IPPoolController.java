package com.savbill.radius.ippool.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.controller.APIResponseController;
import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.ippool.domain.IPPool;
import com.savbill.radius.ippool.model.IPPoolDTO;
import com.savbill.radius.ippool.service.IPPoolServiceImpl;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.savbill.radius.aaa.constant.MenuConstants.*;

@RestController
@RequestMapping("/SavbillRadius/ippool")
public class IPPoolController {
    private static final Logger log = LoggerFactory.getLogger(IPPoolController.class);

    private static final String IP_POOL_LIST = "ippoollist";
    private static final String IP_POOL = "ippool";
    @Autowired
    private IPPoolServiceImpl ipPoolService;
    @Autowired
    Tracer tracer;
    @Autowired
    private APIResponseController apiResponseController;

    @PreAuthorize("validatePermission(\"" + IP_MANAGMENT_DELETE +"\")")
    @PostMapping(value = {"/delete"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> delete(@RequestBody IPPoolDTO entityDTO, @RequestParam Long mvnoId, HttpServletRequest req) throws Exception {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
      //  MDC.put(RadiusConstants.USER_NAME, "");

        try {
            ipPoolService.deleteById(entityDTO.getPoolId(), mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(RadiusConstants.MESSAGE, "IP Pool has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "IP Pool has been deleted successfully.,"  /*+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","*/+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        }catch (Exception e) {
            e.printStackTrace();
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Deleting IP Pool," /*+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","*/+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
            MDC.remove(RadiusConstants.USER_NAME);
        }
    }
    @PreAuthorize("validatePermission(\"" + IP_MANAGMENT +"\")")
    @ApiOperation(value = "Get list of IP-Pool by search in the system")
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchIPPool(PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            PageableResponse<IPPool> ipPoolList = ipPoolService.getListByPageAndSize(mvnoId, paginationDTO);

            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(IP_POOL_LIST, ipPoolList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching Vlan list :,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
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

    @PreAuthorize("validatePermission(\"" + IP_MANAGMENT_CREATE +"\")")
    @PostMapping(value = {"/saveIPPool"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> save(@RequestBody IPPoolDTO ipPoolDTO, HttpServletRequest req, Long mvnoId) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "create IP Pool"  + LogConstants.REQUEST_TO_CREATE+ ipPoolDTO.getPoolName()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            ipPoolDTO = ipPoolService.saveIPPool(ipPoolDTO, mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(IP_POOL, ipPoolDTO);
            response.put(RadiusConstants.MESSAGE, "IP Pool has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "IP Pool has been created successfully:," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating IP Pool," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
            MDC.remove(RadiusConstants.USER_NAME);
        }

    }

    @ApiOperation(value = "Get IP-Pool based on the given IP-Pool id")
    @GetMapping("/findIpPoolById")
    @PreAuthorize("validatePermission(\"" + IP_MANAGMENT + "\")")
    public ResponseEntity<Map<String, Object>> findIPPoolById(@RequestParam(name = "ipPoolId", required = true) Long ipPoolId, @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            IPPool ipPool = ipPoolService.findByPoolId(ipPoolId, mvnoId, false);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(IP_POOL, ipPool);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching IP-Pool with id :," + ipPoolId  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching IP-Pool with id,"+ipPoolId + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @PreAuthorize("validatePermission(\"" + IP_MANAGMENT_EDIT +"\")")
    @PostMapping(value = {"/updateIPPool"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> update(@Valid @RequestBody IPPoolDTO ipPoolDTO, @RequestParam(name = "mvnoId", required = true) Long mvnoId, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        Map<String, Object> response = new HashMap<>();

        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());

        try {
                ipPoolDTO = ipPoolService.updateIPPool(ipPoolDTO, mvnoId);
                Integer responseCode = RadiusConstants.SUCCESS;
                response.put(IP_POOL, ipPoolDTO);
                response.put(RadiusConstants.MESSAGE, "IP Pool has been Updated successfully.");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "IP Pool has been updated successfully:,"  + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
                return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating IP Pool," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+"," + LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
            MDC.remove(RadiusConstants.USER_NAME);
        }
    }

    @ApiOperation(value = "Get list of IP-Pools in the system")
    @GetMapping("/getAll")
    @PreAuthorize("validatePermission(\"" + IP_MANAGMENT + "\")")
    public ResponseEntity<Map<String, Object>> findAllIPPool(@RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<IPPool> ipPoolList = ipPoolService.findAll(mvnoId);

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
    @GetMapping("/getAvailable")
    @PreAuthorize("validatePermission(\"" + IP_MANAGMENT + "\")")
    public ResponseEntity<Map<String, Object>> findAvailableIPPool(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<IPPool> ipPoolList = ipPoolService.findAvailableIPPools(Long.valueOf(mvnoId));

            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(IP_POOL_LIST, ipPoolList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Fetching Available IP-Pool list :,"   + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while available fetching IP-Pool list," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        }  finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

}
