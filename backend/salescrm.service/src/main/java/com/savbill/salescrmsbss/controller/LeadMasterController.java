package com.savbill.salescrmsbss.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.salescrmsbss.entity.*;
import com.savbill.salescrmsbss.entity.pojo.*;
import com.savbill.salescrmsbss.repository.*;
import com.savbill.salescrmsbss.entity.*;
import com.savbill.salescrmsbss.entity.pojo.CustPlanMapppingPojo;
import com.savbill.salescrmsbss.entity.pojo.LeadMasterPojo;
import com.savbill.salescrmsbss.entity.pojo.SearchLeadByBuidDTO;
import com.savbill.salescrmsbss.entity.pojo.StaffUserDTO;
import com.savbill.salescrmsbss.repository.*;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import com.savbill.salescrmsbss.utils.*;

import com.savbill.salescrmsbss.utils.*;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.helper.LeadMgmtWfDTO;
import com.savbill.salescrmsbss.helper.LeadNotesDto;
import com.savbill.salescrmsbss.helper.LeadRejectDto;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.service.ClientServiceSrv;
import com.savbill.salescrmsbss.service.LeadMasterService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static com.savbill.salescrmsbss.entity.QLeadMaster.leadMaster;

@Api(value = "LeadMaster", description = "REST APIs related to LeadMaster", tags = "LeadMaster")
@RestController
@RequestMapping("api/v1/SavbillSalesCrmsBss/leadMaster")
@CrossOrigin(origins = URLConstant.URL_CONSTANT)
public class LeadMasterController extends BaseController {

    @Autowired
    private CityRepository cityRepository;

    private static String MODULE = " [LeadMasterController] ";

    private final Logger LOGGER = Logger.getLogger(LeadMasterController.class);

    private static final String LEAD_MASTER = "leadMaster";
    private static final String LEAD_NOTES = "leadNotes";
    private static final String LEAD_MASTER_LIST = "leadMasterList";
    private static final String LEAD_AUDIT_LIST = "leadAuditList";
    private static final String STAFF_USER_LIST = "staffUserList";
    private static final String PREVIOUS_VENDOR_LIST = "previousVendorList";
    private static final String SERVICER_TYPE_LIST = "servicerTypeList";
    private static final String BRANCH_LIST = "branchList";
    private static final String SERVICE_AREA_LIST = "serviceAreaList";
    private static final String PARTNER_LIST = "partnerList";
    private static final String CUSTOMERS_LIST = "customersList";
    private static final String LEAD_TYPE_LIST = "leadTypeList";
    private static final String LEAD_CATEGORY_LIST = "leadCategoryList";
    private static final String FEASIBILITY_LIST = "feasibility";
    private static final String PLAN_TYPE_LIST = "planType";
    private static final String LEAD_NO = "leadNo";
    private static final String LEAD_NOTES_LIST = "leadNoteList";
    private static final String POP_MANAGEMENT_LIST = "popManagementList";
    private static final String LEAD_ORIGIN_TYPE_LIST = "leadOriginTypeList";
    private static final String REQUIRE_SERVICE_TYPE_LIST = "requireServiceTypeList";
    private static final String CAF_NO = "cafNo";
    private static final String LEAD_CUSTOMER_GENDER_TYPE_LIST = "leadCustomerGender";
    private static final String LEAD_SERVICE_MAPPING_LIST = "leadServiceMappingList";
    private static final String PLAN_MAPPING_LIST = "planMappingList";
    private static final String LEAD_SERVICE_MAPPING = "leadServiceMapping";

    private static final String Final_PLAN_MAPPING_LIST = "planMappingList";

    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private LeadMasterService leadMasterService;

    @Autowired
    private LeadAuditRepository leadAuditRepository;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PopManagementRepository popManagementRepository;

    @Autowired
    Tracer tracer;

