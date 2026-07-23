package com.savbill.partnermanagement.modules.partner.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.partnermanagement.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.partnermanagement.auditLog.service.AuditLogService;
import com.savbill.partnermanagement.constants.*;
import com.savbill.partnermanagement.constants.*;
import com.savbill.partnermanagement.core.constants.Constants;
import com.savbill.partnermanagement.core.constants.UrlConstants;
import com.savbill.partnermanagement.core.dto.*;
import com.savbill.partnermanagement.core.dto.*;
import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.partnermanagement.modules.partner.dto.PartnerHierarchy;
import com.savbill.partnermanagement.modules.partner.dto.PartnerPojo;
import com.savbill.partnermanagement.modules.partner.entity.Partner;
import com.savbill.partnermanagement.modules.partner.entity.PriceBook1;
import com.savbill.partnermanagement.modules.partner.repository.PartnerRepository;
import com.savbill.partnermanagement.modules.partner.repository.PriceBookRepository1;
import com.savbill.partnermanagement.modules.partner.service.PartnerService;
import com.savbill.partnermanagement.security.dto.LoggedInUser;
import com.savbill.partnermanagement.security.spring.MessagesPropertyConfig;
import com.savbill.partnermanagement.security.spring.SpringContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PARTNER_CONTROLLER)
@Api(value = "PartnerController", description = "REST APIs related to Partner Entity!!!!", tags = "partner_controller")
public class PartnerController {

    private static String MODULE = " [PartnerController] ";


    @Autowired
    PartnerService partnerService;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private Tracer tracer;

    @Autowired
    private PriceBookRepository1 priceBookRepository1;

