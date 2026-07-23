package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.entity.ProxyServer;
import com.savbill.radius.helper.ProxyServerDto;
import com.savbill.radius.services.CacheConfigService;
import com.savbill.radius.services.ProxyServerService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Proxy Server Management", description = "REST APIs related to Proxy Server Entity!!!!", tags = "Proxy Server")
@RestController
@RequestMapping("/SavbillRadius/proxyserver")
public class ProxyServerController {

    private static final String PROXY_SERVER = "proxyServer";
    private static final String PROXY_SERVER_LIST = "proxyServerList";
    private static final Logger log = LoggerFactory.getLogger(ProxyServerController.class);
    @Autowired
    private ProxyServerService proxyServerService;
    @Autowired
    private APIResponseController responseController;
    @Autowired
    private Tracer tracer;
    @Autowired
    private CacheConfigService cacheConfigService;

    @ApiOperation(value = "Get list of proxy servers in the system")
    @GetMapping("/all")
//    @PreAuthorize("@roleAccesses.hasPermission('proxy','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_PROXY_CONFIG + "\")")
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<ProxyServer> proxyServers = proxyServerService.getAll(mvnoId);
            response.put(PROXY_SERVER_LIST, proxyServers);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Proxy server has been fetched successfully " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return responseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            responseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching  Proxy server: " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return responseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Add new proxy server")
    @PostMapping
//    @PreAuthorize("@roleAccesses.hasPermission('proxy','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_PROXY_CONFIG_CREATE + "\")")
    public ResponseEntity<Map<String, Object>> add(@Valid @RequestBody ProxyServerDto proxyServerDto, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(PROXY_SERVER, proxyServerService.save(proxyServerDto, mvnoId));
            response.put(RadiusConstants.MESSAGE, "Proxy server has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Proxy server has been updated successfully," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            //cacheConfigService.reloadCache(AAAConstant.PROXYCACHE);
            return responseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            responseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating  Proxy server " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return responseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Update existing proxy server based on the given id")
    @PutMapping("/{id}")
//    @PreAuthorize("@roleAccesses.hasPermission('proxy','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_PROXY_CONFIG_EDIT + "\")")
    public ResponseEntity<Map<String, Object>> update(@PathVariable(name = "id", required = true) Long id, @Valid @RequestBody ProxyServerDto proxyServerDto, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(PROXY_SERVER, proxyServerService.update(id, proxyServerDto, mvnoId, request));
            response.put(RadiusConstants.MESSAGE, "proxy server has been updated successfully.");
            //cacheConfigService.reloadCache(AAAConstant.PROXYCACHE);
            return responseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            responseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while updating  Proxy server by id: " + id + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return responseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Delete proxy server based on the given id")
    @DeleteMapping("/{id}")
//    @PreAuthorize("@roleAccesses.hasPermission('proxy','deleteAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_PROXY_CONFIG_DELETE + "\")")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable(name = "id", required = true) Long id, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            proxyServerService.delete(id, mvnoId);
            response.put(RadiusConstants.MESSAGE, "proxy server has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Proxy server has been deleted successfully of given ID," + id + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            //cacheConfigService.reloadCache(AAAConstant.PROXYCACHE);
            return responseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            responseController.buildErrorMessageForResponse(response, e);
            log.error("Error while creating Proxy server: " + e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while creating  Proxy server " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return responseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get proxy server based on the given id")
    @GetMapping("/{id}")
//    @PreAuthorize("@roleAccesses.hasPermission('proxy','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> get(@PathVariable(name = "id", required = true) Long id, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(PROXY_SERVER, proxyServerService.getById(id, mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Proxy server has been updated successfully of given ID," + id + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return responseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            responseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching  Proxy server by id: " + id + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return responseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get proxy server based on the given name")
    @GetMapping(value = {"/name/{name}", "/name/"})
//    @PreAuthorize("@roleAccesses.hasPermission('proxy','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getByName(@PathVariable(name = "name", required = false) String name, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put(PROXY_SERVER_LIST, proxyServerService.getByName(name, mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Proxy server has been fetched successfully of given name," + name + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return responseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            responseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching  Proxy server by name: " + name + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return responseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Update proxy server based on the given id and status value")
    @GetMapping("/updateStatus")
//    @PreAuthorize("@roleAccesses.hasPermission('proxy','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateStatus(@RequestParam(name = "id", required = true) Long id, @RequestParam(name = "status", required = true) String status, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            proxyServerService.updateStatus(id, status, mvnoId, request);
            response.put(RadiusConstants.MESSAGE, "proxy server status has been updated successfully.");
            //cacheConfigService.reloadCache(AAAConstant.PROXYCACHE);
            return responseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            responseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while changing proxy server status: " + status + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return responseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }
}
