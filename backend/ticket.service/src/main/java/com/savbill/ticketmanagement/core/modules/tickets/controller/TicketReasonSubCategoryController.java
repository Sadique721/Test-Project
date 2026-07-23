package com.savbill.ticketmanagement.core.modules.tickets.controller;

import com.savbill.ticketmanagement.core.constants.LogConstants;
import com.savbill.ticketmanagement.core.constants.MenuConstants;
import com.savbill.ticketmanagement.core.constants.MessageConstants;
import com.savbill.ticketmanagement.core.controller.ExBaseAbstractController;
import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.dto.PaginationRequestDTO;
import com.savbill.ticketmanagement.core.exceptions.CustomValidationException;
import com.savbill.ticketmanagement.core.exceptions.DataNotFoundException;
import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.ticketmanagement.core.modules.constants.UrlConstants;
import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketReasonSubCategory;
import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketSubCategoryTatMapping;
import com.savbill.ticketmanagement.core.modules.tickets.mapper.TicketReasonSubCategoryMapper;
import com.savbill.ticketmanagement.core.modules.tickets.model.TicketReasonSubCategoryDTO;
import com.savbill.ticketmanagement.core.modules.tickets.service.TicketReasonSubCategoryService;
import com.savbill.ticketmanagement.core.modules.utils.APIConstants;
import com.savbill.ticketmanagement.core.modules.utils.Constants;
import com.savbill.ticketmanagement.core.modules.utils.UpdateDiffFinder;
import com.savbill.ticketmanagement.core.security.dto.LoggedInUser;
import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.TICKET_REASON_SUB_CATEGORY)
public class TicketReasonSubCategoryController extends ExBaseAbstractController<TicketReasonSubCategoryDTO> {

    @Autowired
    TicketReasonSubCategoryService ticketReasonSubCategoryService;

    @Autowired
    TicketReasonSubCategoryMapper ticketReasonSubCategoryMapperr;

    @Autowired
    private Tracer tracer;

//    @Autowired
//    private AuditLogService auditLogService;


    public TicketReasonSubCategoryController(TicketReasonSubCategoryService ticketReasonSubCategoryService) {
        super(ticketReasonSubCategoryService);
    }

    private final Logger log = Logger.getLogger(TicketReasonSubCategoryController.class);

    @Override
    public String getModuleNameForLog() {
        return "[TicketReasonSubCategoryController]";
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.ticketreasonSubCategory.TICKET_REASON_SUB_CATEGORY_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody TicketReasonSubCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
//            entityDTO.setInwardNumber(CommonUtils.getResponse("","",null,5));
//            entityDTO.unusedQty=entityDTO.getQty();
//            entityDTO.setUsedQty(0L);

            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket sub reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            }
            if (getMvnoIdFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() == 1) {
                entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
            }

            if (getLoggedInUser().getLco())
                entityDTO.setLcoId(getLoggedInUser().getPartnerId());
            else
                entityDTO.setLcoId(null);

            if (Objects.isNull(entityDTO.getIsDefaultSubProblemDomain())) {
                entityDTO.setIsDefaultSubProblemDomain(false);
            }

