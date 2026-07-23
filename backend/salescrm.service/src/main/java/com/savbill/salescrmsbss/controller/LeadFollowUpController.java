package com.savbill.salescrmsbss.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import com.savbill.salescrmsbss.utils.*;
import com.savbill.salescrmsbss.utils.*;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.salescrmsbss.entity.ClientService;
import com.savbill.salescrmsbss.entity.FollowUpRemark;
import com.savbill.salescrmsbss.entity.StaffUser;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.helper.FollowUpRemarkDto;
import com.savbill.salescrmsbss.helper.LeadFollowUpDto;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.service.ClientServiceSrv;
import com.savbill.salescrmsbss.service.LeadFollowUpService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "LeadFollowUp", description = "REST APIs related to LeadFollowUp", tags = "LeadFollowUp")
@RestController
@RequestMapping("api/v1/SavbillSalesCrmsBss/followUp")
@CrossOrigin(origins = URLConstant.URL_CONSTANT)
public class LeadFollowUpController extends BaseController {

    private static String MODULE = " [LeadFollowUpController] ";

    private final Logger LOGGER = Logger.getLogger(LeadFollowUpController.class);

    private static final String FOLLOW_UP = "followUp";
    private static final String FOLLOW_UP_LIST = "followUpList";
    private static final String FOLLOW_UP_RAMARK_LIST = "followUpRemarkList";
    private static final String STAFF_USER_LIST = "staffUserList";
    private static final String RESCHEDULE_FOLLOW_UP_REMARK_LIST = "rescheduleFollowupRemarkList";

    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private LeadFollowUpService leadFollowUpService;

    @Autowired
    private ClientServiceSrv clientServiceSrv;
    @Autowired
    Tracer tracer;

