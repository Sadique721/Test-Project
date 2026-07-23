package com.savbill.taskmanagement.core.modules.tasks.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.taskmanagement.core.constants.LogConstants;
import com.savbill.taskmanagement.core.constants.MenuConstants;
import com.savbill.taskmanagement.core.constants.MessageConstants;
import com.savbill.taskmanagement.core.controller.ExBaseAbstractController;
import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.PaginationRequestDTO;
import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.exceptions.DataNotFoundException;
import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.modules.constants.UrlConstants;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseSubCategory;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseSubCategoryMapper;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseSubCategoryDTO;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseSubCategoryCategoryMappingRepository;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseSubCategoryRepository;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseSubCategoryService;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;
import com.savbill.taskmanagement.core.modules.utils.Constants;
import com.savbill.taskmanagement.core.modules.utils.UpdateDiffFinder;
import com.savbill.taskmanagement.core.security.dto.LoggedInUser;
import com.savbill.taskmanagement.core.service.ExBaseService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.CASE_SUB_CATEGORY)
public class CaseSubCategoryController extends ExBaseAbstractController<CaseSubCategoryDTO> {

    @Autowired
    private Tracer tracer;

    @Autowired
    CaseSubCategoryMapper caseSubCategoryMapper;
    public CaseSubCategoryController(@Qualifier("caseSubCategoryService") ExBaseService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CaseSubCategoryController]";
    }



    @Autowired
    private CaseSubCategoryService caseSubCategoryService;


    @Autowired
    private CaseSubCategoryRepository caseSubCategoryRepository;

    @Autowired
    private CaseSubCategoryCategoryMappingRepository caseSubCategoryCategoryMappingRepository;

    @PreAuthorize("validatePermission(\"" + MenuConstants.taskSubCategory.TASK_SUB_CATEGORY_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody CaseSubCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {

            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            }
            if (getMvnoIdFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() == 1) {
                entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
            }


            boolean flag = caseSubCategoryService.duplicateVerifyAtSave(entityDTO.getSubCategoryName());
            if (flag) {
                CaseSubCategoryDTO ticketReasonSubCategoryDTO = caseSubCategoryService.saveEntity(entityDTO);
                genericDataDTO.setData(ticketReasonSubCategoryDTO);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Success");
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, ticketReasonSubCategoryDTO.getId(), ticketReasonSubCategoryDTO.getSubCategoryName());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_SUB_CATAGORY_NAME_EXITS);
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "reasone Category with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }



    @PreAuthorize("validatePermission(\"" + MenuConstants.taskSubCategory.TASK_SUB_CATEGORY_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody CaseSubCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {

            CaseSubCategoryDTO dtoData = caseSubCategoryService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
            boolean flag = caseSubCategoryService.duplicateVerifyAtEdit(entityDTO.getSubCategoryName(), entityDTO.getSubCategoryId().intValue());
            if (flag) {

                CaseSubCategory olddata = caseSubCategoryService.getRepository().getOne(entityDTO.getSubCategoryId());
                CaseSubCategoryDTO olddatadto = caseSubCategoryMapper.domainToDTO(olddata, new CycleAvoidingMappingContext());
                genericDataDTO = super.update(entityDTO, result, authentication, req);
                if (olddatadto != null) {
                    RESP_CODE = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + UpdateDiffFinder.getUpdatedDiff(olddatadto, entityDTO) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                }

            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_CATAGORY_NAME_EXITS);
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
//                log.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Case Category With name : " + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else if (ex instanceof CustomValidationException) {
//                log.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update  Case Category With name : " + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else {
//                log.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update  Case sub  reasone Category With name : " + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.taskSubCategory.TASK_SUB_CATEGORY + "\")")
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
            genericDataDTO = caseSubCategoryService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Case Category With name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            log.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Case Category With name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            log.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Case Category With name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.taskSubCategory.TASK_SUB_CATEGORY_DELETE + "\")")
    @PostMapping(value = "/delete")
    public GenericDataDTO delete(@RequestBody CaseSubCategoryDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Long reasone_cat_id = entityDTO.getSubCategoryId();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            //TicketReasonSubCategoryDTO dtoData = ticketReasonSubCategoryService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
            CaseSubCategoryDTO dtoData = caseSubCategoryService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
            Boolean flag = caseSubCategoryService.getUniqueSubCategory(reasone_cat_id);
            if (!flag) {
                caseSubCategoryService.deleteEntity(entityDTO);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket sub reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
                genericDataDTO.setResponseMessage("Sub Category Domain Already in use");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket sub  reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + " Sub Category Domain Already in use! cannot delete" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            log.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket sub  reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket sub  reasone Category" + LogConstants.LOG_BY_NAME + entityDTO.getSubCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
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
            //log.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }


    @Override
    public GenericDataDTO getAllWithoutPagination() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            List<CaseSubCategory> list = caseSubCategoryService.getAllActiveEntities();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FOR + " get all without pagination" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } catch (Exception ex) {
            RESP_CODE = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FOR + " get all without pagination " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.taskSubCategory.TASK_SUB_CATEGORY + "\")")
    @PostMapping(value = "/getAll")
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = caseSubCategoryService.search(new ArrayList<>(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching List: " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            log.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching List : " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            log.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching List: " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.taskSubCategory.TASK_SUB_CATEGORY + "\")")
    @GetMapping(value = "/getAllSubCaseCategoryListByCategoryId")
    public GenericDataDTO getAllSubCaseCategoryListByCategoryId(@RequestParam Integer caseCategoryId, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = caseSubCategoryService.getAllSubcategoryFromCategoryWithoutPagination(caseCategoryId);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching List: " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            log.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching List : " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            log.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching List: " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }



}