    @GetMapping("/findById")
    @ApiOperation(value = "Get lead detail based on the given lead id")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD + "\",\"" + MenuConstants.LEAD_DETAILS + "\",\"" + MenuConstants.ENTERPRISE_LEAD + "\",\"" + MenuConstants.ENTERPRISE_LEAD + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findById(@RequestParam("leadId") Long leadId,
                                                        HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        LeadMasterPojo leadMasterPojo = this.leadMasterService.findById(leadId);
        try {
            if (leadMasterPojo == null) {
                response.put(SalesCrmsConstants.MESSAGE, "No record found for lead with the given lead Id :" + leadId);
            } else {
                response.put(LEAD_MASTER, leadMasterPojo);
            }
            responseCode = SalesCrmsConstants.SUCCESS;

            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch lead detail" + LogConstants.LOG_BY_NAME + leadMasterPojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch lead detail" + LogConstants.LOG_BY_NAME + leadMasterPojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch lead detail" + LogConstants.LOG_BY_NAME + leadMasterPojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findByMobileNo")
    @ApiOperation(value = "Get lead detail based on the given lead mobileno")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findByMobileno(@RequestParam("mobileNo") String mobileNo,
                                                              HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            List<LeadMasterPojo> leadMasterPojo = this.leadMasterService.findByMobileNo((long) getLoggedInMvnoId(),
                    getLoggedInBuIdList(), mobileNo);
            if (leadMasterPojo == null) {
                response.put(LEAD_MASTER_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE,
                        "No record found for lead with the given lead mobileNo :" + mobileNo);
            } else {
                response.put(LEAD_MASTER_LIST, leadMasterPojo);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead By Mobile Number : " + mobileNo + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead By Mobile Number : " + mobileNo + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead By Mobile Number : " + mobileNo + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/save")
    @ApiOperation(value = "Add new lead")
    @PreAuthorize("validatePermission(\"" + MenuConstants.CREATE_LEAD + "\",\"" + MenuConstants.CREATE_ENTERPRISE_LEAD + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> addLead(@RequestBody LeadMasterPojo leadMaster,
                                                       HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			LeadMaster master = new LeadMaster();
//			String buid = leadMaster.getFirstname().toUpperCase();//leadMaster.getBuId();
//			String branch = leadMaster.getFirstname().toUpperCase();
//			String service_type = leadMaster.getFirstname().toUpperCase();
//			String conn_type = leadMaster.getLastname().toUpperCase();
//			String link_type = leadMaster.getUsername().toUpperCase();
//			String circuitname = buid+"_"+branch+"_"+service_type+"_"+conn_type+"_"+link_type;
//			master.setCircuitname(circuitname);
            //String authTokenHeader = request.getHeader("Authorization");
            //getLoggedInUserBuIds(authTokenHeader);
            String leadNo = this.leadMasterService.generateLeadNo();
            leadMaster.setLeadNo(leadNo);
            this.leadMasterService.validateRequest(leadMaster, CommonConstants.OPERATION_ADD);
            if (getLoggedInBuIdList().size() == 1) {
                response.put(LEAD_MASTER, this.leadMasterService.save(leadMaster, (long) getLoggedInMvnoId(), getLoggedInBuIdList().get(0)
                        , (long) getLoggedInUserId()));
            } else if (getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty()) {
                response.put(LEAD_MASTER, this.leadMasterService.save(leadMaster, (long) getLoggedInMvnoId(), null
                        , (long) getLoggedInUserId()));
            } else if (getLoggedInBuIdList().size() > 1) {
                throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
            }

            response.put(SalesCrmsConstants.MESSAGE, "Lead has been added successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Lead" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Lead" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Lead" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "Delete existing lead based on lead id")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> deleteLead(@RequestParam(name = "leadId", required = true) Long leadId,
                                                          HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        LeadMasterPojo leadMasterPojo = this.leadMasterService.findById(leadId);
        try {
            LeadMaster lmaster = (LeadMaster) getEntityForUpdateAndDelete(leadMasterPojo.getId().intValue(), "leadMaster");
            if (leadMasterPojo == null) {
                response.put(SalesCrmsConstants.MESSAGE, "No record found for lead with the given lead Id :" + leadId);
            } else {
                if (getLoggedInBuIdList().size() == 1 || getLoggedInBuIdList().size() == 0) {
                    this.leadMasterService.deleteLeadMaster(leadId);
                } else if (getLoggedInBuIdList().size() > 1) {
                    throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
                }

                response.put(SalesCrmsConstants.MESSAGE, "Lead has been deleted successfully");
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete Lead" + LogConstants.LOG_BY_NAME + leadMasterPojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete Lead" + LogConstants.LOG_BY_NAME + leadMasterPojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete Lead" + LogConstants.LOG_BY_NAME + leadMasterPojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/add/notes")
    @ApiOperation(value = "Add LeadNotes")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> addLeadNotes(@RequestBody LeadNotesDto leadNotes,
                                                            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        try {
            LeadMasterPojo leadMaster = leadMasterService.findById(leadNotes.getLeadMasterId());
            LeadMaster lmaster = (LeadMaster) getEntityForUpdateAndDelete(leadMaster.getId().intValue(), "leadMaster");
            if (getLoggedInBuIdList().size() == 1) {
                response.put(LEAD_NOTES, this.leadMasterService.saveNotes(leadNotes, (long) getLoggedInUserId()));
            } else if (getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty()) {
                response.put(LEAD_NOTES, this.leadMasterService.saveNotes(leadNotes, (long) getLoggedInUserId()));
            } else if (getLoggedInBuIdList().size() > 0) {
                throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
            }
            response.put(SalesCrmsConstants.MESSAGE, "Notes has been added successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create LeadNote" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create LeadNote" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create LeadNote" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Search LeadMaster In System")
    @PostMapping("/search")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> search(@RequestBody PaginationRequestDTO paginationRequestDTO,
                                                      HttpServletRequest request,
                                                      @RequestParam(name = "fromConvertedDate", required = false) String fromConvertedDate,
                                                      @RequestParam(name = "toConvertedDate", required = false) String toConvertedDate) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Page<LeadMasterPojo> page = null;
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            page = this.leadMasterService.search((long) getLoggedInMvnoId(), getLoggedInBuIdList(),
                    paginationRequestDTO, fromConvertedDate, toConvertedDate);
            if (page.isEmpty()) {
                response.put(LEAD_MASTER_LIST, page);
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_MASTER_LIST, page);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search lead using keyword : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search lead using keyword : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search lead using keyword : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PutMapping("/update/{id}")
    @ApiOperation(value = "Update eixsting leadMaster data based on the leadMaster id")
    @PreAuthorize("validatePermission(\"" + MenuConstants.EDIT_LEAD + "\",\"" + MenuConstants.EDIT_ENTERPRISE_LEAD + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateLeadMaster(@PathVariable Long id,
                                                                @RequestBody LeadMasterPojo leadMaster, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        LeadMaster oldleadmaster = null;
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            leadMaster.setId(id);
            LeadMaster lmaster = (LeadMaster) getEntityForUpdateAndDelete(leadMaster.getId().intValue(), "leadMaster");
            oldleadmaster = new LeadMaster(lmaster);

            this.leadMasterService.validateRequest(leadMaster, CommonConstants.OPERATION_UPDATE);
            if (getLoggedInBuIdList().size() == 1) {
                response.put(LEAD_MASTER, this.leadMasterService.update(leadMaster));
            } else if (getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty()) {
                response.put(LEAD_MASTER, this.leadMasterService.update(leadMaster));
            } else if (getLoggedInBuIdList().size() > 0) {
                throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
            }
            LeadMaster newleadmaster = (LeadMaster) getEntityForUpdateAndDelete(leadMaster.getId().intValue(), "leadMaster");
            if (oldleadmaster != null) {
                LOGGER.info("LeadMaster update details" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + UpdateDiffFinder.getUpdatedDiff(oldleadmaster, newleadmaster));
            }
            response.put(SalesCrmsConstants.MESSAGE, "Lead has been updated successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update existing leadMaster" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {

            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update existing leadMaster" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);


        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update existing leadMaster" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/all")
    @ApiOperation(value = "Get list of lead in the system")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        Page<LeadMasterPojo> leadMasterList = null;
        try {

            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
            paginationRequestDTO.setPage(page);
            paginationRequestDTO.setPageSize(pageSize);
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            leadMasterList = this.leadMasterService.findAll((long) getLoggedInMvnoId(),
                    getLoggedInBuIdList(), getLoggedInServiceAreaIds(), paginationRequestDTO, getLoggedInUser().getUserId());
            // Page<LeadMasterPojo> leadMasterList =
            // this.leadMasterService.findAll(getMvnoId(authTokenHeader),getBUId(authTokenHeader),paginationRequestDTO);
            if (leadMasterList.isEmpty()) {
                response.put(LEAD_MASTER_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_MASTER_LIST, leadMasterList.getContent());
            }
            responseCode = SalesCrmsConstants.SUCCESS;
//			logger.info("Fetch Lead By Pagination :  request: { From : {}}; Response : {{}}", MODULE, responseCode,
//			response);
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response, leadMasterList);
    }

    @GetMapping("/findAllLeadAudit/{leadMasterId}")
    @ApiOperation(value = "Get list of leadAudit in the system")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_AUDIT_TRAIL + "\",\"" + MenuConstants.ENTERPRISE_LEAD_AUDIT_TRAIL + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllLeadAuditByLeadId(@PathVariable Long leadMasterId,
                                                                        HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        LeadAudit leadAudit = new LeadAudit();
        try {
            leadAudit.setLeadMasterId(leadMasterId);
            List<LeadAudit> leadAuditList = this.leadAuditRepository.findAll(Example.of(leadAudit));
            if (leadAuditList.isEmpty()) {
                response.put(LEAD_AUDIT_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_AUDIT_LIST, leadAuditList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadAudit" + LogConstants.LOG_BY_NAME + leadAudit.getAuditName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
//			logger.error(
//					"Unable to Fetch LeadAudit By LeadId  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//					MODULE, responseCode, response, e.getStackTrace());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadAudit" + LogConstants.LOG_BY_NAME + leadAudit.getAuditName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadAudit" + LogConstants.LOG_BY_NAME + leadAudit.getAuditName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/StaffUser")
    @ApiOperation(value = "Get list of StaffUser in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllStaffUser(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        long start = System.currentTimeMillis(); // ⏱️ Start time tracker

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            Integer loggedInMvnoId = getLoggedInMvnoId();
            List<Long> loggedInBuIds = getLoggedInBuIdList();
            long t1 = System.currentTimeMillis();
            List<StaffUserDTO> staffUser=getOptimizedStaffUsers(getLoggedInUserPartnerId(),loggedInMvnoId);

//            List<StaffUser> staffUser = this.staffUserRepository.findAllByPartnerid(getLoggedInUserPartnerId()).stream()
//                    .filter(data -> {
//                        try {
//                            Integer mvnoId = data.getMvnoId();
//                            List<Long> buId = getLoggedInBuIdList();
//                            if (buId == null && mvnoId != null) {
//                                return data.getIsDelete() == false && (mvnoId == 1 || mvnoId == getLoggedInMvnoId()); // Return all data when buId is null
//                            } else if (mvnoId != null) {
//                                return data.getIsDelete() == false && (mvnoId == null || mvnoId == 1 || mvnoId == getLoggedInMvnoId() && (buId.containsAll(data.getBusinessUnitNameList().stream().map(i -> i.getId()).collect(Collectors.toList()))));
//                            } else {
//                                return Boolean.parseBoolean(null);
//                            }
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }).collect(Collectors.toList());
            long t2 = System.currentTimeMillis();
            System.out.println("Time taken to fetch from DB: {} ms"+ (t2 - t1));
            if (staffUser == null || staffUser.isEmpty()) {
                response.put(STAFF_USER_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(STAFF_USER_LIST, staffUser);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch StaffUserList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch StaffUserList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch StaffUserList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

public List<StaffUserDTO> getOptimizedStaffUsers(Integer partnerId, Integer mvnoId) {
    // fetch user details
    List<Object[]> rawUsers = staffUserRepository.findMinimalStaffUsers(partnerId, mvnoId);

    // fetch user IDs
    List<Integer> userIds = rawUsers.stream()
            .map(row -> (Integer) row[0])
            .collect(Collectors.toList());

    // fetching BusinessUnitIDs mapping per user
    Map<Integer, List<Long>> buMap = staffUserRepository.findBusinessUnitIdsForStaffUsers(userIds).stream()
            .collect(Collectors.groupingBy(
                    row -> (Integer) row[0],
                    Collectors.mapping(row -> (Long) row[1], Collectors.toList())
            ));

    // returning data
    return rawUsers.stream()
            .map(row -> {
                Integer id = (Integer) row[0];
                String firstname = (String) row[1];
                String lastname = (String) row[2];
                List<Long> buIds = buMap.getOrDefault(id, Collections.emptyList());
                return new StaffUserDTO(id, firstname, lastname, buIds);
            })
            .collect(Collectors.toList());
}


    @PostMapping("/lead/close")
    @ApiOperation(value = "Close Lead")
    @PreAuthorize("validatePermission(\"" + MenuConstants.CLOSE_ENTERPRISE_LEAD + "\",\"" + MenuConstants.CLOSE_LEAD + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> closeLead(@RequestBody LeadRejectDto leadReject,
                                                         HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        String leadname = null;
        try {
            this.leadMasterService.rejectLead(leadReject);
            leadname = leadMasterService.getLeadNameById(leadReject.getLeadMasterId());
            response.put(SalesCrmsConstants.MESSAGE, "Lead has been closed successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
//			logger.info("Lead is closed successfully :  request: { From : {}}; Response : {{}}",
//					request.getHeader("requestFrom"), responseCode);
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "close lead" + LogConstants.LOG_BY_NAME + leadname + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "close lead" + LogConstants.LOG_BY_NAME + leadname + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "close lead" + LogConstants.LOG_BY_NAME + leadname + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PutMapping("/approveLead")
    public ResponseEntity<?> assignCustomerCaf(@Valid @RequestBody LeadMgmtWfDTO pojo, HttpServletRequest req)
            throws Exception {
        Integer responseCode = 0;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());

        try {
            if (pojo != null) {
                response.put("result", leadMasterService.updateCustomerLeadAssignment(pojo));
                response.put("message", "Successfully updated");
                responseCode = SalesCrmsConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "approve Lead" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            }
        } catch (Exception e) {
            responseCode = SalesCrmsConstants.FAIL;
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "approve Lead" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(responseCode, response);
    }

//	@PutMapping("/leadStatus/{leadMasterId}")
//	public ResponseEntity<?> GetLeadStatus(@PathVariable Long leadMasterId,
//										   HttpServletRequest request) throws Exception {
//		Integer responseCode = 0;
//		HashMap<String, Object> response = new HashMap<>();
//		LeadMasterPojo leadMasterPojo = leadMasterService.findById(leadMasterId);
//		try {
//			if (leadMasterId != null) {
//				response.put("result", leadMasterService.getLeadStatus(leadMasterPojo,null));
//				response.put("message", "Successfully updated");
//				responseCode = SalesCrmsConstants.SUCCESS;
//			}
//		} catch (Exception e) {
//			responseCode = SalesCrmsConstants.FAIL;
//			response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
//			return apiResponseController.apiResponse(responseCode, response);
//		}
//		return apiResponseController.apiResponse(responseCode, response);
//	}

    @GetMapping("/findAll/previousVendor")
    @ApiOperation(value = "Get list of previousVendor in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllPreviousVendor(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
//			ClientService clientService = clientServiceSrv.getByName(CommonConstants.PREVIOUS_VENDOR_TYPE);
            ClientService clientService = clientServiceSrv.getByNameAndMvnoId(CommonConstants.PREVIOUS_VENDOR_TYPE, getLoggedInMvnoId().longValue());
            if (clientService == null) {
                response.put(PREVIOUS_VENDOR_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                List<String> previousVendorList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put(PREVIOUS_VENDOR_LIST, previousVendorList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Previous Vendor List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Previous Vendor List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Previous Vendor List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/servicerType")
    @ApiOperation(value = "Get list of servicerType in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllServicerType(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            ClientService clientService = clientServiceSrv.getByNameAndMvnoId(CommonConstants.SERVICER_TYPE, getLoggedInMvnoId().longValue());
            if (clientService == null) {
                response.put(SERVICER_TYPE_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                List<String> servicerTypeList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put(SERVICER_TYPE_LIST, servicerTypeList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Servicer TypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Servicer TypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Servicer TypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/lead/reopen/{leadId}")
    @ApiOperation(value = "Close Lead")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> reOpenLead(@PathVariable Long leadId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        String leadname = null;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            this.leadMasterService.reopenLead(leadId, (long) getLoggedInUserId());
            leadname = leadMasterService.getLeadNameById(leadId);
            response.put(SalesCrmsConstants.MESSAGE, "Lead has been reOpen successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "reopen lead" + LogConstants.LOG_BY_NAME + leadname + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "reopen lead" + LogConstants.LOG_BY_NAME + leadname + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "reopen lead" + LogConstants.LOG_BY_NAME + leadname + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/convertCustomerCaf")
    @ApiOperation(value = "Convert Lead to Customer CAF")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> convertCustomerCaf(@RequestBody LeadMasterPojo leadMaster,
                                                                  HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Convert");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            this.leadMasterService.update(leadMaster);
            LeadMgmtWfDTO leadMgmtWfDTO = new LeadMgmtWfDTO();
            leadMgmtWfDTO.setId(leadMaster.getId());
            leadMgmtWfDTO.setBuId(leadMaster.getApproveBuId());
            leadMgmtWfDTO.setCurrentLoggedInStaffId(leadMaster.getApproveCurrentLoggedInStaffId());
            leadMgmtWfDTO.setFirstname(leadMaster.getApproveFirstname());
            leadMgmtWfDTO.setFlag(leadMaster.getFlag());
            leadMgmtWfDTO.setMvnoId(leadMaster.getApproveMvnoId());
            leadMgmtWfDTO.setNextApproveStaffId(leadMaster.getNextApproveStaffId());
            leadMgmtWfDTO.setRemark(leadMaster.getApproveRemark());
            leadMgmtWfDTO.setServiceareaid(leadMaster.getApproveServiceareaid());
            leadMgmtWfDTO.setNextTeamMappingId(leadMaster.getNextTeamMappingId());
            leadMgmtWfDTO.setStatus(leadMaster.getApproveStatus());
            leadMgmtWfDTO.setUsername(leadMaster.getApproveUsername());
            ResponseEntity<?> assignLeadResponse = this.assignCustomerCaf(leadMgmtWfDTO, request);
            HttpStatus statusCode = assignLeadResponse.getStatusCode();
            if (statusCode.is2xxSuccessful()) {
                // send customer entry
                this.leadMasterService.convertLeadToCustomerCafAndSendToCustomerCafEntry(leadMaster);
            }
            response.put(SalesCrmsConstants.MESSAGE, "Convert Customer CAF successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
//			logger.info("Convert Customer CAF successfully :  request: { From : {}}; Response : {{}}",
//					request.getHeader("requestFrom"), responseCode);
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Convert Lead to Customer CAF" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Convert Lead to Customer CAF " + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Convert Lead to Customer CAF" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/Branch")
    @ApiOperation(value = "Get list of StaffUser in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllBranch(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            List<Branch> branchList = this.branchRepository.findAll().stream()
                    .filter(data -> {
                        try {
                            Integer mvnoId = data.getMvnoId();
                            //List<Long> buId = getBuFromCurrentStaff(authTokenHeader);
                            if (mvnoId != null) {
                                return data.getIsDeleted() == false && (mvnoId == 1 || mvnoId == getLoggedInMvnoId()); // Return all data when buId is null
                            } else if (mvnoId != null) {
                                return data.getIsDeleted() == false && (mvnoId == 1 || mvnoId == getLoggedInMvnoId());
                            } else {
                                return Boolean.parseBoolean(null);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());
            if (branchList == null || branchList.isEmpty()) {
                response.put(BRANCH_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(BRANCH_LIST, branchList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
//			logger.info("Fetch BranchList :  request: { From : {}}; Response : {{}}", MODULE, responseCode,
//					response);
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch BranchList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());

            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch BranchList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch BranchList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/ServiceArea")
    @ApiOperation(value = "Get list of ServiceArea in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllServiceArea(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            List<ServiceArea> serviceAreaList = this.serviceAreaRepository.findAll().stream()
                    .filter(data -> {
                        try {
                            Integer mvnoId = data.getMvnoId();
//							List<Long> buId = getBuFromCurrentStaff(authTokenHeader);
                            if ((getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty() || getLoggedInBuIdList() == null) && (mvnoId != null)) {
                                return data.getIsDeleted() == false && (mvnoId == 1 || mvnoId == getLoggedInMvnoId()); // Return all data when buId is null
                            } else if (mvnoId != null) {
                                return data.getIsDeleted() == false && (mvnoId == 1 || mvnoId == getLoggedInMvnoId()) && (getLoggedInBuIdList() == null || getLoggedInBuIdList().isEmpty() || getLoggedInBuIdList().contains(data.getBuId()));
                            } else {
                                return Boolean.parseBoolean(null);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());
            if (serviceAreaList == null || serviceAreaList.isEmpty()) {
                response.put(SERVICE_AREA_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(SERVICE_AREA_LIST, serviceAreaList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch ServiceAreaList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch ServiceAreaList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch ServiceAreaList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/Partner")
    @ApiOperation(value = "Get list of Partner in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllPartner(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            List<Partner> partnerList = this.partnerRepository.findAll().stream()
                    .filter(data -> {
                        try {
//							Integer mvnoId = data.getMvnoId();
//							List<Long> buId = getBuFromCurrentStaff(authTokenHeader);
                            if ((getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty() || getLoggedInBuIdList() == null) && (getLoggedInMvnoId() != null)) {
                                return data.getIsDelete() == false && (getLoggedInMvnoId() == 1 || getLoggedInMvnoId() == getMvnoId(authTokenHeader).intValue()); // Return all data when buId is null
                            } else if (getLoggedInMvnoId() != null) {
                                return data.getIsDelete() == false && (getLoggedInMvnoId() == 1 || getLoggedInMvnoId() == getMvnoId(authTokenHeader).intValue()) && (getLoggedInBuIdList().contains(data.getBuId()));
                            } else {
                                return Boolean.parseBoolean(null);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());
            if (partnerList == null || partnerList.isEmpty()) {
                response.put(PARTNER_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(PARTNER_LIST, partnerList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PartnerList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PartnerList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PartnerList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/Customers")
    @ApiOperation(value = "Get list of Customers in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllCustomers(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            Integer loggedInMvnoId = getLoggedInMvnoId();
            List<Long> buIdLongList = getLoggedInBuIdList();
            List<Integer> buIdIntList = buIdLongList.stream()
                    .map(Long::intValue)
                    .collect(Collectors.toList());
            boolean hasBu = buIdIntList != null && !buIdIntList.isEmpty();

            List<CustomerBasicDto> customerList = customersRepository.findCustomersByMvnoAndBu(
                    loggedInMvnoId,
                    hasBu ? buIdIntList : Arrays.asList(-1),
                    hasBu
            );
            if (customerList == null || customerList.isEmpty()) {
                response.put(CUSTOMERS_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(CUSTOMERS_LIST, customerList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch CustomerList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch CustomerList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch CustomerList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/createLeadByCampaignManager")
    @ApiOperation(value = "Add new lead By CampaignManager")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> addLeadByCampaignManager(@RequestBody LeadMasterPojo leadMaster,
                                                                        HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {

            this.leadMasterService.validateRequest(leadMaster, CommonConstants.OPERATION_ADD);
            if (getLoggedInBuIdList().size() == 1) {
                response.put(LEAD_MASTER, this.leadMasterService.saveCampaignManager(leadMaster, (long) getLoggedInMvnoId(),
                        getLoggedInBuIdList().get(0), (long) getLoggedInUserId()));
            } else if (getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty()) {
                response.put(LEAD_MASTER, this.leadMasterService.saveCampaignManager(leadMaster, (long) getLoggedInMvnoId(),
                        null, (long) getLoggedInUserId()));
            } else if (getLoggedInBuIdList().size() > 0) {
                throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
            }

            response.put(SalesCrmsConstants.MESSAGE, "CampaignManager Lead has been added successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create lead by CampaignManager" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create lead by CampaignManager" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create lead by CampaignManager" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PutMapping("/updateLeadByCampaignManager/{id}")
    @ApiOperation(value = "Update eixsting leadMasterByCamaignManager data based on the leadMaster id")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateLeadMasterByCampaignManager(@PathVariable Long id,
                                                                                 @RequestBody LeadMasterPojo leadMaster, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        LeadMaster oldleadmaster = null;
        try {
            leadMaster.setId(id);
            LeadMaster lmaster = (LeadMaster) getEntityForUpdateAndDelete(leadMaster.getId().intValue(), "leadMaster");
            oldleadmaster = new LeadMaster(lmaster);
            this.leadMasterService.validateRequest(leadMaster, CommonConstants.OPERATION_UPDATE);
            if (getLoggedInBuIdList().size() == 1) {
                response.put(LEAD_MASTER, this.leadMasterService.update(leadMaster));
            } else if (getLoggedInBuIdList().size() == 0 || getLoggedInBuIdList().isEmpty()) {
                response.put(LEAD_MASTER, this.leadMasterService.update(leadMaster));
            } else if (getLoggedInBuIdList().size() > 1) {
                throw new CustomValidationException(APIConstants.EXPACTIATION_FAILED, "You are not allowed to perform this action, Please contact your system administrator", null);
            }
            LeadMaster newleadmaster = (LeadMaster) getEntityForUpdateAndDelete(leadMaster.getId().intValue(), "leadMaster");
            if (oldleadmaster != null) {
                LOGGER.info("LeadMaster update by CamaignManager details: " + UpdateDiffFinder.getUpdatedDiff(oldleadmaster, newleadmaster));
            }
            response.put(SalesCrmsConstants.MESSAGE, "CamaignManager Lead has been updated successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update existing leadMaster by CamaignManager" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update existing leadMaster by CamaignManager" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update existing leadMaster by CamaignManager" + LogConstants.LOG_BY_NAME + leadMaster.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @DeleteMapping("/deleteLeadByCampaignManager")
    @ApiOperation(value = "Delete existing CampaignManagerlead based on lead id")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> deleteLeadByCampaignManager(
            @RequestParam(name = "leadId", required = true) Long leadId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());

        Integer responseCode = SalesCrmsConstants.FAIL;
        LeadMasterPojo leadMasterPojo = this.leadMasterService.findById(leadId);
        try {
            if (leadMasterPojo == null) {
                response.put(SalesCrmsConstants.MESSAGE,
                        "No record found for CampaignManager lead with the given lead Id :" + leadId);
            } else {
                this.leadMasterService.deleteLeadMaster(leadId);
                response.put(SalesCrmsConstants.MESSAGE, "CampaignManager Lead has been deleted successfully");
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete LeadByCampaignManager" + LogConstants.LOG_BY_NAME + leadMasterPojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete LeadByCampaignManager" + LogConstants.LOG_BY_NAME + leadMasterPojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete LeadByCampaignManager" + LogConstants.LOG_BY_NAME + leadMasterPojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/leadType")
    @ApiOperation(value = "Get list of leadType in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllLeadType(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            //String authTokenHeader = request.getHeader("Authorization");
            ClientService clientService = clientServiceSrv.getByNameAndMvnoId(CommonConstants.LEAD_TYPE, getLoggedInMvnoId().longValue());
            if (clientService == null) {
                response.put(LEAD_TYPE_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                List<String> leadTypeList = new ArrayList<String>(Arrays.asList(clientService.getValue().split(" , ")));
                response.put(LEAD_TYPE_LIST, leadTypeList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadType" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadType" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch LeadType" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/leadCategory")
    @ApiOperation(value = "Get list of leadCategory in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllLeadCategory(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        MDC.put(SalesCrmsConstants.TYPE, SalesCrmsConstants.TYPE_FETCH);
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            //String authTokenHeader = request.getHeader("Authorization");
            ClientService clientService = clientServiceSrv.getByNameAndMvnoId(CommonConstants.LEAD_CATEGORY, getLoggedInMvnoId().longValue());
            if (clientService == null) {
                response.put(LEAD_CATEGORY_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                List<String> leadCategoryList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put(LEAD_CATEGORY_LIST, leadCategoryList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead CategoryList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead CategoryList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead CategoryList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/feasibility")
    @ApiOperation(value = "Get list of feasibility in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllFeasibility(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            //String authTokenHeader = request.getHeader("Authorization");
            ClientService clientService = clientServiceSrv.getByNameAndMvnoId(CommonConstants.FEASIBILITY, getLoggedInMvnoId().longValue());
            if (clientService == null) {
                response.put(FEASIBILITY_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                List<String> feasibilityList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put(FEASIBILITY_LIST, feasibilityList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Feasibility List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());

            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Feasibility List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Feasibility List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/planType")
    @ApiOperation(value = "Get list of planType in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllPlanType(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            //String authTokenHeader = request.getHeader("Authorization");
            ClientService clientService = clientServiceSrv.getByNameAndMvnoId(CommonConstants.PLAN_TYPE, getLoggedInMvnoId().longValue());
            if (clientService == null) {
                response.put(PLAN_TYPE_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                List<String> planTypeList = new ArrayList<String>(Arrays.asList(clientService.getValue().split(" , ")));
                response.put(PLAN_TYPE_LIST, planTypeList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PlanType List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PlanType List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PlanType List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/generateLeadNo")
    @ApiOperation(value = "Generate LeadNo")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> generateLeadNo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        try {
            String leadNo = this.leadMasterService.generateLeadNo();
            response.put(LEAD_NO, leadNo);
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create LeadNo" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create LeadNo" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create LeadNo" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Get list of LeadNote by leadid in the system")
    @GetMapping("/findAllLeadNoteWithPagination/{id}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_LEAD_NOTES + "\",\"" + MenuConstants.ENTERPRISE_LEAD_NOTES + "\")")
    public ResponseEntity<Map<String, Object>> findAllLeadNoteWithPagination(@PathVariable Long id,
                                                                             @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
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
            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
            paginationRequestDTO.setPage(page);
            paginationRequestDTO.setPageSize(pageSize);
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            Page<LeadNotesDto> leadDocDetailsList = this.leadMasterService
                    .findAllLeadNoteWithPagination(paginationRequestDTO, id);
            if (leadDocDetailsList == null || leadDocDetailsList.isEmpty()) {
                response.put(LEAD_NOTES_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
                LOGGER.debug(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of LeadNote by leadid : " + id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            } else {
                LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of LeadNote by leadid : " + id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

                response.put(LEAD_NOTES_LIST, leadDocDetailsList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of LeadNote by leadid : " + id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of LeadNote by leadid : " + id + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/assignworkflow/{leadId}")
    @ApiOperation(value = "Assign Workflow By leadId")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> assignWorkflowByLeadId(@PathVariable Long leadId,
                                                                      HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            response.put(LEAD_MASTER, this.leadMasterService.assignWorkFlow(leadId, getStaffId(authTokenHeader)));
            response.put(SalesCrmsConstants.MESSAGE, "Assign Workflow is successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Assign Workflow" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Assign Workflow" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Assign Workflow" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/popManagement")
    @ApiOperation(value = "Get list of PopManagement in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllPopManagement(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            //String authTokenHeader = request.getHeader("Authorization");
            List<PopManagement> popManagementList = this.popManagementRepository.findAll().stream()
                    .filter(data -> {
                        try {
                            Integer mvnoId = data.getMvnoId();
                            return data.getIsDeleted() == false && (mvnoId == null || mvnoId == 1 || mvnoId == getLoggedInMvnoId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList());
            if (popManagementList == null || popManagementList.isEmpty()) {
                response.put(POP_MANAGEMENT_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(POP_MANAGEMENT_LIST, popManagementList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PopManagement List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PopManagement List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch PopManagement List" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/leadOriginTypes")
    @ApiOperation(value = "Get list of leadOriginTypes in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllLeadOriginTypes(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            ClientService clientService = clientServiceSrv.getByNameAndMvnoId(CommonConstants.LEAD_ORIGIN_TYPES, getLoggedInMvnoId().longValue());
            if (clientService == null) {
                response.put(LEAD_ORIGIN_TYPE_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                List<String> leadOriginTypesList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put(LEAD_ORIGIN_TYPE_LIST, leadOriginTypesList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead OriginTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead OriginTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead OriginTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/requireServiceTypes")
    @ApiOperation(value = "Get list of RequireServiceTypes in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllRequireServiceTypes(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            ClientService clientService = clientServiceSrv.getByNameAndMvnoId(CommonConstants.REQUIRE_SERVICE_TYPES, getLoggedInMvnoId().longValue());
            if (clientService == null) {
                response.put(REQUIRE_SERVICE_TYPE_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                List<String> requireServiceTypeList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put(REQUIRE_SERVICE_TYPE_LIST, requireServiceTypeList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RequireServiceTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RequireServiceTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch RequireServiceTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAllByCurrentUser")
    @ApiOperation(value = "Get list of lead By Current User in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllByCurrentUser(
            @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
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
//			String authTokenHeader = request.getHeader("Authorization");
            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
            paginationRequestDTO.setPage(page);
            paginationRequestDTO.setPageSize(pageSize);
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            Page<LeadMasterPojo> leadMasterList = this.leadMasterService.findByCurrentUser(paginationRequestDTO,
                    (long) getLoggedInUserId(), (long) getLoggedInMvnoId(), getLoggedInBuIdList());
            if (leadMasterList.isEmpty()) {
                response.put(LEAD_MASTER_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_MASTER_LIST, leadMasterList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead By Current User" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead By Current User" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead By Current User" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAllByCurrentUserTeamLead")
    @ApiOperation(value = "Get list of lead By Current User Team Lead in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllByCurrentUserTeamLead(
            @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());

        try {
//			String authTokenHeader = request.getHeader("Authorization");
            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
            paginationRequestDTO.setPage(page);
            paginationRequestDTO.setPageSize(pageSize);
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            Page<LeadMasterPojo> leadMasterList = this.leadMasterService.findByCurrentUserTeamLeadList(
                    paginationRequestDTO, (long) getLoggedInUserId(), (long) getLoggedInMvnoId(),
                    getLoggedInBuIdList());
            if (leadMasterList.isEmpty()) {
                response.put(LEAD_MASTER_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_MASTER_LIST, leadMasterList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All By CurrentUserTeamLead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All By CurrentUserTeamLead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All By CurrentUserTeamLead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/countByCurrentUser")
    public ResponseEntity<Map<String, Object>> getLeadCount(@RequestParam(name = "staffId",required = true) Integer staffId,HttpServletRequest req) {
        Map<String, Object> response = new HashMap<>();
        try {
            int count = leadMasterService.getLeadCountForCurrentUser(staffId);
            response.put("leadCount", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/generateCafNo")
    @ApiOperation(value = "Generate CafNo")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> generateCafNo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        try {
            String cafNo = this.leadMasterService.generateCafNo();
            response.put(CAF_NO, cafNo);
            responseCode = SalesCrmsConstants.SUCCESS;
//			logger.info("Fetch Caf No :  request: { From : {}}; Response : {{}}", MODULE, responseCode, response);
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Caf No" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Caf No" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Caf No" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findAll/leadcustomergendertype")
    @ApiOperation(value = "Get list of LeadCustomerGenderType in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllLeadCustomerGenderType(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            ClientService clientService = clientServiceSrv.getByNameAndMvnoId(CommonConstants.LEAD_CUSTOMER_GENDER_TYPE, getLoggedInMvnoId().longValue());
            if (clientService == null) {
                response.put(LEAD_CUSTOMER_GENDER_TYPE_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                List<String> leadCustomerGenderTypeList = new ArrayList<String>(
                        Arrays.asList(clientService.getValue().split(" , ")));
                response.put(LEAD_CUSTOMER_GENDER_TYPE_LIST, leadCustomerGenderTypeList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead Customer GenderTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead Customer GenderTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead Customer GenderTypeList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findByusername")
    @ApiOperation(value = "Get lead detail based on the given lead mobileno")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> username(@RequestParam("username") String username,
                                                        HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            List<LeadMasterPojo> leadMasterPojo = this.leadMasterService.findByusername((long) getLoggedInMvnoId(),
                    !getLoggedInBuIdList().isEmpty() ? getLoggedInBuIdList().get(0) : null, username);
            if (leadMasterPojo == null) {
                response.put(LEAD_MASTER_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE,
                        "No record found for lead with the given lead username :" + username);
            } else {
                response.put(LEAD_MASTER_LIST, leadMasterPojo);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead" + LogConstants.LOG_BY_NAME + username + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead" + LogConstants.LOG_BY_NAME + username + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Lead" + LogConstants.LOG_BY_NAME + username + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/addNewService")
    @PreAuthorize("validatePermission(\"" + MenuConstants.ENTERPRISE_CIRCUIT_CREATE + "\")")
    public ResponseEntity<?> addNewService(@Valid @RequestBody LeadMasterPojo pojo,
                                           @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req)
            throws Exception {

        HashMap<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        String authTokenHeader = req.getHeader("Authorization");
        try {

            pojo = this.leadMasterService.newService(pojo);

            response.put("customer", pojo);
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create enterprise circuit" + LogConstants.LOG_BY_NAME + pojo.getCircuitname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create enterprise circuit" + LogConstants.LOG_BY_NAME + pojo.getCircuitname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

            responseCode = ce.getErrCode();
            response.put(SalesCrmsConstants.ERROR_TAG, ce.getMessage());
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create enterprise circuit" + LogConstants.LOG_BY_NAME + pojo.getCircuitname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
            responseCode = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(SalesCrmsConstants.ERROR_TAG, exception.getMessage());
        } catch (Exception ex) {
//			ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create enterprise circuit" + LogConstants.LOG_BY_NAME + pojo.getFirstname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

            ex.printStackTrace();
            responseCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(SalesCrmsConstants.ERROR_TAG, ex.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findCircuitDetailsByLeadId")
    @ApiOperation(value = "Get circuit detail based on the given lead id")
    @PreAuthorize("validatePermission(\"" + MenuConstants.ENTERPRISE_CIRCUIT + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findCircuitDetailsByLeadId(@RequestParam("leadId") Long leadId,
                                                                          HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        try {
            List<LeadServiceMapping> leadServiceMappingList = this.leadMasterService.findCircuitDetailsByLeadId(leadId);
            if (leadServiceMappingList == null && leadServiceMappingList.isEmpty()) {
                response.put(SalesCrmsConstants.MESSAGE,
                        "No record found for lead service mapping with the given lead Id :" + leadId);
            } else {
                response.put(LEAD_SERVICE_MAPPING_LIST, leadServiceMappingList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch CircuitDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch CircuitDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch CircuitDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findCPRForLeadToCAFConvertionForEnterpriseCustomer")
    @ApiOperation(value = "Get final CPR details based on the given lead id for CAF convertion")
    public ResponseEntity<Map<String, Object>> findCPRForLeadToCAFConvertionForEnterpriseCustomer(
            @RequestParam("leadId") Long leadId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        try {
            List<CustPlanMapppingPojo> leadServiceMappingList = this.leadMasterService
                    .findFinalServicesForLeadToCAFConvertion(leadId);
            if (leadServiceMappingList == null && leadServiceMappingList.isEmpty()) {
                response.put(SalesCrmsConstants.MESSAGE,
                        "No record found for lead service mapping with the given lead Id :" + leadId);
            } else {
                response.put(PLAN_MAPPING_LIST, leadServiceMappingList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch final CPR details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch final CPR details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch final CPR details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findLeadServiceMappingById")
    @ApiOperation(value = "Get circuit detail based on the given id")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findLeadServiceMappingById(
            @RequestParam("leadServiceMappingId") Long leadServiceMappingId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        try {
            LeadMasterPojo leadMasterPojo = this.leadMasterService.findLeadServiceMappingById(leadServiceMappingId);
            if (leadMasterPojo == null) {
                response.put(SalesCrmsConstants.MESSAGE,
                        "No record found for lead service mapping with the given lead Id :" + leadServiceMappingId);
            } else {
                response.put(LEAD_MASTER, leadMasterPojo);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Lead ServiceMapping" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Lead ServiceMapping" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Lead ServiceMapping" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/updateLeadService")
    @PreAuthorize("validatePermission(\"" + MenuConstants.ENTERPRISE_CIRCUIT_EDIT + "\")")
    public ResponseEntity<?> updateLeadService(@Valid @RequestBody LeadMasterPojo pojo,
                                               @RequestParam Integer leadMasterServiceId,
                                               @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req)
            throws Exception {

        HashMap<String, Object> response = new HashMap<>();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        String authTokenHeader = req.getHeader("Authorization");
        try {

            pojo = this.leadMasterService.updateLeadService(pojo, leadMasterServiceId.longValue());

            response.put("customer", pojo);
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update enterprise circuite" + LogConstants.LOG_BY_NAME + pojo.getCircuitname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update enterprise circuite" + LogConstants.LOG_BY_NAME + pojo.getCircuitname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

            responseCode = ce.getErrCode();
            response.put(SalesCrmsConstants.ERROR_TAG, ce.getMessage());
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update enterprise circuite" + LogConstants.LOG_BY_NAME + pojo.getCircuitname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
            responseCode = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(SalesCrmsConstants.ERROR_TAG, exception.getMessage());
        } catch (Exception ex) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update enterprise circuite" + LogConstants.LOG_BY_NAME + pojo.getCircuitname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

            ex.printStackTrace();
            responseCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(SalesCrmsConstants.ERROR_TAG, ex.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/findByBuids")
    @ApiOperation(value = "Get list of lead by given BUIDs")
    @PreAuthorize("validatePermission(\"" + MenuConstants.ENTERPRISE_LEAD + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAll(@RequestBody SearchLeadByBuidDTO searchLeadByBuidDTO,
                                                       HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<LeadMasterPojo> leadMasterList = this.leadMasterService.findAllByBuidList(searchLeadByBuidDTO);
            if (leadMasterList.isEmpty()) {
                response.put(LEAD_MASTER_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_MASTER_LIST, leadMasterList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch list of lead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch list of lead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch list of lead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Search LeadMaster In System For Enterprise staff user")
    @PostMapping("/enterpriseSearch")
    @PreAuthorize("validatePermission(\"" + MenuConstants.ENTERPRISE_LEAD + "\")")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> enterpriseSearch(@RequestBody PaginationRequestDTO paginationRequestDTO,
                                                                HttpServletRequest request,
                                                                @RequestParam(name = "fromConvertedDate", required = false) String fromConvertedDate,
                                                                @RequestParam(name = "toConvertedDate", required = false) String toConvertedDate) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Page<LeadMasterPojo> page = null;
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authTokenHeader = request.getHeader("Authorization");
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
//			page = this.leadMasterService.enterpriseSearch(getMvnoId(authTokenHeader),paginationRequestDTO,fromConvertedDate,toConvertedDate);
            page = this.leadMasterService.enterpriseSearch(2L, paginationRequestDTO, fromConvertedDate,
                    toConvertedDate);
            if (page.isEmpty()) {
                response.put(LEAD_MASTER_LIST, page);
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_MASTER_LIST, page);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search lead for : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
//			logger.error(
//					"Unable to Search lead with " + paginationRequestDTO.getFilters().get(0).getFilterValue()
//							+ " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//					MODULE, responseCode, response, e.getStackTrace());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search lead for : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
//			logger.error(
//					"Unable to Search lead with " + paginationRequestDTO.getFilters().get(0).getFilterValue()
//							+ " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",
//					MODULE, responseCode, response, e.getStackTrace());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search lead for : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Verify only those plans whose quotations are approved by end user")
    @PutMapping("/verifyPlansWithQuotationApproval")
    public ResponseEntity<Map<String, Object>> verifyPlansWithQuotationApproval(@RequestBody LeadMasterPojo leadMasterPojo, HttpServletRequest req) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<CustPlanMapppingPojo> planMappingList = leadMasterService.verifyPlansWithQuotationApproval(leadMasterPojo);
            if (planMappingList != null && planMappingList.size() > 0) {
                response.put(Final_PLAN_MAPPING_LIST, planMappingList);
                response.put(SalesCrmsConstants.MESSAGE, "Final plan mapping list has been fetched successfully");
                responseCode = SalesCrmsConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Verify plans whose quotations are approved" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            } else {
                response.put(Final_PLAN_MAPPING_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
                responseCode = SalesCrmsConstants.FAIL;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Verify plans whose quotations are approved" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }
        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Verify plans whose quotations are approved" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

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

    public int getLoggedInUserPartnerId() {
        int partnerId = 1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            e.printStackTrace();
            partnerId = 1;
        }
        return partnerId;
    }
}
