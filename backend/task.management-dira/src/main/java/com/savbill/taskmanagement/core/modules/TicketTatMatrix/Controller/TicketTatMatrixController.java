package com.savbill.taskmanagement.core.modules.TicketTatMatrix.Controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.taskmanagement.core.constants.DeleteContant;
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
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Mapper.TicketTatMatrixMapper;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Model.TicketTatMatrixDTO;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Repository.TicketTatMatrixRepository;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Service.TicketTatMatrixService;
import com.savbill.taskmanagement.core.modules.constants.UrlConstants;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;
import com.savbill.taskmanagement.core.modules.utils.Constants;
import com.savbill.taskmanagement.core.modules.utils.UpdateDiffFinder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TASK_TAT_MATRIX)
public class TicketTatMatrixController extends ExBaseAbstractController<TicketTatMatrixDTO> {

    @Autowired
    private Tracer tracer;


//    @Autowired
//    AuditLogService auditLogService;

    @Autowired
    TicketTatMatrixService tatMatrixService;

    @Autowired
    TicketTatMatrixRepository repository;

    @Autowired
    TicketTatMatrixMapper ticketTatMatrixMapper;

    public TicketTatMatrixController(TicketTatMatrixService service) {
        super(service);
    }


    @Override
    public String getModuleNameForLog() {
        return "[TicketTatMatrixController]";
    }