    @ApiOperation(value = "Get list of followUp in the system")
    @GetMapping("/all")
//    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAll(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<LeadFollowUpDto> leadFollowUpDtoList = this.leadFollowUpService.findAll();
            if (leadFollowUpDtoList.isEmpty()) {
                response.put(FOLLOW_UP_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(FOLLOW_UP_LIST, leadFollowUpDtoList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadFollowUpList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadFollowUpList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadFollowUpList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Get list of followUp by leadId in the system")
    @GetMapping("/all/{leadId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_FOLLOW_UP + "\",\"" + MenuConstants.ENTERPRISE_LEAD_FOLLOW_UP + "\")")
//    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllByLeadId(@PathVariable Long leadId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<LeadFollowUpDto> leadFollowUpDtoList = this.leadFollowUpService.findAllByLeadId(leadId);
            if (leadFollowUpDtoList.isEmpty()) {
                response.put(FOLLOW_UP_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(FOLLOW_UP_LIST, leadFollowUpDtoList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadFollowUpList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadFollowUpList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadFollowUpList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findById/{id}")
    @ApiOperation(value = "Get followUp detail based on the given followUp id")
//    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable Long id, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            LeadFollowUpDto leadFollowUpDto = this.leadFollowUpService.findById(id);
            if (leadFollowUpDto == null) {
                response.put(SalesCrmsConstants.MESSAGE, "No record found for followUp with the given followUp Id :" + id);
            } else {
                response.put(FOLLOW_UP, leadFollowUpDto);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadFollowUp" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadFollowUp" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadFollowUp" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }


    @PostMapping("/save")
    @ApiOperation(value = "Add new followUp")
    @PreAuthorize("validatePermission(\"" + MenuConstants.ENTERPRISE_LEAD_SCHEDULE + "\",\"" + MenuConstants.LEAD_SCHEDULE + "\")")
//    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> addFollowUp(@RequestBody LeadFollowUpDto leadFollowUp, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            this.leadFollowUpService.validateRequest(leadFollowUp, CommonConstants.OPERATION_ADD);
            response.put(FOLLOW_UP, this.leadFollowUpService.save(leadFollowUp, getStaffId(authTokenHeader).intValue()));
            response.put(SalesCrmsConstants.MESSAGE, "FollowUp has been added successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create new FollowUp" + LogConstants.LOG_BY_NAME + leadFollowUp.getFollowUpName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create new FollowUp" + LogConstants.LOG_BY_NAME + leadFollowUp.getFollowUpName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create new FollowUp" + LogConstants.LOG_BY_NAME + leadFollowUp.getFollowUpName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PutMapping("/update/{id}")
    @ApiOperation(value = "Update eixsting followUp data based on the followUp id")
//    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateFollowUp(@PathVariable Long id, @RequestBody LeadFollowUpDto leadFollowUp, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        LeadFollowUpDto oldLeadFollowUp = null;
        try {
            leadFollowUp.setId(id);
            this.leadFollowUpService.validateRequest(leadFollowUp, CommonConstants.OPERATION_UPDATE);
            oldLeadFollowUp = this.leadFollowUpService.findById(id);
            response.put(FOLLOW_UP, this.leadFollowUpService.update(leadFollowUp, request));
            if (oldLeadFollowUp != null) {
                LOGGER.info("FollowUp update details" + LogConstants.LOG_BY_NAME + leadFollowUp.getFollowUpName() + UpdateDiffFinder.getUpdatedDiff(oldLeadFollowUp, leadFollowUp));
            }
            response.put(SalesCrmsConstants.MESSAGE, "FollowUp has been updated successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update followUp" + LogConstants.LOG_BY_NAME + leadFollowUp.getFollowUpName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update followUp" + LogConstants.LOG_BY_NAME + leadFollowUp.getFollowUpName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update followUp" + LogConstants.LOG_BY_NAME + leadFollowUp.getFollowUpName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findStaffUserByLeadId/{leadId}")
    @ApiOperation(value = "Get staffUser detail based on the given lead id")
//    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findStaffUserByLeadId(@PathVariable Long leadId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<StaffUser> staffUserList = this.leadFollowUpService.findStaffUserByLeadId(leadId);
            if (staffUserList.isEmpty()) {
                response.put(STAFF_USER_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No record found for staffUser with the given lead Id :" + leadId);
            } else {
                response.put(STAFF_USER_LIST, staffUserList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Find Staff User" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Find Staff User" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Find Staff User" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/closefollowup")
    @ApiOperation(value = "Close followUp")
//    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> closeFollowUp(@RequestParam("followUpId") Long followUpId, @RequestParam("remarks") String remarks, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        String followUpName = null;
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            followUpName = this.leadFollowUpService.getFollowUpNameById(followUpId);
            String authTokenHeader = request.getHeader("Authorization");
            this.leadFollowUpService.closeFollowUp(followUpId, remarks, getStaffId(authTokenHeader).intValue());
            response.put(SalesCrmsConstants.MESSAGE, "FollowUp has been closed successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
//			logger.info("LeadFollowUp is closed successfully :  request: { From : {}}; Response : {{}}",request.getHeader("requestFrom"),responseCode);
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "closed LeadFollowUp" + LogConstants.LOG_BY_NAME + followUpName + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
//			logger.error("Unable to Closed LeadFollowUp: ,"+"request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",request.getHeader("requestFrom"),responseCode,response,e.getStackTrace());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "closed LeadFollowUp" + LogConstants.LOG_BY_NAME + followUpName + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "closed LeadFollowUp" + LogConstants.LOG_BY_NAME + followUpName + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/reSchedulefollowup")
    @ApiOperation(value = "Reschedule followUp")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_RESCHEDULE + "\",\"" + MenuConstants.ENTERPRISE_LEAD_RESCHEDULE + "\")")
//    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> reScheduleFollowUp(@RequestBody LeadFollowUpDto leadFollowUp, @RequestParam("followUpId") Long followUpId, @RequestParam("remarks") String remarks, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            this.leadFollowUpService.closeAndReScheduleFollowUp(followUpId, remarks, leadFollowUp, getStaffId(authTokenHeader).intValue());
            response.put(SalesCrmsConstants.MESSAGE, "FollowUp has been rescheduled successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reschedule followUp" + LogConstants.LOG_BY_NAME + leadFollowUp.getFollowUpName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reschedule followUp" + LogConstants.LOG_BY_NAME + leadFollowUp.getFollowUpName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
//			logger.error("Unable to ReScheduled LeadFollowUp: ,"+" request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", request.getHeader("requestFrom"),responseCode,response,e.getStackTrace());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Reschedule followUp" + LogConstants.LOG_BY_NAME + leadFollowUp.getFollowUpName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/save/leadFollowUpRemark")
    @ApiOperation(value = "Add new leadFollowUpRemark")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_REMARK + "\",\"" + MenuConstants.ENTERPRISE_LEAD_REMARK + "\")")
    public ResponseEntity<Map<String, Object>> addLeadFollowUpRemark(@RequestBody FollowUpRemarkDto followUpRemark, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Save");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            response.put(FOLLOW_UP, this.leadFollowUpService.saveFollowUpRemark(followUpRemark));
            response.put(SalesCrmsConstants.MESSAGE, "FollowUpRemark has been added successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create new leadFollowUpRemark" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create new leadFollowUpRemark" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create new leadFollowUpRemark" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Get list of followUpRemark By followUpId in the system")
    @GetMapping("/findAll/followUpRemark/{followUpId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_REMARK + "\",\"" + MenuConstants.ENTERPRISE_LEAD_REMARK + "\")")
//    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllFollowUpRemarkByFollowUpid(@PathVariable Long followUpId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<FollowUpRemark> followUpRemarkList = this.leadFollowUpService.findAllFollowUpRemarkByFollowUpId(followUpId);
            if (followUpRemarkList.isEmpty()) {
                response.put(FOLLOW_UP_RAMARK_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.ERROR_MESSAGE, "No Records Found!");
            } else {
                response.put(FOLLOW_UP_RAMARK_LIST, followUpRemarkList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch FollowUpRemark" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch FollowUpRemark" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch FollowUpRemark" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/generateNameOfTheFollowUp/{leadId}")
    @ApiOperation(value = "Generate Name Of TheFollowUp")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_SCHEDULE + "\",\"" + MenuConstants.LEAD_RESCHEDULE + "\",\"" + MenuConstants.ENTERPRISE_LEAD_SCHEDULE + "\",\"" + MenuConstants.ENTERPRISE_LEAD_RESCHEDULE + "\")")
    public ResponseEntity<Map<String, Object>> generateNameOfTheFollowUp(@PathVariable Long leadId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        String generatedNameOfTheFollowUp = null;
        try {
            generatedNameOfTheFollowUp = this.leadFollowUpService.generateNameOfTheFollowUp(leadId);
            response.put("generatedNameOfTheFollowUp", generatedNameOfTheFollowUp);
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "generate Name Of The FollowUp" + LogConstants.LOG_BY_NAME + generatedNameOfTheFollowUp + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
//			logger.error("Unable to Fetch generatedNameOfTheFollowUp  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE,responseCode,response,e.getStackTrace());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "generate Name Of The FollowUp" + LogConstants.LOG_BY_NAME + generatedNameOfTheFollowUp + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
//			logger.error("Unable to Fetch generatedNameOfTheFollowUp :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",MODULE,responseCode,response,e.getStackTrace());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "generate Name Of The FollowUp" + LogConstants.LOG_BY_NAME + generatedNameOfTheFollowUp + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAllByCurruntUser")
    @ApiOperation(value = "Get list of followUp By Current User in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllFollowUpByCurruntUser(
            @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
            paginationRequestDTO.setPage(page);
            paginationRequestDTO.setPageSize(pageSize);
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            Page<LeadFollowUpDto> leadFollowUpList = this.leadFollowUpService.findAllByAssignId(getStaffId(authTokenHeader), paginationRequestDTO);
            if (leadFollowUpList.isEmpty()) {
                response.put(FOLLOW_UP_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(FOLLOW_UP_LIST, leadFollowUpList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
//			logger.info("Fetch LeadFollowUp By Pagination :  request: { From : {}}; Response : {{}}",MODULE,responseCode,response);
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch list of followUp By Current User" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch list of followUp By Current User" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch list of followUp By Current User" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/reScheduleFollowUpRemarks")
    @ApiOperation(value = "Get list of ReScheduleFollowUpRemarks in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllReScheduleFollowUpRemarks(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			ClientService clientService = this.clientServiceSrv.getByName(CommonConstants.RESCHEDULE_FOLLOW_UP_REMARKS);
            ClientService clientService = this.clientServiceSrv.getByNameAndMvnoId(CommonConstants.RESCHEDULE_FOLLOW_UP_REMARKS, getLoggedInMvnoId().longValue());

            if (clientService == null) {
                response.put(RESCHEDULE_FOLLOW_UP_REMARK_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                List<String> reScheduleFollowUpRemarkList = new ArrayList<String>(Arrays.asList(clientService.getValue().split(" , ")));
                response.put(RESCHEDULE_FOLLOW_UP_REMARK_LIST, reScheduleFollowUpRemarkList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RescheduleFollowupRemarkList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RescheduleFollowupRemarkList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RescheduleFollowupRemarkList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAllByCurruntUserAndTeam")
    @ApiOperation(value = "Get list of followUp By Current User and Team in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllFollowUpByCurruntUserAndTeam(
            @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        try {
            String authTokenHeader = request.getHeader("Authorization");
            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
            paginationRequestDTO.setPage(page);
            paginationRequestDTO.setPageSize(pageSize);
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            Page<LeadFollowUpDto> leadFollowUpList = this.leadFollowUpService.findAllByAssignIdAndTeam(getStaffId(authTokenHeader), paginationRequestDTO);
            if (leadFollowUpList.isEmpty()) {
                response.put(FOLLOW_UP_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(FOLLOW_UP_LIST, leadFollowUpList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Find All By Current User And Team" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Find All By Current User And Team" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Find All By Current User And Team" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");


        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    //get logger in user first name
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

}
