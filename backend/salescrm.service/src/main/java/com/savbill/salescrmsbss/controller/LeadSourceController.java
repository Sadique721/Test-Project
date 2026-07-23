package com.savbill.salescrmsbss.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.salescrmsbss.entity.LeadSource;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.helper.LeadSourceDto;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.service.LeadSourceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "LeadSource", description = "REST APIs related to LeadSource", tags = "LeadSource")
@RestController
@RequestMapping("api/v1/SavbillSalesCrmsBss/leadSource")
@CrossOrigin(origins = URLConstant.URL_CONSTANT)
public class LeadSourceController extends BaseController {

    private static String MODULE = " [LeadSourceController] ";

    private final Logger LOGGER = Logger.getLogger(LeadSourceController.class);

    private static final String LEAD_SOURCE = "leadSource";
    private static final String LEAD_SOURCE_LIST = "leadSourceList";

    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    Tracer tracer;
    @Autowired
    private LeadSourceService leadSourceService;

    @ApiOperation(value = "Get list of leadSource in the system")
    @GetMapping("/list")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllData(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        try {
            String authTokenHeader = request.getHeader("Authorization");
            List<LeadSourceDto> leadSourceList = this.leadSourceService.findAll(getMvnoId(authTokenHeader), getBUId(authTokenHeader));
            if (leadSourceList == null || leadSourceList.isEmpty()) {
                response.put(LEAD_SOURCE_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_SOURCE_LIST, leadSourceList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadSource List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadSource List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadSource List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Get list of leadSource in the system")
    @GetMapping("/all")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_SOURCE_MASTER + "\")")
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(value = "page", defaultValue
                    = "1", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            //String authTokenHeader = request.getHeader("Authorization");
            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
            paginationRequestDTO.setPage(page);
            paginationRequestDTO.setPageSize(pageSize);
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            Page<LeadSourceDto> leadSourceList = this.leadSourceService.findAll(Long.valueOf(getLoggedInMvnoId()), getLoggedInBuIdList(), paginationRequestDTO);
            if (leadSourceList == null || leadSourceList.isEmpty()) {
                response.put(LEAD_SOURCE_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_SOURCE_LIST, leadSourceList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All LeadSource" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All LeadSource" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All LeadSource" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Search LeadSource In System")
    @PostMapping("/search")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_SOURCE_MASTER + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> search(@RequestBody PaginationRequestDTO paginationRequestDTO,
                                                      HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Page<LeadSource> page = null;
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            page = this.leadSourceService.search((long) getLoggedInMvnoId(), getLoggedInBuIdList(), paginationRequestDTO);
            if (page.isEmpty()) {
                response.put(LEAD_SOURCE_LIST, page);
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_SOURCE_LIST, page);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search leadSource for : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search leadSource for : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search leadSource for : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findById")
    @ApiOperation(value = "Get leadSource detail based on the given leadSource id")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_SOURCE_MASTER + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findById(@RequestParam("leadSourceid") Long leadSourceid,
                                                        HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        LeadSource leadSource = this.leadSourceService.findById(leadSourceid);
        try {
            if (leadSource == null) {
                response.put(SalesCrmsConstants.MESSAGE,
                        "No record found for leadSource with the given leadSource id :" + leadSourceid);
            } else {
                response.put(LEAD_SOURCE, leadSource);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findByName")
    @ApiOperation(value = "Get leadSource detail based on the given leadSource name")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findByName(@RequestParam("leadSourceName") String leadSourceName,
                                                          HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<LeadSource> leadSourceList = this.leadSourceService.findByName(leadSourceName);
            if (leadSourceList.isEmpty()) {
                response.put(LEAD_SOURCE_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_SOURCE_LIST, leadSourceList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadSource" + LogConstants.LOG_BY_NAME + leadSourceName + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());

            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadSource" + LogConstants.LOG_BY_NAME + leadSourceName + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadSource" + LogConstants.LOG_BY_NAME + leadSourceName + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/save")
    @ApiOperation(value = "Add new leadSource")
    @PreAuthorize("validatePermission(\"" + MenuConstants.CREATE_LEAD_SOURCE + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> addLeadSource(@RequestBody LeadSourceDto leadSource,
                                                             HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            this.leadSourceService.validateRequest(leadSource, (long) getLoggedInMvnoId(), CommonConstants.OPERATION_ADD);
//			getLoggedInUserBuIds(authTokenHeader);
            if (getLoggedInBuIdList().size() == 1) {
                response.put(LEAD_SOURCE, this.leadSourceService.saveLeadSource(leadSource, (long) getLoggedInMvnoId(), getLoggedInBuIdList().get(0)));
            }
            if (getLoggedInBuIdList().isEmpty() || getLoggedInBuIdList().size() == 0) {
                response.put(LEAD_SOURCE, this.leadSourceService.saveLeadSource(leadSource, (long) getLoggedInMvnoId(), null));
            } else if (getLoggedInBuIdList().size() > 1) {
                throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
            }
            response.put(SalesCrmsConstants.MESSAGE, "LeadSource has been added successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PutMapping("/update/{id}")
    @ApiOperation(value = "Update eixsting leadSource data based on the leadSource id")
    @PreAuthorize("validatePermission(\"" + MenuConstants.EDIT_LEAD_SOURCE + "\")")

//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateLeadSource(@PathVariable Long id,
                                                                @RequestBody LeadSourceDto leadSource, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        LeadSource oldLeadSource = null;
        try {
            //String authTokenHeader = request.getHeader("Authorization");
            //getLoggedInUserBuIds(authTokenHeader);
            LeadSource leadSrc = (LeadSource) getEntityForUpdateAndDelete(leadSource.getId().intValue(), "leadSource");
            oldLeadSource = new LeadSource(leadSrc);
            this.leadSourceService.validateRequest(leadSource, (long) getLoggedInMvnoId(), CommonConstants.OPERATION_UPDATE);

            if (getLoggedInBuIdList().size() == 1) {
                response.put(LEAD_SOURCE, this.leadSourceService.updateLeadSource(leadSource, request));
            } else if (getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty()) {
                response.put(LEAD_SOURCE, this.leadSourceService.updateLeadSource(leadSource, request));
            } else if (getLoggedInBuIdList().size() > 1) {
                throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
            }
            LeadSource newLeadSource = (LeadSource) getEntityForUpdateAndDelete(leadSource.getId().intValue(), "leadSource");

            response.put(SalesCrmsConstants.MESSAGE, "LeadSource has been updated successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            if (oldLeadSource != null) {
                LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + UpdateDiffFinder.getUpdatedDiff(oldLeadSource, newLeadSource) + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "Delete existing leadSource based on leadSource id")
    @PreAuthorize("validatePermission(\"" + MenuConstants.DELETE_LEAD_SOURCE + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> deleteLeadSource(
            @RequestParam(name = "leadSourceId", required = true) Long leadSourceId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        LeadSource leadSource = this.leadSourceService.findById(leadSourceId);
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            LeadSource leadSrc = (LeadSource) getEntityForUpdateAndDelete(leadSource.getId().intValue(), "leadSource");
            if (leadSource == null) {
                response.put(SalesCrmsConstants.MESSAGE,
                        "No record found for leadSource with the given leadSource id :" + leadSourceId);
            } else {
                if (getLoggedInBuIdList().size() == 1) {
                    this.leadSourceService.deleteLeadSource(leadSourceId);
                } else if (getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty()) {
                    this.leadSourceService.deleteLeadSource(leadSourceId);
                } else if (getLoggedInBuIdList().size() > 1) {
                    throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
                }
                response.put(SalesCrmsConstants.MESSAGE, "LeadSource has been deleted successfully");
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete LeadSource" + LogConstants.LOG_BY_NAME + leadSource.getLeadSourceName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
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
