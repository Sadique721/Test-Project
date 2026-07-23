package com.savbill.salescrmsbss.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import com.savbill.salescrmsbss.utils.APIConstants;
import com.savbill.salescrmsbss.utils.LogConstants;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.salescrmsbss.entity.pojo.CommonListDTO;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.service.ComboService;
import com.savbill.salescrmsbss.utils.SalesCrmsConstants;
import com.savbill.salescrmsbss.utils.URLConstant;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = URLConstant.ROOT_ENDPOINT + URLConstant.COMMON_COLLECTIONS)
public class ComboController extends BaseController {

    @Autowired
    private ComboService comboService;

    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    Tracer tracer;
    private static String MODULE = " [ComboController] ";

    private final Logger LOGGER = Logger.getLogger(ComboController.class);

    //	private static final String STAFF_USER_LIST = "staffUserList";
//	private static final String PREVIOUS_VENDOR_LIST = "previousVendorList";
//	private static final String SERVICER_TYPE_LIST = "servicerTypeList";
//	private static final String BRANCH_LIST = "branchList";
//	private static final String SERVICE_AREA_LIST = "serviceAreaList";
//	private static final String PARTNER_LIST = "partnerList";
//	private static final String CUSTOMERS_LIST = "customersList";
//	private static final String LEAD_TYPE_LIST = "leadTypeList";
//	private static final String LEAD_CATEGORY_LIST = "leadCategoryList";
//	public static final String PLAN_TYPE_LIST = "planTypeList";
//	public static final String LEAD_FEASIBILITY_LIST = "leadFeasibilityList";
//	public static final String LEAD_SOURCE_AND_SUB_SOURCE_LIST = "leadSourceAndSubSourceList";
//	public static final String REJECT_REASON_AND_SUB_REASON_LIST = "rejectReasonAndSubReasonList";
//	public static final String LEAD_ORIGIN_TYPES = "leadOriginTypes";
//	public static final String REQUIRE_SERVICE_TYPES = "requireServiceTypes";
    public static final String DATA_LIST = "dataList";
    public static final String DATA = "data";

    @GetMapping("/getCommonLeadTypes")
    @ApiOperation("Get the list of common lead types in the system")
    public ResponseEntity<Map<String, Object>> getCommonLeadTypes(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;

        try {
            List<CommonListDTO> list = comboService.getLeadTypes();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Common Lead Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Common Lead Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);
        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Common Lead Typet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonPlanType")
    @ApiOperation(value = "Get list of common planType in the system")
    public ResponseEntity<Map<String, Object>> findAllCommonPlanType(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getPlanTypes();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Common  PlanTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Common  PlanTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Common  PlanTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonPreviousVendor")
    @ApiOperation(value = "Get list of common previousVendor in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllCommonPreviousVendor(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getPreviousVendors();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Common PreviousVendorList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Common PreviousVendorList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Common PreviousVendorList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonServicerType")
    @ApiOperation(value = "Get list of common servicer types in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllCommonServicerTypes(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getSeriverTypes();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Common Servicer Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Common Servicer Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching Common Servicer Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonLeadCategories")
    @ApiOperation(value = "Get list of common lead categories in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllCommonLeadCategories(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getLeadCategories();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Lead Categpries" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Lead Categpries" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Lead Categpries" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonLeadFeasibilities")
    @ApiOperation(value = "Get list of common feasibilities in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllCommonFeasibilities(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getFeasibilities();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
//			logger.info("Fetching PreviousVendorList :  request: { From : {}}; Response : {{}}", MODULE, responseCode,
//					response);
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Lead Feasibilities" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
//			logger.error(
//					"Unable to Fetch PreviousVendorList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//					MODULE, responseCode, response, e.getStackTrace());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Lead Feasibilities" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);
        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
//			logger.error(
//					"Unable to Fetch PreviousVendorList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//					MODULE, responseCode, response, e.getStackTrace());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Lead Feasibilities" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonLeadSourceAndItsSubSource")
    @ApiOperation(value = "Get list of common lead sources and it's sub sources in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCommonLeadSourceAndItsSubSource(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            //String authTokenHeader = request.getHeader("Authorization");
            List<CommonListDTO> list = comboService.getLeadSourceAndItsSubSource();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Lead Source  And Its Sub Source" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Lead Source  And Its Sub Source" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Lead Source  And Its Sub Source" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonRejectReasonAndSubReason")
    @ApiOperation(value = "Get list of common reject reasons and its sub reasons in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCommonRejectReasonAndSubReason(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            List<CommonListDTO> list = comboService.getRejectedReasonsAndItsSubReasons(getBUId(authTokenHeader),
                    getMvnoId(authTokenHeader));
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Reject Reason And Sub-Reason" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Reject Reason And Sub-Reason" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Reject Reason And Sub-Reason" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonLeadOriginTypes")
    @ApiOperation(value = "Get list of common lead origin types in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCommonLeadOriginTypes(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getLeadOriginTypes();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Common Lead Origin Types" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Common Lead Origin Types" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Common Lead Origin Types" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonRequireServiceTypes")
    @ApiOperation(value = "Get list of common require service types in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCommonRequireServiceTypes(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getRequireServiceTypes();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Require Service Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Require Service Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Require Service Type" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonStaffUsers")
    @ApiOperation(value = "Get list of common staff users in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCommonStaffUsers(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getLeadStaffs();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Staff Users" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Staff Users" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Staff Users" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonCustomers")
    @ApiOperation(value = "Get list of common customers in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCommonCustomers(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getLeadCustomers();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Customers" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Customers" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Customers" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonPartners")
    @ApiOperation(value = "Get list of common staff users in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCommonPartners(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getLeadPartners();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of common Partners" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of common Partners" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of common Partners" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonBranchList")
    @ApiOperation(value = "Get list of common branch list in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCommonBranchList(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getLeadBranchList();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Branch List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Branch List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Branch List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonServiceAreaList")
    @ApiOperation(value = "Get list of common staff users in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCommonServiceAreaList(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CommonListDTO> list = comboService.getLeadServiceAreaList();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of common Service Area List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of common Service Area List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of common Service Area List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/commonGenerateLeadNo")
    @ApiOperation(value = "Generate LeadNo")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> commonGenerateLeadNo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        try {
            CommonListDTO obj = comboService.getLeadNo();
            if (obj == null) {
                response.put(DATA, null);
                response.put(SalesCrmsConstants.MESSAGE, "No Lead Number Found!");
            } else {
                response.put(DATA, obj);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Generate Lead Number" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Generate Lead Number" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Generate Lead Number" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/getCommonCustomerGenderValues")
    @ApiOperation(value = "Get common customer gender values")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCommonCustomerGenderValues(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        try {
            List<CommonListDTO> list = comboService.getCustomerGenderValues();
            if (list == null || list.size() == 0) {
                response.put(DATA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(DATA_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Customer Gender Value" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Customer Gender Value" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get Common Customer Gender Value" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + response);
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