            boolean flag = ticketReasonSubCategoryService.duplicateVerifyAtSave(entityDTO.getSubCategoryName());
            if (flag) {
                TicketReasonSubCategoryDTO ticketReasonSubCategoryDTO = ticketReasonSubCategoryService.saveEntity(entityDTO);
                genericDataDTO.setData(ticketReasonSubCategoryDTO);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Success");
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket sub reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, ticketReasonSubCategoryDTO.getId(), ticketReasonSubCategoryDTO.getSubCategoryName());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_SUB_CATAGORY_NAME_EXITS);
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket sub  reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "reasone Category with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket sub  reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket sub  reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.ticketreasonSubCategory.TICKET_REASON_SUB_CATEGORY_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody TicketReasonSubCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            if (Objects.isNull(entityDTO.getIsDefaultSubProblemDomain())) {
                entityDTO.setIsDefaultSubProblemDomain(false);
            }
            TicketReasonSubCategoryDTO dtoData = ticketReasonSubCategoryService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
            boolean flag = ticketReasonSubCategoryService.duplicateVerifyAtEdit(entityDTO.getSubCategoryName(), entityDTO.getId().intValue());
            if (flag) {

                TicketReasonSubCategory olddata = ticketReasonSubCategoryService.getRepository().getOne(entityDTO.getId());
                TicketReasonSubCategoryDTO olddatadto = ticketReasonSubCategoryMapperr.domainToDTO(olddata, new CycleAvoidingMappingContext());
                genericDataDTO = super.update(entityDTO, result, authentication, req);
                if (olddatadto != null) {
//                    log.info("TicketReasonSubCategory update details: " + UpdateDiffFinder.getUpdatedDiff(olddatadto, entityDTO));
                    RESP_CODE = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Ticket sub reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + UpdateDiffFinder.getUpdatedDiff(olddatadto, entityDTO) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                }
                Iterable<TicketSubCategoryTatMapping> list = ticketReasonSubCategoryService.updateStatus(entityDTO);
                //ticketReasonSubCategoryService.UpdateStatus(entityDTO);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_CATAGORY_NAME_EXITS);
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Ticket sub reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Ticket sub reasone Category With name : " + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else if (ex instanceof CustomValidationException) {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update  Ticket sub reasone Category With name : " + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update  Ticket sub  reasone Category With name : " + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.ticketreasonSubCategory.TICKET_REASON_SUB_CATEGORY + "\")")
    @PostMapping(value = "/searchAll")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = ticketReasonSubCategoryService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Ticket sub reasone Category With name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Ticket sub  reasone Category With name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Ticket sub  reasone Category With name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.TicketConversation.SLA_COUNTER + "\")")
    @GetMapping(value = "/getSubCategoryReasons")
    public GenericDataDTO getSubCategoryReasons(@RequestParam("parentCategoryId") Long parentCategoryId, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(ticketReasonSubCategoryService.getSubCategoryReasons(parentCategoryId));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket sub reason Category With parent category" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket sub reason Category With parent category" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket sub reason Category With parent category" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            //ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.ticketreasonSubCategory.TICKET_REASON_SUB_CATEGORY_DELETE + "\")")
    @PostMapping(value = "/delete")
    public GenericDataDTO delete(@RequestBody TicketReasonSubCategoryDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Long reasone_cat_id = entityDTO.getId();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            //TicketReasonSubCategoryDTO dtoData = ticketReasonSubCategoryService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
            TicketReasonSubCategoryDTO dtoData = ticketReasonSubCategoryService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
            Boolean flag = ticketReasonSubCategoryService.getUniqueSubCategory(reasone_cat_id);
            if (!flag) {
                super.delete(entityDTO, authentication, req);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket sub reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
                genericDataDTO.setResponseMessage("Sub Problem Domain Already in use");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket sub  reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "reasone Category with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket sub  reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket sub  reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/isReasonSubCategoryDefault")
    public GenericDataDTO isReasonSubCategoryDefault(@RequestBody List<Integer> categoryIds, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            List<Long> buIds = getBUIdsFromCurrentStaff();
            if (buIds != null && !buIds.isEmpty()) {
                genericDataDTO.setData(ticketReasonSubCategoryService.isReasonSubCategoryDefault(categoryIds, buIds.get(0)));
            } else {
                genericDataDTO.setData(ticketReasonSubCategoryService.isReasonSubCategoryDefault(categoryIds, null));
            }
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "finding given service id have defualt sub  problem domain " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "finding given service id have defualt sub problem domain " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "finding given service id have defualt sub  problem domain " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @Override
    public GenericDataDTO getAllWithoutPagination() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<TicketReasonSubCategory> list = ticketReasonSubCategoryService.getAllActiveEntities();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());

            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }

        return genericDataDTO;
    }
}
