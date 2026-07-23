package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.entity.Client;
import com.savbill.radius.helper.ClientDto;
import com.savbill.radius.mvno.Repository.MvnoRepository;
import com.savbill.radius.services.CacheConfigService;
import com.savbill.radius.services.ClientService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Client Management", description = "REST APIs related to Client Entity!!!!", tags = "Radius Client")
@RestController
@RequestMapping("/SavbillRadius")
public class ClientController {

    private static final String CLIENT_LIST = "clientList";
    private static final String CLIENT = "client";

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private Tracer tracer;

    @Autowired
    private CacheConfigService cacheConfigService;

    @ApiOperation(value = "Get list of clients in the system")
    @GetMapping("/clients")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusClient','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CLIENT + "\")")
    public ResponseEntity<Map<String, Object>> findAllClients(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<Client> clientList = clientService.findAllClients(mvnoId);

            for (Client client : clientList) {
                if (mvnoRepository.findMvnoNameById(client.getMvnoId().longValue()) != null) {
                    client.setMvnoName(mvnoRepository.findMvnoNameById(client.getMvnoId().longValue()));
                }
            }
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(CLIENT_LIST, clientList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Client list :," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Client list," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get client based on the given client id")
    @GetMapping("/findClientById")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CLIENT + "\")")
    public ResponseEntity<Map<String, Object>> findClientById(@RequestParam(name = "clientId", required = true) Long clientId, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Client clientVo = clientService.findClientById(clientId, mvnoId, request);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(CLIENT, clientVo);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Client with id :," + clientId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching client with id," + clientId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get list of clients based on the given Ip address")
    @GetMapping("/findClientByIpAddress")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CLIENT + "\")")
    public ResponseEntity<Map<String, Object>> findClientByIpAddress(@RequestParam(name = "ipAddress", required = true) String ipAddress, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<Client> clientList = clientService.findClientByIpAddress(ipAddress, mvnoId);
            Integer responseCode = 0;
            if (clientList.isEmpty()) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Client has been fetched successfully," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.NOT_FOUND.value());
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put(CLIENT_LIST, clientList);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Client has been fetched successfully of IpAddress :," + ipAddress + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            }
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching client with ip:," + ipAddress + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Add new client")
    @PostMapping("/addClient")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusClient','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CLIENT_CREATE + "\")")
    public ResponseEntity<Map<String, Object>> addClient(@RequestBody ClientDto client, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Client clientVo = clientService.saveClient(client, mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(CLIENT, clientVo);
            response.put(RadiusConstants.MESSAGE, "Client has been added successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Client has been created successfully:," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            //cacheConfigService.reloadCache(AAAConstant.CLIENTDETAILCACHE);
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Creating Client ," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Update client based on the client id")
    @PutMapping("/updateClient")
//    @PreAuthorize("@roleAccesses.hasPermission('radiusClient','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CLIENT_EDIT + "\")")
    public ResponseEntity<Map<String, Object>> updateClient(@RequestBody Client client, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Client clientVo = clientService.updateClient(client, mvnoId, request);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(CLIENT, clientVo);
            response.put(RadiusConstants.MESSAGE, "Client has been updated successfully.");
            //cacheConfigService.reloadCache(AAAConstant.CLIENTDETAILCACHE);
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Updating Client," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }

    }

    @ApiOperation(value = "Delete client as per the given client id")
    @DeleteMapping("/deleteClient")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CLIENT_DELETE + "\")")
    public ResponseEntity<Map<String, Object>> deleteClient(@RequestParam(name = "clientId", required = true) Long clientId, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            clientService.deleteClientById(clientId, mvnoId);
            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(RadiusConstants.MESSAGE, "Client has been deleted successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Client has been deleted successfully.," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            //cacheConfigService.reloadCache(AAAConstant.CLIENTDETAILCACHE);
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while Deleting Client," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }


    @ApiOperation(value = "Get list of clients in the system for dropdown")
    @GetMapping("/all/clients")
    public ResponseEntity<Map<String, Object>> findAllClientsForDropdown(@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            List<ClientDto> clientList = clientService.findAllClientList(mvnoId);

            Integer responseCode = RadiusConstants.SUCCESS;
            response.put(CLIENT_LIST, clientList);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Client list :," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Client list," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }
}
