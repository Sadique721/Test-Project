package com.savbill.cpm.modules.Cas.Controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.cpm.constants.*;
import com.savbill.cpm.constants.*;
import com.savbill.cpm.core.controller.ExBaseAbstractController2;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.modules.Cas.Domain.CasParameterMapping;
import com.savbill.cpm.modules.Cas.Model.CasMasterDTO;
import com.savbill.cpm.modules.Cas.Repository.CasParameterMappingRepocitory;
import com.savbill.cpm.modules.Cas.Service.CasMasterService;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonConstants;
import com.savbill.cpm.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.CASE_PACKAGE)
public class CasMasterController extends ExBaseAbstractController2<CasMasterDTO> {
    public CasMasterController(CasMasterService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CasePackageController]";
    }

    @Autowired
    private Tracer tracer;


    private final Logger logger = LoggerFactory.getLogger(CasMasterController.class);
    @Autowired
    CasMasterService casMasterService;
    @Autowired
    CasParameterMappingRepocitory casParameterMappingRepocitory;

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CAS_MASTER_ALL + "\",\"" + AclConstants.OPERATION_CAS_MASTER_ADD + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody CasMasterDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", casMasterService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        boolean flag = casMasterService.duplicateVerifyAtSave(entityDTO.getCasname());
        CasMasterDTO casMasterDTO = null;
        if (flag) {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());

                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                    throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
                }
                if (getBUIdsFromCurrentStaff().size() == 1) {
                    entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
                }
            }
            dataDTO = super.save(entityDTO, result, authentication, req);
            casMasterDTO = (CasMasterDTO) dataDTO.getData();
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Cas Management"+LogConstants.LOG_BY_NAME+entityDTO.getCasname() + LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            if (casMasterDTO != null) {
                casMasterService.sendCreatedDataShared(casMasterDTO, CommonConstants.OPERATION_ADD);
            }
        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.CAS_NAME);
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create Cas Management"+LogConstants.LOG_BY_NAME+ casMasterDTO.getCasname() + LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        }
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");

        return dataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.CAS_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody CasMasterDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", casMasterService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            casMasterService.getEntityForUpdateAndDelete(entityDTO.getId());
            CasMasterDTO casMasterDTO = casMasterService.getEntityById(entityDTO.getId());
            List<CasParameterMapping> casParameterMappingList = new ArrayList<>();
            entityDTO.getCasParameterMappings().stream().forEach(r -> {
                if (r.getId() != null) {
                    r.setCasId(entityDTO.getId());
                    casParameterMappingList.add(r);
                }

            });
            String updatedValues = CommonUtils.getUpdatedDiff(casMasterDTO,entityDTO);
            boolean flag = casMasterService.duplicateVerifyAtEdit(entityDTO.getCasname(), entityDTO.getId().intValue());
            if (flag) {
                dataDTO = super.update(entityDTO, result, authentication, req);
                if (!casParameterMappingList.isEmpty()) {
                    casParameterMappingRepocitory.saveAll(casParameterMappingList);
                }
                CasMasterDTO casMasterDTO1 = (CasMasterDTO) dataDTO.getData();
                if (casMasterDTO1 != null) {
                    casMasterService.sendCreatedDataShared(casMasterDTO1, CommonConstants.OPERATION_UPDATE);
                }
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Cas Management" +LogConstants.LOG_BY_NAME+casMasterDTO.getCasname()+ LogConstants.REQUEST_BY+casMasterService.getLoggedInUser().getUsername()+" , Updated Details "+updatedValues+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS );
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.CAS_NAME);
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Cas Management" +  LogConstants.LOG_BY_NAME+casMasterDTO.getCasname()+ LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+ LogConstants.LOG_STATUS_CODE + APIConstants.FAIL );
            }
        } catch (CustomValidationException ex) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Cas Management" + LogConstants.LOG_BY_NAME+entityDTO.getCasname()+LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE +ex.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.FAIL );
        } catch (Exception ex){
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Cas Management" +  LogConstants.LOG_BY_NAME+entityDTO.getCasname()+ LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE +ex.getMessage()+LogConstants.LOG_STATUS_CODE + APIConstants.FAIL );
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.CAS_MGMT + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO dataDTO = super.getEntityById(id, req);
        CasMasterDTO casMasterDTO = (CasMasterDTO) dataDTO.getData();
        MDC.remove("type");
        return dataDTO;
    }

    @GetMapping(value = "/getactivecas")
    public GenericDataDTO getAllActiveCas(HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", casMasterService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            dataDTO.setDataList(casMasterService.getAllActiveEntities());
            dataDTO.setResponseCode(APIConstants.SUCCESS);
            dataDTO.setResponseMessage("Success");
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Active Cas Management" + LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception ex){
            dataDTO.setResponseCode(APIConstants.FAIL);
            dataDTO.setResponseMessage("Error");
            ex.printStackTrace();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +" Fetch Active Cas Management" + LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    //Get All MATRIX With Pagination
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CAS_MASTER_ALL + "\",\"" + AclConstants.OPERATION_CAS_MASTER_VIEW + "\")")
    @PostMapping(value = "/searchAll")
    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO, List<GenericSearchModel> filterList, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", casMasterService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {

            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Active CasMaster using keyword : " + filterList.get(0).getFilterValue() + LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            genericDataDTO = casMasterService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetch Active CasMaster using keyword : " + filterList.get(0).getFilterValue() + LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetch Active CasMaster using keyword: " + filterList.get(0).getFilterValue() + LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        }
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.CAS_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody CasMasterDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", casMasterService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            boolean flag = casMasterService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                dataDTO = super.delete(entityDTO, authentication, req);
                CasMasterDTO casMasterDTO = (CasMasterDTO) dataDTO.getData();
                if (casMasterDTO != null) {
                    casMasterService.sendCreatedDataShared(casMasterDTO, CommonConstants.OPERATION_DELETE);
                }
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete CasManagement"+LogConstants.LOG_BY_NAME+entityDTO.getCasname()+LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS );
            } else {
                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                dataDTO.setResponseMessage(DeleteContant.CAS_EXIST);
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete CasManagement"+LogConstants.LOG_BY_NAME+ LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
            }
        }catch (Exception ex){
            if (ex instanceof RuntimeException) {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(ex.getMessage());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete CasManagement"+LogConstants.LOG_BY_NAME+ LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +APIConstants.ERROR_MESSAGE +ex.getMessage()+ LogConstants.LOG_STATUS_CODE +HttpStatus.NOT_ACCEPTABLE.value());

            } else {
                ex.printStackTrace();
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete CasManagement"+LogConstants.LOG_BY_NAME+ LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +APIConstants.ERROR_MESSAGE +ex.getMessage()+ LogConstants.LOG_STATUS_CODE +HttpStatus.NOT_ACCEPTABLE.value());

            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.CAS_MGMT + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        return super.getAll(requestDTO, req);
    }


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CAS_MASTER_ALL + "\",\"" + AclConstants.OPERATION_CAS_MASTER_VIEW + "\")")
    @GetMapping(value = "/refreshCasPackage")
    public GenericDataDTO refreshCasPackage(@RequestParam(name = "casID") Long casID, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", casMasterService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = casMasterService.refreshCasPackage(casID);
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Fresh CasPackage" + LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +" Fetch Active Cas Management" + LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +" Fetch Active Cas Management" + LogConstants.REQUEST_BY + casMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

}
