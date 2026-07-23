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
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.savbill.taskmanagement.core.modules.constants.UrlConstants;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCategory;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCategoryTatMapping;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseCategoryMapper;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseCategoryDTO;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseCategoryRepository;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseCategoryService;
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
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.CASE_CATEGORY)
public class CaseCategoryController extends ExBaseAbstractController<CaseCategoryDTO> {



    @Autowired
    private Tracer tracer;


    @Autowired
    private CaseCategoryRepository caseCategoryRepository;


    @Autowired
    private CaseCategoryService caseCategoryService;


    @Autowired
    private CaseCategoryMapper caseCategoryMapper;



    public CaseCategoryController(@Qualifier("caseCategoryService") ExBaseService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CaseCategoryController]";
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.taskCategory.TASK_CATEGORY_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody CaseCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {

            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            }
            if (getMvnoIdFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() == 1) {
                entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
            }

            if (getLoggedInUser().getLco())
                entityDTO.setLcoId((long) getLoggedInUser().getPartnerId());
            else
                entityDTO.setLcoId(null);

            if (Objects.isNull(entityDTO.getIsDefaultCaseCategory())) {
                entityDTO.setIsDefaultCaseCategory(false);
            }

            boolean flag = caseCategoryService.duplicateVerifyAtSave(entityDTO.getCategoryName());
            if (flag) {
                CaseCategoryDTO caseCategoryDTO = caseCategoryService.saveEntity(entityDTO);
                genericDataDTO.setData(caseCategoryDTO);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Success");
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Ticket Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, ticketReasonSubCategoryDTO.getId(), ticketReasonSubCategoryDTO.getSubCategoryName());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_SUB_CATAGORY_NAME_EXITS);
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "reason Category with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
//            log.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }



    @PreAuthorize("validatePermission(\"" + MenuConstants.taskCategory.TASK_CATEGORY_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody CaseCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            if (Objects.isNull(entityDTO.getIsDefaultCaseCategory())) {
                entityDTO.setIsDefaultCaseCategory(false);
            }
            CaseCategoryDTO dtoData = caseCategoryService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
            boolean flag = caseCategoryService.duplicateVerifyAtEdit(entityDTO.getCategoryName(), entityDTO.getCategoryId().intValue());
            if (flag) {

                CaseCategory olddata = caseCategoryService.getRepository().getOne(entityDTO.getCategoryId());
                CaseCategoryDTO olddatadto = caseCategoryMapper.domainToDTO(olddata, new CycleAvoidingMappingContext());
                genericDataDTO = super.update(entityDTO, result, authentication, req);
                if (olddatadto != null) {
//                    log.info("TicketReasonSubCategory update details: " + UpdateDiffFinder.getUpdatedDiff(olddatadto, entityDTO));
                    RESP_CODE = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + UpdateDiffFinder.getUpdatedDiff(olddatadto, entityDTO) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                }
//                Iterable<CaseCategoryTatMapping> list = caseCategoryService.updateStatus(entityDTO);
                //ticketReasonSubCategoryService.UpdateStatus(entityDTO);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_CATAGORY_NAME_EXITS);
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Case Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_UNAUTHORIZED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
//                log.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Case Category With name : " + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else if (ex instanceof CustomValidationException) {
//                log.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update  Case Category With name : " + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else {
//                log.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update  Case sub  reasone Category With name : " + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.taskCategory.TASK_CATEGORY + "\")")
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
            genericDataDTO = caseCategoryService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Case Category With name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            log.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
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


    @PreAuthorize("validatePermission(\"" + MenuConstants.taskCategory.TASK_CATEGORY_DELETE + "\")")
    @PostMapping(value = "/delete")
    public GenericDataDTO delete(@RequestBody CaseCategoryDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Long caseCategoryId = entityDTO.getCategoryId();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            //TicketReasonSubCategoryDTO dtoData = ticketReasonSubCategoryService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
            CaseCategoryDTO dtoData = caseCategoryService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
            Boolean flag = caseCategoryService.getUniqueCategory(caseCategoryId);
            if (!flag) {
                caseCategoryService.deleteEntity(entityDTO);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket  reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
                genericDataDTO.setResponseMessage("Sub Problem Domain Already in use");
                RESP_CODE = APIConstants.FAIL;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + "reasone Category with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            log.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete Ticket reason Category" + LogConstants.LOG_BY_NAME + entityDTO.getCategoryName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.taskCategory.TASK_CATEGORY + "\")")
    @GetMapping(value = "/getAllActiveReasonCatgory")
    public GenericDataDTO getAllActiveReasonCatgory(HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(caseCategoryService.getAllActiveReasonCategory());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket reason Category With name" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            log.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket reason Category With name" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            log.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All Ticket reason Category With name" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
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
        try {
            List<CaseCategory> list = caseCategoryService.getAllActiveEntities();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            log.info(LogConstants.REQUEST_FOR + "get All without Pagination" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS);
            return genericDataDTO;
        } catch (Exception ex) {
            log.error(LogConstants.REQUEST_FOR + "error while get All without Pagination" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + APIConstants.INTERNAL_SERVER_ERROR+", Error message: "+ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }

        return genericDataDTO;
    }
    @PreAuthorize("validatePermission(\"" + MenuConstants.taskCategory.TASK_CATEGORY + "\")")
    @GetMapping(value = "/getTatByCaseCategory")
    public GenericDataDTO getTaTCaseCategoryById(@RequestParam("caseCategoryId") Long categoryId,HttpServletRequest request) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            Object data = getEntityById(String.valueOf(categoryId), request).getData();
            if(Objects.nonNull(data)){
                CaseCategoryDTO caseCategoryDTO = (CaseCategoryDTO)data;
                List<TicketTatMatrix> ticketTatMatrixList = new ArrayList<>();
                for(CaseCategoryTatMapping matrixMapping:caseCategoryDTO.getCaseCategoryTatMappingList()){
                    ticketTatMatrixList.add(matrixMapping.getTicketTatMatrix());
                }
                genericDataDTO.setData(ticketTatMatrixList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("SUCCESS");
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search all TATs by category id" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }else{
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not found");
                RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search all TATs by category id" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        }catch (Exception ex ){
            log.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search all TATs by category id" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE + " Message : " + ex.getMessage());
        }finally{
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return  genericDataDTO;
    }
}
