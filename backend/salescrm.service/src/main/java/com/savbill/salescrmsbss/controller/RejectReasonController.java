package com.savbill.salescrmsbss.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.salescrmsbss.entity.RejectReason;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import com.savbill.salescrmsbss.service.Impl.RejectReasonServiceImpl;
import com.savbill.salescrmsbss.utils.*;
import com.savbill.salescrmsbss.utils.*;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.helper.RejectReasonDto;
import com.savbill.salescrmsbss.service.RejectReasonService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "RejectReason", description = "REST APIs related to RejectReason", tags = "RejectReason")
@RestController
@RequestMapping("api/v1/SavbillSalesCrmsBss/rejectReason")
@CrossOrigin(origins = URLConstant.URL_CONSTANT)
public class RejectReasonController extends BaseController {

    private static String MODULE = " [RejectReasonController] ";

    private final Logger LOGGER = Logger.getLogger(RejectReasonController.class);

    private static final String REJECT_REASON = "rejectReason";
    private static final String REJECT_REASON_LIST = "rejectReasonList";

    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private RejectReasonService rejectReasonService;

    @Autowired
    Tracer tracer;
    @Autowired
    private RejectReasonServiceImpl rejectReasonServiceImpl;

    @ApiOperation(value = "Search RejectReason In System")
    @PostMapping("/search")
    @PreAuthorize("validatePermission(\"" + MenuConstants.REJECTED_REASON_MASTER + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> search(@RequestBody PaginationRequestDTO paginationRequestDTO,
                                                      HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Page<RejectReasonDto> page = null;
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            page = this.rejectReasonService.search((long) getLoggedInMvnoId(), getLoggedInBuIdList(), paginationRequestDTO);
            if (page.isEmpty()) {
                response.put(REJECT_REASON_LIST, page);
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(REJECT_REASON_LIST, page);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search rejectReason using keyword: " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search rejectReason using keyword: " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            responseCode = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search rejectReason keyword: " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findById")
    @ApiOperation(value = "Get rejectReason detail based on the given rejectReason id")
    @PreAuthorize("validatePermission(\"" + MenuConstants.REJECTED_REASON_MASTER + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findById(@RequestParam("rejectReasonId") Long rejectReasonId,
                                                        HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        RejectReasonDto rejectReason = this.rejectReasonService.findById(rejectReasonId);
        try {
            if (rejectReason == null) {
                response.put(SalesCrmsConstants.MESSAGE,
                        "No record found for rejectReason with the given rejectReason id :" + rejectReasonId);
            } else {
                response.put(REJECT_REASON, rejectReason);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/save")
    @ApiOperation(value = "Add new rejectReason")
    @PreAuthorize("validatePermission(\"" + MenuConstants.CREATE_REJECTED_REASON + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> addRejectReason(@RequestBody RejectReasonDto rejectReason,
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
//			getLoggedInUserBuIds(authTokenHeader);
            this.rejectReasonService.validateRequest(rejectReason, (long) getLoggedInMvnoId(), CommonConstants.OPERATION_ADD);
            boolean isDuplicate = rejectReasonService.isDuplicate(rejectReason.getName());
            if (isDuplicate && getLoggedInMvnoId() == 1) {
                throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "Reason name already exists", null);
            }
            if (getLoggedInBuIdList().size() == 1) {
                response.put(REJECT_REASON, this.rejectReasonService.saveRejectReason(rejectReason, (long) getLoggedInMvnoId(), getLoggedInBuIdList().get(0)));
            } else if (getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty()) {
                response.put(REJECT_REASON, this.rejectReasonService.saveRejectReason(rejectReason, (long) getLoggedInMvnoId(), null));
            } else if (getLoggedInBuIdList().size() > 1) {
                throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
            }
            response.put(SalesCrmsConstants.MESSAGE, "RejectReason has been added successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
//			logger.info("RejectReason with name "+rejectReason.getName()+" is created successfully :  request: { From : {}}; Response : {{}}",request.getHeader("requestFrom"),responseCode);
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PutMapping("/update/{id}")
    @ApiOperation(value = "Update eixsting rejectReason data based on the rejectReason id")
    @PreAuthorize("validatePermission(\"" + MenuConstants.EDIT_REJECTED_REASON + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateRejectReason(@PathVariable Long id,
                                                                  @RequestBody RejectReasonDto rejectReason, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());

        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
//			getLoggedInUserBuIds(authTokenHeader);
            rejectReason.setId(id);
            RejectReason rejectReasn = (RejectReason) getEntityForUpdateAndDelete(rejectReason.getId().intValue(), "rejectReason");
            RejectReason oldClone = new RejectReason(rejectReasn);
            RejectReason rejectReasns = (RejectReason) getEntityForUpdateAndDelete(rejectReason.getId().intValue(), "rejectReason");
            if (getLoggedInBuIdList().size() == 1) {
                this.rejectReasonService.validateRequest(rejectReason, (long) getLoggedInMvnoId(), CommonConstants.OPERATION_UPDATE);
            } else if (getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty()) {
                this.rejectReasonService.validateRequest(rejectReason, (long) getLoggedInMvnoId(), CommonConstants.OPERATION_UPDATE);
            } else if (getLoggedInBuIdList().size() > 1) {
                throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
            }
            response.put(REJECT_REASON, this.rejectReasonService.updateRejectReason(rejectReason, request));
            response.put(SalesCrmsConstants.MESSAGE, "RejectReason has been updated successfully");
           // RejectReasonDto updatedreason = rejectReasonService.updateRejectReason(rejectReason, request);
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + UpdateDiffFinder.getUpdatedDiff(oldClone, rejectReasns) + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "Delete existing rejectReason based on rejectReason id")
    @PreAuthorize("validatePermission(\"" + MenuConstants.DELETE_REJECTED_REASON + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> deleteCustomer(
            @RequestParam(name = "rejectReasonId", required = true) Long rejectReasonId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        RejectReasonDto rejectReason = this.rejectReasonService.findById(rejectReasonId);
        try {
//			String authTokenHeader = request.getHeader("Authorization");

            RejectReason rejectReasn = (RejectReason) getEntityForUpdateAndDelete(rejectReason.getId().intValue(), "rejectReason");

            if (rejectReason == null) {
                response.put(SalesCrmsConstants.MESSAGE,
                        "No record found for rejectReason with the given rejectReason id :" + rejectReasonId);
            } else {
                System.out.println("Buid list size "+getLoggedInBuIdList().size() );
                if (getLoggedInBuIdList().size() == 1) {
                    this.rejectReasonService.deleteRejectReason(rejectReasonId);
                } else if (getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty()) {
                    this.rejectReasonService.deleteRejectReason(rejectReasonId);
                } else if (getLoggedInBuIdList().size() > 1) {
                    throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
                }
                response.put(SalesCrmsConstants.MESSAGE, "RejectReason has been deleted successfully");
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete RejectReason" + LogConstants.LOG_BY_NAME + rejectReason.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/all")
    @ApiOperation(value = "Get list of rejectReason in the system")
    //@PreAuthorize("validatePermission(\"" + MenuConstants.REJECTED_REASON_MASTER+ "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page, @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//				String authTokenHeader = request.getHeader("Authorization");
            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
            paginationRequestDTO.setPage(page);
            paginationRequestDTO.setPageSize(pageSize);
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            Page<RejectReasonDto> rejectReasonDto = this.rejectReasonService.findAll((long) getLoggedInMvnoId(), getLoggedInBuIdList(), paginationRequestDTO);
            if (rejectReasonDto.isEmpty()) {
                response.put(REJECT_REASON_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(REJECT_REASON_LIST, rejectReasonDto);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RejectReason" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RejectReason" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RejectReason" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/allRejectedReasonsList")
    @ApiOperation(value = "Get list of rejectReason in the system")
    public ResponseEntity<Map<String, Object>> findAllRejectedReasonsList(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());

        Integer responseCode = SalesCrmsConstants.FAIL;
        List<RejectReason> rejectReasonList = new ArrayList<>();
        try {
            rejectReasonList = rejectReasonServiceImpl.findAllRejectedReasonsList();

            if (rejectReasonList.isEmpty()) {
                response.put(REJECT_REASON_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(REJECT_REASON_LIST, rejectReasonList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RejectReasonList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RejectReasonList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RejectReasonList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
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
