package com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Controller;



import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.dto.ValidationData;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.exceptions.DataNotFoundException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Domain.SubBusinessVertical;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Mapper.SubBusinessVerticalMapper;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Model.SubBusinessVerticalDTO;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Repository.SubBusinessVerticalRepository;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Service.SubBusinessVerticalService;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SUB_BUSINESS_VERICAL)
public class SubBusinessVerticalController extends ExBaseAbstractController<SubBusinessVerticalDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubBusinessVerticalController.class);

    public SubBusinessVerticalController(SubBusinessVerticalService service) {
        super(service);
    }


    @Override
    public String getModuleNameForLog() {
        return "[SubBusinessVerticalController]";
    }

    @Autowired
    SubBusinessVerticalRepository subBusinessVerticalRepository;

    @Autowired
    private Tracer tracer;
    @Autowired
    SubBusinessVerticalService subBusinessVerticalService;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    SubBusinessVerticalMapper subBusinessVerticalMapper;

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SUB_BUSINESS_VERTICALS_DELETE + "\")")
    @DeleteMapping(value = "/delete")
    public GenericDataDTO delete(@RequestParam(name = "id") Long id, HttpServletRequest req,HttpServletResponse res) throws Exception {
       int respCode = APIConstants.FAIL;
       TraceContext traceContext = tracer.currentSpan().context();
        Map<String ,Object> response = new HashMap<>();
       MDC.put("type", "Delete");
       MDC.put("userName",subBusinessVerticalService.getLoggedInUser().getUsername());
       MDC.put("traceId", traceContext.traceIdString());
       MDC.put("spanId",traceContext.spanIdString());
    SubBusinessVerticalDTO entityDTO = new SubBusinessVerticalDTO();

        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            subBusinessVerticalService.getEntityForUpdateAndDelete(id);
            subBusinessVerticalRepository.deleteById(id);
            SubBusinessVertical subBusinessVertical = subBusinessVerticalRepository.findById(id).orElse(null);
            if (subBusinessVertical != null) {
//            SubBusinessVerticalDTO subBusinessVerticalDTO = subBusinessVerticalMapper.domainToDTO(subBusinessVertical,new CycleAvoidingMappingContext());
                createDataSharedService.updateEntityDataForAllMicroService(subBusinessVertical);

                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete sub-businessVertical"+LogConstants.LOG_BY_NAME+entityDTO.getSbvname() +LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND +LogConstants.LOG_STATUS_CODE+ respCode);
            }else {
                response.put(APIConstants.MESSAGE, "Successfully deleted");
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete sub-businessVertical"+LogConstants.LOG_BY_NAME+entityDTO.getSbvname()+ LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+ respCode);

            }

        } catch (CustomValidationException ce) {
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete sub-businessVertical" +LogConstants.LOG_BY_NAME+entityDTO.getSbvname()+ LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() +LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            respCode = HttpStatus.METHOD_NOT_ALLOWED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete sub-businessVertical"+LogConstants.LOG_BY_NAME+entityDTO.getSbvname()+ LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
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

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_ADD+ "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SUB_BUSINESS_VERTICALS_CREATE+ "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody SubBusinessVerticalDTO subBusinessVerticalDTO,BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        MDC.put("userName", subBusinessVerticalService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            Boolean flag = subBusinessVerticalService.duplicateVerifyAtSave(subBusinessVerticalDTO.getSbvname());
            if (flag) {
                dataDTO = super.save(subBusinessVerticalDTO, result, authentication, req,res);
                SubBusinessVerticalDTO savedSubbuVerticalDTO = new SubBusinessVerticalDTO();
                savedSubbuVerticalDTO = (SubBusinessVerticalDTO) dataDTO.getData();
                SubBusinessVertical subBusinessVertical = subBusinessVerticalMapper.dtoToDomain(savedSubbuVerticalDTO, new CycleAvoidingMappingContext());
                createDataSharedService.sendEntitySaveDataForAllMicroService(subBusinessVertical);
                respCode=APIConstants.SUCCESS;
                response.put(APIConstants.MESSAGE, "Successfully Created");
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"create Sub-Business Verticals"+LogConstants.LOG_BY_NAME+subBusinessVerticalDTO.getSbvname()+ LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+ respCode);
            } else {
                respCode=HttpStatus.NOT_ACCEPTABLE.value();
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.SUB_BUSINESS_VERTICAL_NAME_EXITS);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"create Business Verticals"+LogConstants.LOG_BY_NAME+subBusinessVerticalDTO.getSbvname()+ LogConstants.REQUEST_BY +subBusinessVerticalService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+ LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE+ respCode);
            }
        }
        finally {
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");
        long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
        res.addHeader("Server-Timing", "app;dur=" + durationInMs);
    }
            return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_VIEW+ "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SUB_BUSINESS_VERTICALS+ "\")")
    @Override
    public GenericDataDTO getEntityById (@PathVariable String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
        long startTime = System.nanoTime();  // Start measuring
        try {
            GenericDataDTO dataDTO = super.getEntityById(id, req,res);
            SubBusinessVerticalDTO subBusinessVerticalDTO = (SubBusinessVerticalDTO) dataDTO.getData();
            return dataDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_VIEW+ "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination (HttpServletRequest req,HttpServletResponse res) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        long startTime = System.nanoTime();  // Start measuring
        try {
            List<SubBusinessVerticalDTO> list = subBusinessVerticalService.getAllEntities().stream().filter(subBusinessVerticalDTO -> !subBusinessVerticalDTO.getIsDeleted()).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
    //        logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        //    logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SUB_BUSINESS_VERTICALS_EDIT+ "\")")
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public GenericDataDTO update(@Valid @RequestBody SubBusinessVerticalDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Update");
        MDC.put("userName", subBusinessVerticalService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {

            if (result.hasErrors()) {
                  respCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update sub-businessVertical"+LogConstants.LOG_BY_NAME+entityDTO.getSbvname()+ LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+respCode);
                return genericDataDTO;
            }
            ValidationData validation = validateUpdate(entityDTO);
            if (!validation.isValid()) {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update sub-businessVertical"+LogConstants.LOG_BY_NAME+entityDTO.getSbvname()+ LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+respCode);
                return genericDataDTO;
            }
            Boolean flag = subBusinessVerticalService.duplicateVerifyAtEdit(entityDTO.getSbvname(), entityDTO.getId());
            if(flag){
                SubBusinessVertical old1 = subBusinessVerticalService.getId(entityDTO.getId());
                SubBusinessVertical oldClone = new SubBusinessVertical(old1);
                SubBusinessVerticalDTO dtoData = subBusinessVerticalService.getEntityForUpdateAndDelete(entityDTO.getId());
                genericDataDTO.setData(subBusinessVerticalService.updateEntity(entityDTO));
                SubBusinessVerticalDTO subBusinessVerticalDTO =(SubBusinessVerticalDTO) genericDataDTO.getData();
                SubBusinessVertical subBusinessVertical = subBusinessVerticalMapper.dtoToDomain(subBusinessVerticalDTO,new CycleAvoidingMappingContext());
                createDataSharedService.updateEntityDataForAllMicroService(subBusinessVertical);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Success");
                genericDataDTO.setTotalRecords(1);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update sub-businessVertical"+LogConstants.LOG_BY_NAME+entityDTO.getSbvname() + LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername() + " , Updated sub-businessVertical Details " + UpdateDiffFinder.getUpdatedDiff(oldClone,subBusinessVertical)+ LogConstants.LOG_STATUS +" "+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +APIConstants.SUCCESS);
            } else {
                respCode=HttpStatus.NOT_ACCEPTABLE.value();
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUB_BUSINESS_VERTICAL_NAME_EXITS);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"create Business Verticals"+LogConstants.LOG_BY_NAME+entityDTO.getSbvname()+ LogConstants.REQUEST_BY +subBusinessVerticalService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+ LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE+ respCode);
            }


        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                respCode = HttpStatus.NOT_FOUND.value();
                response.put(APIConstants.ERROR_TAG, ex.getMessage()+"Data Not Found");
                genericDataDTO.setResponseCode(respCode);
                genericDataDTO.setResponseMessage(ex.getMessage()+"Data Not Found");
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update sub-businessVertical"+LogConstants.LOG_BY_NAME+entityDTO.getSbvname()+ LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
            } else if (ex instanceof CustomValidationException){
                response.put(APIConstants.ERROR_TAG, ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update sub-businessVertical"+LogConstants.LOG_BY_NAME+entityDTO.getSbvname()+ LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);

            } else {
                LOGGER.error(ex.getMessage(),ex);
                respCode = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                genericDataDTO.setResponseCode(respCode);
                genericDataDTO.setResponseMessage(ex.getMessage()+"Data Not Found");
                LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update sub-businessVertical"+LogConstants.LOG_BY_NAME+entityDTO.getSbvname()+ LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
            }
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SUB_BUSINESS_VERTICALS + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,HttpServletResponse res) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Search");
        MDC.put("userName",subBusinessVerticalService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter , req,res);
            if(genericDataDTO.getDataList().isEmpty()){
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Sub-Business Vertical using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.NULL_VALUE);
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Sub-Business Vertical using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Sub-Business Vertical using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + subBusinessVerticalService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);

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

}
