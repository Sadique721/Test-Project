package com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Controller;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Domain.SubBusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Mapper.SubBusinessUnitMapper;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Model.SubBusinessUnitDTO;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Service.SubBusinessUnitService;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SUB_BUSINESS_UNIT)
public class SubBusinessUnitController extends ExBaseAbstractController<SubBusinessUnitDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubBusinessUnitController.class);
    @Autowired
    SubBusinessUnitService subBusinessUnitService;

    @Autowired
    private Tracer tracer;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    SubBusinessUnitMapper subBusinessUnitMapper;
    public SubBusinessUnitController(SubBusinessUnitService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[SubBusinessUnitController]";
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SUB_BUSINESS_UNIT_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody SubBusinessUnitDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {

        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        MDC.put("userName",subBusinessUnitService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            boolean flag = subBusinessUnitService.duplicateVerifyAtSave(entityDTO.getSubbuname());
            boolean flagForsubbuCode = subBusinessUnitService.duplicateVerifyAtSaveSubBUcode(entityDTO.getSubbucode());

            if (flag && flagForsubbuCode) {
                dataDTO = super.save(entityDTO, result, authentication, req,res);
                SubBusinessUnitDTO subBusinessUnitDTO = (SubBusinessUnitDTO) dataDTO.getData();
                SubBusinessUnit subBusinessUnit = subBusinessUnitMapper.dtoToDomain(subBusinessUnitDTO, new CycleAvoidingMappingContext());
                createDataSharedService.sendEntitySaveDataForAllMicroService(subBusinessUnit);
                dataDTO.setResponseMessage("Successfully Created");
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create Sub-BusinessUnit"+LogConstants.LOG_BY_NAME+entityDTO.getSubbuname() +  LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE +respCode);
            } else {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                if(!flag){
                    dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_SUB_BUSINESSUNIT_NAME);
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create  Business Unit" +LogConstants.LOG_BY_NAME+entityDTO.getSubbuname() + LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE+ respCode);
                }else {
                    dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_SUB_BUSINESSUNIT_BUCODE);
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Sub-Business Unit" +LogConstants.LOG_BY_NAME+entityDTO.getSubbuname() + LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE+ respCode);
                }
            }
            return dataDTO;
        }catch (Exception ex){
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create Sub-BusinessUnit"+LogConstants.LOG_BY_NAME+entityDTO.getSubbuname()+  LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SUB_BUSINESS_UNIT_EDIT+ "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody SubBusinessUnitDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Update");
        MDC.put("userName", subBusinessUnitService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }

            SubBusinessUnit old = subBusinessUnitService.getId(entityDTO.getId());
            SubBusinessUnit oldClone = new SubBusinessUnit(old);


            boolean flag = subBusinessUnitService.duplicateVerifyAtEdit(entityDTO.getSubbuname(), entityDTO.getId());
            boolean flagForsubbuCode = subBusinessUnitService.duplicateVerifyAtEditSubBUcode(entityDTO.getSubbucode(),entityDTO.getId().longValue());
            if (flag && flagForsubbuCode) {
                dataDTO = super.update(entityDTO, result, authentication, req,res);
                SubBusinessUnitDTO subBusinessUnitDTO = (SubBusinessUnitDTO) dataDTO.getData();
                SubBusinessUnit subBusinessUnit = subBusinessUnitMapper.dtoToDomain(subBusinessUnitDTO, new CycleAvoidingMappingContext());
                createDataSharedService.updateEntityDataForAllMicroService(subBusinessUnit);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update Sub-Business Unit"+LogConstants.LOG_BY_NAME+entityDTO.getSubbuname() +LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername() + " , Updated Details : " + UpdateDiffFinder.getUpdatedDiff(oldClone,subBusinessUnit)+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS +APIConstants.SUCCESS);
            } else {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                if(!flag){
                    dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_SUB_BUSINESSUNIT_NAME);
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update  Business Unit" +LogConstants.LOG_BY_NAME+entityDTO.getSubbuname() + LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE+ respCode);
                }else {
                    dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_SUB_BUSINESSUNIT_BUCODE);
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Sub-Business Unit" +LogConstants.LOG_BY_NAME+entityDTO.getSubbuname() + LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_STATUS_CODE+ respCode);
                }
            }
            return dataDTO;
        }catch (Exception ex){

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SUB_BUSINESS_UNIT_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody SubBusinessUnitDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Delete");
        MDC.put("userName", subBusinessUnitService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());

        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            subBusinessUnitService.getEntityForUpdateAndDelete(entityDTO.getId());
            dataDTO = super.delete(entityDTO, authentication, req,res);
            SubBusinessUnitDTO subBusinessUnitDTO = (SubBusinessUnitDTO) dataDTO.getData();
            SubBusinessUnit subBusinessUnit = subBusinessUnitMapper.dtoToDomain(subBusinessUnitDTO, new CycleAvoidingMappingContext());
            subBusinessUnit.setIsDeleted(true);
            createDataSharedService.deleteEntityDataForAllMicroService(subBusinessUnit);
            if (subBusinessUnitDTO != null) {
                response.put(APIConstants.MESSAGE, "Successfully deleted");
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Sub-Business-unit"+LogConstants.LOG_BY_NAME+entityDTO.getSubbuname()+ LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+respCode);
            } else {
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Sub-Business-unit"+LogConstants.LOG_BY_NAME+entityDTO.getSubbuname()+ LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+respCode);
            }
        } catch (CustomValidationException ce) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete Sub-Business-unit" +LogConstants.LOG_BY_NAME+entityDTO.getSubbuname() + LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+respCode);
        } catch (Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete Sub-Business-unit"+LogConstants.LOG_BY_NAME+entityDTO.getSubbuname()+ LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SUB_BUSINESS_UNIT+ "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        try {
            return super.getAll(requestDTO,req,res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SUB_BUSINESS_UNIT+ "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,HttpServletResponse res) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Search");
        MDC.put("userName",subBusinessUnitService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter , req,res);
            if(genericDataDTO.getDataList().isEmpty()){
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Sub-business Unit using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.NULL_VALUE);
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Sub-business Unit using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Sub-Business Unit using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + subBusinessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + APIConstants.EXPECTATION_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_VIEW + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        try {
            return super.getAllWithoutPagination(req,res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }
}
