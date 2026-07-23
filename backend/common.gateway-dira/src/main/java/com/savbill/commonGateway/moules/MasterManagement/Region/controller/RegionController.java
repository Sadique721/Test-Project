package com.savbill.commonGateway.moules.MasterManagement.Region.controller;



import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.Region.Mapper.RegionMapper;
import com.savbill.commonGateway.moules.MasterManagement.Region.domain.Region;
import com.savbill.commonGateway.moules.MasterManagement.Region.model.RegionDTO;
import com.savbill.commonGateway.moules.MasterManagement.Region.service.RegionService;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
//import liquibase.pro.packaged.E;
//import lombok.extern.java.Log;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.Region)
public class RegionController extends ExBaseAbstractController<RegionDTO> {

//    @Autowired
//    AuditLogService auditLogService;
    private static String MODULE = " [BusinessVerticalsController] ";

    @Autowired
    private MessageSender messageSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(RegionController.class);
    @Autowired
    RegionService regionService;

    @Autowired
    private Tracer tracer;


    @Autowired
    CreateDataSharedService createDataSharedService;


    @Autowired
    RegionMapper regionMapper;

    public RegionController(RegionService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[BusinessVerticalController]";
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.REGION_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody RegionDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", regionService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            boolean flag = regionService.duplicateVerifyAtSave(entityDTO.getRname());
            GenericDataDTO dataDTO = new GenericDataDTO();
            if (flag /*&& flagforUcode*/) {
                dataDTO = super.save(entityDTO, result, authentication, req,res);
                RegionDTO regionDTO = (RegionDTO) dataDTO.getData();
                Region region = new Region();
                region = regionMapper.dtoToDomain(regionDTO, new CycleAvoidingMappingContext());
                createDataSharedService.sendEntitySaveDataForAllMicroService(region);
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"create Region" +LogConstants.LOG_BY_NAME+entityDTO.getRname()+ LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS  +LogConstants.LOG_STATUS_CODE+respCode);
                response.put(APIConstants.ERROR_TAG, "Input size is Exceeded");
            } else {
                 respCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_COUNTRY_MANAGEMENT);
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.SAME_REGION_ALREADY_EXITS);
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create Region"+LogConstants.LOG_BY_NAME+entityDTO.getRname() +  LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Region with same name already exist"+LogConstants.LOG_STATUS_CODE+respCode);
            }
            return dataDTO;

        }catch (Exception ex){
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create Region"+LogConstants.LOG_BY_NAME+entityDTO.getRname() +  LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return null;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.REGION_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody RegionDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", regionService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            Region old1= regionService.getById(entityDTO.getId());
            Region oldClone = new Region(old1);
            GenericDataDTO dataDTO = new GenericDataDTO();
            boolean flag = regionService.duplicateVerifyAtEdit(entityDTO.getRname(), entityDTO.getId());
            if (flag) {
                dataDTO = super.update(entityDTO, result, authentication, req,res);
                RegionDTO businessVerticalsDTO = (RegionDTO) dataDTO.getData();
                if (businessVerticalsDTO != null) {
                    Region updatedRegion = regionMapper.dtoToDomain(businessVerticalsDTO, new CycleAvoidingMappingContext());
                    createDataSharedService.updateEntityDataForAllMicroService(updatedRegion);
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update region : "+LogConstants.LOG_BY_NAME +entityDTO.getRname()+ LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername() + " , Updated region Details "+ UpdateDiffFinder.getUpdatedDiff(oldClone,updatedRegion)+ LogConstants.LOG_STATUS +" "+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

                }
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.SAME_REGION_ALREADY_EXITS);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update region : "+LogConstants.LOG_BY_NAME+entityDTO.getRname() + LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername() +  LogConstants.LOG_STATUS +" "+LogConstants.LOG_FAILED+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

            }return dataDTO;
        }catch (Exception ex){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update region : "+LogConstants.LOG_BY_NAME +entityDTO.getRname()+ LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername() +  LogConstants.LOG_STATUS +" "+LogConstants.LOG_FAILED +APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return null;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.REGION_DELETE+ "\")")
    @Override
    public GenericDataDTO delete(@RequestBody RegionDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", regionService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            regionService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean flag = regionService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                dataDTO = super.delete(entityDTO, authentication, req,res);
                RegionDTO regionDTO = (RegionDTO) dataDTO.getData();
                if (regionDTO != null) {
                    Region region = regionMapper.dtoToDomain(regionDTO, new CycleAvoidingMappingContext());
                    region.setIsDeleted(true);
                    createDataSharedService.updateEntityDataForAllMicroService(region);
                    // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
                    //  AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
                    response.put(APIConstants.MESSAGE, "Successfully deleted");
                    respCode = APIConstants.SUCCESS;
                    LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete Region" +LogConstants.LOG_BY_NAME+entityDTO.getRname()+ LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+respCode);
                }
            } else {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                dataDTO.setResponseMessage(MessageConstants.REGION_IN_USE);
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete Region" +LogConstants.LOG_BY_NAME+entityDTO.getRname()+ LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+respCode);
            }
        } catch (CustomValidationException ce) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete Region" +LogConstants.LOG_BY_NAME+entityDTO.getRname()+ LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete Region" +LogConstants.LOG_BY_NAME+entityDTO.getRname() +LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO;

    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_VIEW + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<RegionDTO> list = regionService.getAllEntities().stream().filter(regionDTO -> !regionDTO.getIsDeleted() && regionDTO.getStatus().equalsIgnoreCase("ACTIVE")).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
//            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
//            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());

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

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.REGION + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
        long startTime = System.nanoTime();  // Start measuring
        try {
            MDC.put("type", "Fetch");
            GenericDataDTO dataDTO = super.getEntityById(id, req,res);
            RegionDTO regionDTO = (RegionDTO) dataDTO.getData();
            // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
            // AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getVname());
            MDC.remove("type");
            return dataDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req,HttpServletResponse res) {

        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())

                genericDataDTO = regionService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder()
                        , requestDTO.getFilters());

            else
                genericDataDTO = regionService.search(requestDTO.getFilters()
                        , requestDTO.getPage(), requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder());


            if (null != genericDataDTO) {
                //logger.info("Fetching data :  request: { From : {}}; Response : {Code{},Message:{};}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                //logger.info("Unable to fetch all Entities   :  request: { module : {}}; Response : {Code{},Message:{};}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to fetch all Entities   :  request: { Module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ex.getStackTrace());
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;

        // return super.getAll(requestDTO);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.REGION + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_VIEW + "\")")
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String
                                         sortBy, @RequestParam List<GenericSearchModel> filterList) {
        return regionService.search(filterList, page, pageSize, sortBy, sortOrder);
    }

    @PostMapping("/getAllRegionByBranchId")
    public GenericDataDTO getAllRegionByServiceArea(@RequestBody List<Long> branchId, HttpServletRequest req) {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", regionService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        RegionDTO entityDTO = new RegionDTO();
        try {
            RegionService regionService = SpringContext.getBean(RegionService.class);
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all region" +  LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+respCode);
        } catch (Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all region" + LogConstants.REQUEST_BY + regionService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
}