    private final Logger log = Logger.getLogger(PartnerController.class);

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;


    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER + "\")")
    @PostMapping("/partner/search")
    public ResponseEntity<?> searchPartner(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<Partner> partnerList = null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            requestDTO = (requestDTO);
            ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
            if (validationData.isValid()) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response);
            }
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            partnerList = partnerService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
            Integer Response = 0;
            if (partnerList.isEmpty()) {
                Response = APIConstants.NULL_VALUE;
                response.put(APIConstants.MESSAGE, "No Records Found!");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword :  " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(Response, response);

            }
            if (null != partnerList && 0 < partnerList.getSize()) {
                response.put("partnerlist", partnerService.convertResponseModelIntoPojo(partnerList.getContent()));
            } else {
                response.put("partnerlist", new ArrayList<>());
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (RuntimeException re) {
            re.printStackTrace();
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, re.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + re.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, partnerList);
    }

    @GetMapping("/partner/all")
    public ResponseEntity<?> getAllPartnerListWithoutPagination(HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
//            List<Partner> partnerList = partnerService.getAllPartnersAsPojo();
            response.put("partnerlist", partnerService.getAllPartnersAsPojo());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/partner/type/{type}")
    public ResponseEntity<?> getAllPartnerListWithoutPaginationByType(@PathVariable String type, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            List<Partner> partnerList = partnerService.getAllEntities(type);
            response.put("partnerlist", partnerService.convertResponseModelIntoPojo(partnerList));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch partner using type" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch partner using typer" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch partner using type" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PARTNER_ALL + "\",\"" + AclConstants.OPERATION_PARTNER_VIEW + "\")")
    @GetMapping("/partner/allActive")
    public ResponseEntity<?> getAllActivePartnerListWithoutPagination(HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            List<Partner> partnerList = partnerService.getAllActiveEntities();
            response.put("partnerlist", partnerService.convertResponseModelIntoPojo(partnerList));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Active partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Active partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Active partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER + "\")")
    @GetMapping("/partner/{id}")
    public ResponseEntity<?> getPartnerById(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {

            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            PartnerPojo partner = partnerService.getPartnerPojoById(id);
            if (partner == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All  partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                response.put("partnerlist", partner);
                RESP_CODE = APIConstants.SUCCESS;

                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All  partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PARTNER, AclConstants.OPERATION_PARTNER_VIEW, req.getRemoteAddr(), null, partner.getId().longValue(), partner.getName());
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All  partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All  partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_CREATE + "\")")
    @PostMapping("/partner")
    public ResponseEntity<?> createPartner(@RequestBody PartnerPojo pojo, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            partnerService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            boolean flag = partnerService.duplicateVerifyAtSave(pojo.getName());
            boolean isSameStaff = partnerService.isSameStaff(pojo.getName());
            if (flag) {
                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                    throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
                }
                pojo = partnerService.save(pojo, req);
                if (pojo.getPartnerType().equals(CommonConstants.PARTNER_TYPE_LCO))
                    partnerService.createInvoiceFunctionForPartner(pojo);
                partnerService.sendCreateDataShared(pojo.getId(), pojo, CommonConstants.OPERATION_ADD);
                response.put("partner", pojo);
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create partner" + LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else {
                String errMsg;
                if (!isSameStaff) {
                    errMsg = MessageConstants.PARTNER_NAME_EXITS_DIFF_STAFF;
                } else {
                    errMsg = MessageConstants.PARTNER_NAME_EXITS;
                }
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, errMsg);
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create partner" + LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Partner with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response, null);
            }
            //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PARTNER, AclConstants.OPERATION_PARTNER_ADD, req.getRemoteAddr(), "", pojo.getId().longValue(), pojo.getName());
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create partner" + LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create partner" + LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_EDIT + "\",\"" + MenuConstants.PARTNER_SHIFT_PARTNER + "\")")
    @PutMapping("/partner/{id}")
    public ResponseEntity<?> updatePartner(@Valid @RequestBody PartnerPojo pojo, @PathVariable Integer id, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            Partner oldpartner = partnerService.get(id);
            pojo.setId(id);
            partnerService.getEntityForUpdateAndDelete(id);
            partnerService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            if (pojo.getCommtype().equalsIgnoreCase(CommonConstants.PRICE_BOOK)) {
                pojo.setCommrelvalue(null);
            }
            boolean flag = partnerService.duplicateVerifyAtEdit(pojo.getName(), pojo.getId());
            boolean isSameStaff = partnerService.isSameStaff(pojo.getName());
            Long oldPriceBookId= oldpartner.getPriceBookId().getId();
            Long newPriceBookId=pojo.getPricebookId();

            if (flag) {
                pojo = partnerService.save(pojo, req);
                partnerService.sendCreateDataShared(pojo.getId(), pojo, CommonConstants.OPERATION_UPDATE);
                if(oldPriceBookId!=null && newPriceBookId!=null && oldPriceBookId.longValue()!=newPriceBookId.longValue())
                {
                    List<Partner> childPartners=partnerRepository.getAllChildPartners(pojo.getId());
                    PriceBook1 priceBook1=priceBookRepository1.findById(pojo.getPricebookId()).orElse(null);
                    if(!childPartners.isEmpty() && priceBook1!=null)
                    {
                        childPartners.forEach(childPartner->{
                            childPartner.setPriceBookId(priceBook1);
                            partnerRepository.save(childPartner);
                            try {
                                PartnerPojo partnerPojo=partnerService.convertPartnerModelToPartnerPojo(childPartner);
                                partnerService.sendCreateDataShared(partnerPojo.getId(), partnerPojo, CommonConstants.OPERATION_UPDATE);
                                partnerService.updateChildPartnerBundle(childPartner,priceBook1);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                }

                response.put("partner", pojo);
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                String errMsg;
                if (!isSameStaff) {
                    errMsg = MessageConstants.PARTNER_NAME_EXITS_DIFF_STAFF;
                } else {
                    errMsg = MessageConstants.PARTNER_NAME_EXITS;
                }
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, errMsg);
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Partner for partner" + LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response, null);
            }
            //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PARTNER, AclConstants.OPERATION_PARTNER_EDIT, req.getRemoteAddr(),null, pojo.getId().longValue(), pojo.getName());
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Partner for partner" + LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Partner for partner" + LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER_DELETE + "\")")
    @DeleteMapping("/partner/{id}")
    public ResponseEntity<?> deletePartner(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        PartnerPojo partnerPojo = new PartnerPojo();
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            partnerService.getEntityForUpdateAndDelete(id);
            if(partnerService.canPartnerDelete(id)) {
                throw new Exception("Active Partner Can not delete");
            }


            Partner partner = partnerService.get(id);
            if (partner != null && partner.getPartnerType().equalsIgnoreCase("Franchise") && partnerService.isPartnerUsedAsParentPartner(partner)) {
                throw new Exception("Partner Used As Parent Partner by Child Partner");
            }
            partner.setIsDelete(true);
            partnerService.deletePartner(id);
//            createDataSharedService.updateEntityDataForAllMicroService(partner);
            partnerService.sendDeletePartnerDataShared(id);
            response.put(CommonConstants.RESPONSE_MESSAGE, "Partner Deleted Successfully");
            RESP_CODE = APIConstants.SUCCESS;
            //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PARTNER, AclConstants.OPERATION_PARTNER_DELETE, req.getRemoteAddr(), null, partner.getId().longValue(), partner.getName());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Partner" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Partner " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Partner" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }


    @GetMapping("/getPartnerByServiceAreaId/{serviceAreaId}")
    public ResponseEntity<?> getPartnerByServiceAreaId(@Valid @PathVariable Integer serviceAreaId, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            response.put("partnerList", partnerService.convertResponseModelIntoPojo(partnerService.getPartnerByServiceAreaId(serviceAreaId)));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All  partner" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " fetch All  partner" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " fetch All  partner" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER + "\")")
    @PostMapping("/partner/list")
    public ResponseEntity<?> getPartnerList(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest re) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, re.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<PartnerPojo> partnerList = null;
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            requestDTO = setDefaultPaginationValues(requestDTO);
            long startTime = System.currentTimeMillis();
            partnerList = partnerService.getListPartnerPojo(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters());
            long afterGetList = System.currentTimeMillis();
            ApplicationLogger.logger.info("Step-1 getList() took: " + (afterGetList - startTime) + " ms");
            response.put("partnerlist", partnerList.getContent());
            long afterGetList2 = System.currentTimeMillis();
            ApplicationLogger.logger.info("Step-2 convertResponseModelIntoPojoOptimized() took: " + (afterGetList2 - afterGetList) + " ms");
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + re.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All  partner  List" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + re.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + re.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, partnerList);
    }


    @PostMapping("/getChildPartnerList/{partnerId}")
    public ResponseEntity<?> getChildPartnerList(@Valid @PathVariable Integer partnerId, @RequestBody PaginationRequestDTO requestDTO, HttpServletRequest re) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, re.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<Partner> partnerList = null;
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            requestDTO = setDefaultPaginationValues(requestDTO);
            partnerList = partnerService.getChildPartnerList(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), partnerId);
            response.put("partnerlist", partnerService.convertResponseModelIntoPojo(partnerList.getContent().stream().filter(x -> x.getPartnerType().equalsIgnoreCase("Franchise")).collect(Collectors.toList())));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + re.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All  Child partner  Lis " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + re.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Child partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + re.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Child partner list" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, partnerList);
    }



    @GetMapping("/getPartnerHierarchyByChildPartnerId/{childPartnerId}")
    public ResponseEntity<?> getPartnerHierarchyByChildPartner(@Valid @PathVariable Integer childPartnerId, HttpServletRequest re) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, re.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        List<PartnerHierarchy> partnerHierarchyList = null;
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            partnerHierarchyList = partnerService.getPartnerHierarchyList(childPartnerId);
            response.put("partnerHierarchyList", partnerHierarchyList);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + re.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All  PartnerHierarchyList By Child partner " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + re.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All PartnerHierarchyList By Child partner" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + re.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All PartnerHierarchyList By Child partner" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }


    public List<Long> getBUIdsFromCurrentStaff() {
        List<Long> mvnoIds = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff error{},exception{}", APIConstants.FAIL, e.getStackTrace());
        }
        return mvnoIds;
    }

    public int getLoggedInUserPartnerId() {
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            partnerId = -1;
        }
        return partnerId;
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
    }


    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        String SUBMODULE = MODULE + "[apiResponse()] ";
        try {
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (null != page) {
                response.put("pageDetails", setPaginationDetails(page));
            }

            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}", APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }


    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
        PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
        SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
        SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
        MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

        if (null == requestDTO.getPage()) requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize()) requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy()) requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder()) requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }

    public ValidationData validateSearchCriteria(List<GenericSearchModel> filterList) {
        ValidationData validationData = new ValidationData();
        if (null == filterList || 0 < filterList.size()) {
            validationData.setValid(false);
            validationData.setMessage("Please Provide Search Criteria");
            return validationData;
        }
        validationData.setValid(true);
        return validationData;
    }


    @ApiOperation(value = "Used to check whether Partner Management service is up or not.")
    @GetMapping("/serviceStatus")
    public String checkServiceStatus() {
        try {
            log.debug("Partner Management Service is Up");
            return "{\"success\": true,\"message\": \"Partner Management Service is Up.\"}";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/email")
    public GenericDataDTO isEmailAvailable(@RequestParam("emailId") String emailId) {
        log.info("In PartnerController.isEmailAvailable");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            boolean isEmailAvailable = partnerService.isEmailAvailable(emailId);
            if (!isEmailAvailable) {
                genericDataDTO.setData("false");
                genericDataDTO.setResponseCode(HttpStatus.CONFLICT.value());
                genericDataDTO.setResponseMessage("Email is already in use");
            } else {
                genericDataDTO.setData("true");
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
            }
        } catch (Exception exception) {
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PARTNER + "\")")
    @PostMapping("/partner/search/byColumns")
    public ResponseEntity<?> searchPartnerByColumns(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<PartnerPojo> partnerList = null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            requestDTO = (requestDTO);
            ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
            if (validationData.isValid()) {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response);
            }
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            partnerList = partnerService.searchByColumnsPartnerPojo(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
            Integer Response = 0;
            if (partnerList.isEmpty()) {
                Response = APIConstants.NULL_VALUE;
                response.put(APIConstants.MESSAGE, "No Records Found!");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword :  " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(Response, response);

            }
            if (null != partnerList && 0 < partnerList.getSize()) {
                response.put("partnerlist", partnerList.getContent());
            } else {
                response.put("partnerlist", new ArrayList<>());
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (RuntimeException re) {
            re.printStackTrace();
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, re.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + re.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search partner using keyword : " + requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, partnerList);
    }
}
