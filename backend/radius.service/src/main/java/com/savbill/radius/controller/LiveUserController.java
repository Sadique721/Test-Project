package com.savbill.radius.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.SoapApi.Dto.GenericDataDTO;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.dto.DisconnectRequest;
import com.savbill.radius.dto.LiveUserSearchDTO;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.LiveUser;
import com.savbill.radius.helper.UsersDto;
import com.savbill.radius.services.ExcelExportService;
import com.savbill.radius.services.LiveUserService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.Api;
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
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(value = "Live User Management", description = "REST APIs related to Live User Entity!!!!", tags = "Live User")
@RestController
@RequestMapping("/SavbillRadius/liveUser")
public class LiveUserController {

    private static final String LIVE_USER = "liveUser";
    private static final String LIVE_USER_LIST = "liveUserList";

    @Autowired
    ExcelExportService excelExportService;
    @Autowired
    private LiveUserService liverUserService;
    @Autowired
    private APIResponseController aPIResponseController;
    @Autowired
    private LiveUserService liveUserService;
    @Autowired
    private Tracer tracer;
    private static final Logger log = LoggerFactory.getLogger(LiveUserController.class);

    @ApiOperation(value = "Get list of live users in the system")
    @GetMapping("/all")
//    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','readAccess',#request.getHeader('requestFrom'))")
    //@PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_LIVE_USERS +"\")")
    public ResponseEntity<Map<String, Object>> getAll(PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Page<LiveUser> liveUsers = liverUserService.getAll(mvnoId, paginationDTO, request);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(liveUsers.getContent())) {
                response.put("status",RadiusConstants.NO_CONTENT_FOUND);
                response.put("message","No Records Found!");
                return aPIResponseController.apiResponse(HttpStatus.NO_CONTENT.value(), response);
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put("liveUser", liveUsers);
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "All users has been fetched successfully," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return aPIResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Live user: " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return aPIResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Disconnect live used based on the given cdrId")
    @DeleteMapping("/disconnect/{cdrId}")
//    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','deleteAccess',#request.getHeader('requestFrom'))")
    // @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_LIVE_USERS_DISCONNECT + "\")")
    public ResponseEntity<Map<String, Object>> disconnect(@PathVariable(name = "cdrId", required = true) Long cdrId, @RequestParam(name = "mvnoId", required = true) Integer mvnoId,
                                                          @RequestParam(name = "isDisconnect", required = false, defaultValue = "true") Boolean isDisconnect, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            log.debug("In Disconnect Live USer cdrId: " + cdrId);
            liverUserService.disconnectLiveUsers(Arrays.asList(cdrId), mvnoId, isDisconnect);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Live user hasbeen disconnected  successfully with cdrid," + cdrId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            response.put(RadiusConstants.MESSAGE, "live user has been DISCONNECTED successfully.");
            return aPIResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while disconnecting  Live user with cdrid: " + cdrId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return aPIResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Disconnect live used based on the given cdrId")
    @DeleteMapping("/disconnectByUserName")
    public ResponseEntity<Map<String, Object>> disconnectByUsername(@RequestBody DisconnectRequest requestPayload, @RequestParam(name = "mvnoId", required = true) Integer mvnoId,
                                                                    @RequestParam(name = "isDisconnect", required = false, defaultValue = "true") Boolean isDisconnect, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            log.debug("In Disconnect Live USer username: " + requestPayload.getUsername());
            liverUserService.disconnectLiveUsersByUsername(requestPayload.getUsername(), mvnoId, isDisconnect);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Live user hasbeen disconnected  successfully with username," + requestPayload.getUsername() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            response.put(RadiusConstants.MESSAGE, "live user has been DISCONNECTED successfully.");
            return aPIResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while disconnecting  Live user with username: " + requestPayload.getUsername() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return aPIResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Delete live used based on the given cdrId")
    @DeleteMapping("/{cdrId}")
//    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','deleteAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_LIVE_USERS_DELETE + "\")")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable(name = "cdrId", required = true) Long cdrId, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            liverUserService.delete(cdrId, mvnoId);
            response.put(RadiusConstants.MESSAGE, "liveuser has been DELETED successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " live  user has been fetched successfully with id," + cdrId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return aPIResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting  Live user by cdrid: " + cdrId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return aPIResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get live user based on the given user name")
    @PostMapping("/getByUserName")
//    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_LIVE_USERS + "\")")
    public ResponseEntity<Map<String, Object>> findByUserName(@RequestBody LiveUserSearchDTO liveUserSearchDto, @RequestParam(required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            Page<LiveUser> liveUsers = liverUserService.findLiveUsersUsingFilter(liveUserSearchDto, mvnoId);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(liveUsers.getContent())) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
            } else {
                responseCode = RadiusConstants.SUCCESS;
                response.put("liveUser", liveUsers);
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Live users has been fetched successfully with name," + liveUserSearchDto.getUserName() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return aPIResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Live user by username: " + liveUserSearchDto.getUserName() + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return aPIResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get LiveUser detail")
    @GetMapping("/liveUserDetail")
//    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','readAccess',#request.getHeader('requestFrom'))")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_LIVE_USERS + "\")")
    public ResponseEntity<Map<String, Object>> getLiveUserDetail(@RequestParam(name = "cdrID", required = true) Long cdrId, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            response.put("liveUserDetail", liverUserService.findLiveUserById(cdrId, mvnoId));
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Live users has been fetched successfully of  cdrid," + cdrId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return aPIResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            aPIResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Live user by cdrId : " + cdrId + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return aPIResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Excel Export")
    @GetMapping(value = "/exportExcel")
//    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> exportExcel(@RequestParam(name = "userName", required = false) String userName, @RequestParam(name = "framedIp", required = false) String framedIp, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletResponse httpResponse, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            httpResponse.setContentType("application/octet-stream");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());
            int responseCode = RadiusConstants.SUCCESS;
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=LiveUsers" + currentDateTime + ".xlsx";
            httpResponse.setHeader(headerKey, headerValue);
            Page<LiveUser> page = liverUserService.findLiveUsersUsingFilter(null, mvnoId);
            if (CollectionUtils.isEmpty(page.getContent())) {
                responseCode = RadiusConstants.NULL_VALUE;
                response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
            }
            excelExportService.exportExcelLiveUsers(page.getContent(), httpResponse);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Live users has been fetched successfully with name," + userName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return aPIResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            aPIResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while fetching Live user with name: " + userName + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return aPIResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Get Online or Offline Status of Customers")
    @PostMapping(value = "/isCustomersOnlineOrOffline")
//    @PreAuthorize("@roleAccesses.hasPermission('liveUsers','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> isUsersOnlineOrOffline(@RequestBody UsersDto usersDto, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            int responseCode = RadiusConstants.SUCCESS;
            List<String> liveUsers = liverUserService.findUserStatusOnlineOrOffline(usersDto);
            response.put("liveusers", liveUsers);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Online users has been fetched successfully," + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return aPIResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            aPIResponseController.buildErrorMessageForResponse(response, e);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while exporting Live user: " + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return aPIResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

//    @ApiOperation(value = "Add LiveUser detail")
//    @GetMapping("/addDummyLiveUserDetails")
//    public void addDummyLiveUserDetails(@RequestParam(name = "userName", required = true) String userName,@RequestParam(name = "password", required = true) String password, HttpServletRequest request) {
//        try {
//            liverUserService.dummyEntries(userName,password);
//        } catch (Exception e) {
//            log.error("dummyEntries");
//        }
//    }

    @ApiOperation(value = "Delete live users based on the given Ids")
    @DeleteMapping("/deleteMultiple")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_LIVE_USERS_DELETE + "\")")
    public ResponseEntity<Map<String, Object>> deleteMultiple(@RequestBody(required = true) List<Long> ids, @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            liverUserService.delete(ids, mvnoId);
            response.put(RadiusConstants.MESSAGE, "Live-users have been DELETED successfully.");
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " live  users has been fetched successfully with id," + ids + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return aPIResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.FAIL;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while deleting  Live users by id: " + ids + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return aPIResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @ApiOperation(value = "Disconnect live uses based on the given IDs")
    @DeleteMapping("/disconnectMultiple")
    @PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_LIVE_USERS_DISCONNECT + "\")")
    public ResponseEntity<Map<String, Object>> disconnectMultiple(@RequestBody(required = true) List<Long> ids, @RequestParam(name = "mvnoId", required = true) Integer mvnoId,
                                                                  @RequestParam(name = "isDisconnect", required = false, defaultValue = "true") Boolean isDisconnect, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        try {
            log.debug("In Disconnect Multiple Live User count: " + ids.size());
            liveUserService.disconnectLiveUsers(ids, mvnoId, isDisconnect);

            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Live users have been disconnected  successfully with ids," + ids + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            response.put(RadiusConstants.MESSAGE, "live users have been DISCONNECTED successfully.");
            return aPIResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            Integer responseCode = RadiusConstants.EMPTY;
            response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Error while disconnecting  Live user with cdrid: " + ids + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.EXPECTATION_FAILED.value());
            return aPIResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }


    @ApiOperation(value = "Get Framed IP address when user is live")
    @GetMapping("/getFramedIpAddress/{id}")
    public GenericDataDTO getFramedIpAddress(@PathVariable(name = "id", required = true) String id){
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put(RadiusConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put(RadiusConstants.SPAN_ID, traceContext.spanIdString());
        String framedIpAddress = "";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            log.info("Fetch assigned FramedIpAddress for custId: " + id);
            framedIpAddress = liveUserService.getframedIpAddress(id);
            genericDataDTO.setData(framedIpAddress);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Fetched FramedIpAddress Successfully.");
        }catch (Exception e){
            genericDataDTO.setData(null);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to Fetched FramedIpAddress.");
            log.error("Failed to fetch Framed Ip Address for custId: "+id);
        }finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
        return genericDataDTO;
    }
}