    @Override
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.tatmatrixmanagement.TAT_MATRIX_CREATE + "\")")
    public GenericDataDTO save(@Valid @RequestBody TicketTatMatrixDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", tatMatrixService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        boolean flag = tatMatrixService.duplicateVerifyAtSave(entityDTO.getName());
        TicketTatMatrixDTO matrixDTO = null;
        try {
            if (flag) {
                if (getMvnoIdFromCurrentStaff() != null) {
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());

                    if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                        RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                        log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create tat matrix with name" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + "Unable to create tat" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                        dataDTO.setResponseMessage(Constants.AVOID_SAVE_MULTIPLE_BU);
                        throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, Constants.AVOID_SAVE_MULTIPLE_BU, null);

                    }
                    if (getBUIdsFromCurrentStaff().size() == 1) {
                        entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
                    }
                }
                dataDTO = super.save(entityDTO, result, authentication, req);
                matrixDTO = (TicketTatMatrixDTO) dataDTO.getData();

                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create tat matrix with name" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.MATRIX_NAME);
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create tat matrix with name" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + "Unable to create tat" + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }

        } catch (CustomValidationException e) {
            RESP_CODE = e.getErrCode();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create tat matrix with name" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }


    @Override
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.tatmatrixmanagement.TAT_MATRIX_EDIT + "\")")
    public GenericDataDTO update(@Valid @RequestBody TicketTatMatrixDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Update");
        MDC.put("userName", tatMatrixService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        TicketTatMatrixDTO td = tatMatrixService.getEntityById(entityDTO.getId());
        GenericDataDTO dataDTO = new GenericDataDTO();
        //String updatedValues = CommonUtils.getUpdatedDiff(entityDTO,td);

        try {
            TicketTatMatrixDTO dtoData = tatMatrixService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
            boolean flag = tatMatrixService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue());
            if (flag) {
                TicketTatMatrix olddata = tatMatrixService.getRepository().getOne(entityDTO.getId());
                TicketTatMatrixDTO olddatadto = ticketTatMatrixMapper.domainToDTO(olddata, new CycleAvoidingMappingContext());
                dataDTO = super.update(entityDTO, result, authentication, req);
                if (olddatadto != null) {
//                    log.info("Ticket Tat update details: " + UpdateDiffFinder.getUpdatedDiff(olddatadto, entityDTO));
                    RESP_CODE = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update tat matrix" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + UpdateDiffFinder.getUpdatedDiff(olddatadto, entityDTO) + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                }
                TicketTatMatrixDTO tatMatrixDTODTO = (TicketTatMatrixDTO) dataDTO.getData();
                if (tatMatrixDTODTO != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MATRIX,
//                        AclConstants.OPERATION_MATRIX_EDIT, req.getRemoteAddr(), null, tatMatrixDTODTO.getId(), tatMatrixDTODTO.getName());
                }

            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.MATRIX_NAME);
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update tat matrix" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + "Unable to create tat" + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }

        } catch (Exception ex) {

            if (ex instanceof DataNotFoundException) {
                log.error(getModuleNameForLog() + "[UPDATE] " + ex.getMessage(), ex);
                dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                dataDTO.setResponseMessage("Not Found");
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update tat matrix" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else if (ex instanceof CustomValidationException) {
                log.error(getModuleNameForLog() + "[UPDATE] " + ex.getMessage(), ex);
                dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                dataDTO.setResponseMessage(ex.getMessage());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update tat matrix" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else {
                log.error(getModuleNameForLog() + "[UPDATE] " + ex.getMessage(), ex);
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage("Failed to update data. Please try after some time");
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update tat matrix" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_INFO + "Unable too update tat " + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;

    }

    @Override
    //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.tatmatrixmanagement.TAT_MATRIX + "\")")
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        MDC.put("userName", tatMatrixService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO dataDTO = null;
        try {
            dataDTO = super.getEntityById(id, req);
            TicketTatMatrixDTO tatMatrixDTO = (TicketTatMatrixDTO) dataDTO.getData();
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "get TatMatrix by ID : "+id  + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "get TatMatrix by ID : " +id + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() +LogConstants.LOG_ERROR+e.getMessage() +LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            e.printStackTrace();
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove(LogConstants.TRACE_ID);
        }
        
        return dataDTO;
    }


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.tatmatrixmanagement.TAT_MATRIX + "\")")
    @PostMapping(value = "/searchAll")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Search");
        MDC.put("userName", tatMatrixService.getLoggedInUser().getFirstName());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = tatMatrixService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All TatMatrix for name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            log.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            RESP_CODE = ce.getErrCode();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All TatMatrix for name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search All TatMatrix for name : " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_VIEW + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination() {
        Integer RESP_CODE = APIConstants.SUCCESS;
        log.info(LogConstants.REQUEST_FOR + " get all TAT matrix without pagination" + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        return super.getAllWithoutPagination();
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.SUCCESS;
        try {
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " get all TAT matrix " + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return super.getAll(requestDTO, req);
        } catch (Exception ex) {
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " get all TAT matrix " + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.clear();
        }
        return null;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_VIEW + "\")")
    @GetMapping("/searchByStatus")
    public GenericDataDTO getAllByStatus(HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + "[getALlByStatus] ";
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Search");
        MDC.put("userName", tatMatrixService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = GenericDataDTO.getGenericDataDTO(tatMatrixService.findAllByStatus());
            if (null != genericDataDTO) {

                if (genericDataDTO.getDataList().isEmpty()) {
                    genericDataDTO = new GenericDataDTO();
                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                    genericDataDTO.setResponseMessage("No Record Found!");
                    genericDataDTO.setDataList(new ArrayList<>());
                    genericDataDTO.setTotalRecords(0);
                    genericDataDTO.setPageRecords(0);
                    genericDataDTO.setCurrentPageNumber(1);
                    genericDataDTO.setTotalPages(1);
                    RESP_CODE = APIConstants.NOT_FOUND;
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Tat matrix" + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);


                }

                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Tat matrix" + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }

        } catch (Exception ex) {
//            log.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Tat matrix" + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return genericDataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_DELETE + "\")")
    @Override
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_ALL + "\",\"" + AclConstants.OPERATION_TICKET_TAT_MATRIX_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.tatmatrixmanagement.TAT_MATRIX_DELETE + "\")")
    @PostMapping("/delete")
    public GenericDataDTO delete(@RequestBody TicketTatMatrixDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Delete");
        MDC.put("userName", tatMatrixService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            boolean flag = tatMatrixService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                TicketTatMatrixDTO dtoData = tatMatrixService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
                dataDTO = super.delete(entityDTO, authentication, req);
                TicketTatMatrixDTO tatMatrixDTODTO = (TicketTatMatrixDTO) dataDTO.getData();
                if (tatMatrixDTODTO != null) {
//                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MATRIX,
//                            AclConstants.OPERATION_MATRIX_DELETE, req.getRemoteAddr(), null, tatMatrixDTODTO.getId(), tatMatrixDTODTO.getName());
                }
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete tat matrix" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else {
                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                dataDTO.setResponseMessage(DeleteContant.MATRIX_EXIST);
                RESP_CODE = HttpStatus.METHOD_NOT_ALLOWED.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete tat matrix" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_INFO + DeleteContant.MATRIX_EXIST + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                dataDTO.setResponseMessage(ex.getMessage());
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete tat matrix" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + tatMatrixService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);


            } else {
                ex.printStackTrace();

            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

}

