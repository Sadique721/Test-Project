package com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.Controller;


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
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.controller.BusinessUnitController;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.DTO.BusinessVerticalsDTO;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.Mapper.BusinessVerticalsMpper;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.Service.BusinessVerticalsService;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.domain.BusinessVerticals;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
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
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BUSINESS_VERTICALS)
public class BusinessVerticalsController extends ExBaseAbstractController<BusinessVerticalsDTO>
{
//    @Autowired
//    AuditLogService auditLogService;
    private static String MODULE = " [BusinessVerticalsController] ";

    @Autowired
    private MessageSender messageSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessUnitController.class);
    @Autowired
    BusinessVerticalsService businessVerticalsService;

    @Autowired
    private Tracer tracer;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    BusinessVerticalsMpper businessVerticalsMpper;



    public BusinessVerticalsController(BusinessVerticalsService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[BusinessVerticalController]";
    }

//   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_VERTICALS_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody BusinessVerticalsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {

        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        MDC.put("userName", businessVerticalsService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try{
        if(getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }

        boolean flag = businessVerticalsService.duplicateVerifyAtSave(entityDTO.getVname());
        if (flag /*&& flagforUcode*/) {
            dataDTO = super.save(entityDTO, result, authentication, req,res);
            BusinessVerticalsDTO businessVerticalsDTO = (BusinessVerticalsDTO) dataDTO.getData();

            BusinessVerticals businessVerticals = businessVerticalsMpper.dtoToDomain(businessVerticalsDTO,new CycleAvoidingMappingContext());
            createDataSharedService.sendEntitySaveDataForAllMicroService(businessVerticals);
            respCode=APIConstants.SUCCESS;
            response.put(APIConstants.MESSAGE, "Successfully Created");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"create Business Verticals"+LogConstants.LOG_BY_NAME+entityDTO.getVname()+ LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
            //            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
//                    AclConstants.OPERATION_BUSINESS_VERTICALS_ADD, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
        } else{
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.BUSINESS_VERTICALS_NAME_EXITS);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_COUNTRY_MANAGEMENT);
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create Business Verticals"+LogConstants.LOG_BY_NAME+entityDTO.getVname() +  LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "Country with same name already exist" + LogConstants.LOG_STATUS_CODE +respCode);
        }
            return dataDTO;
        }catch(Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, "Input Size Exceeded");
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create Business varticals"+LogConstants.LOG_BY_NAME+entityDTO.getVname() +  LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE +respCode);
        }finally
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO ;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_VERTICALS_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody BusinessVerticalsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Update");
        MDC.put("userName", businessVerticalsService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try{
        if(getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        BusinessVerticals  old1=businessVerticalsService.getById(entityDTO.getId());
        BusinessVerticals oldClone = new BusinessVerticals(old1);
        boolean flag = businessVerticalsService.duplicateVerifyAtEdit(entityDTO.getVname(), entityDTO.getId());
        if (flag) {
            dataDTO = super.update(entityDTO, result, authentication, req,res);
            BusinessVerticalsDTO businessVerticalsDTO = (BusinessVerticalsDTO) dataDTO.getData();
            if(businessVerticalsDTO != null) {
                BusinessVerticals businessVerticals = businessVerticalsMpper.dtoToDomain(businessVerticalsDTO,new CycleAvoidingMappingContext());
                createDataSharedService.updateEntityDataForAllMicroService(businessVerticals);
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update Business Vertical"+ LogConstants.LOG_BY_NAME+entityDTO.getVname() + LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName() + " , Updated Business Vertical Details " + UpdateDiffFinder.getUpdatedDiff(oldClone,businessVerticals)+ LogConstants.LOG_STATUS +LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
//                        AclConstants.OPERATION_BUSINESS_VERTICALS_EDIT, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
            }
        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, "Input Size Exceeded");
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"update Business varticals"+ LogConstants.LOG_BY_NAME+entityDTO.getVname() +  LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR +  LogConstants.LOG_STATUS_CODE +respCode);


        }
            return dataDTO;
        }catch (CustomValidationException ce){
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"update business verticals"+LogConstants.LOG_BY_NAME+entityDTO.getVname() + LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE +respCode);
        }catch (Exception ex){
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, "Input Size Exceeded");
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"update Business varticals"+ LogConstants.LOG_BY_NAME+entityDTO.getVname() +  LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE +respCode);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO ;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody BusinessVerticalsDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        org.slf4j.MDC.put("type", "Delete");
//        GenericDataDTO dataDTO = new GenericDataDTO();
//            dataDTO = super.delete(entityDTO, authentication, req);
//            BusinessVerticalsDTO businessVerticalsDTO = (BusinessVerticalsDTO) dataDTO.getData();
//            if (businessVerticalsDTO != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
//                        AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
//                logger.info("Business Verticals  With name " + entityDTO.getVname() + " is deleted Successfully  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//        } else {
//            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//            dataDTO.setResponseMessage(DeleteContant.BUSINESS_VERTICALS_DELETE_EXIST);
//            logger.error("Unable to Delete Bank With name: "+entityDTO.getVname() +"  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value());
//        }
//        org.slf4j.MDC.remove("type");
//        return dataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_VERTICALS_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody BusinessVerticalsDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Delete");
        MDC.put("userName", businessVerticalsService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());

        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            businessVerticalsService.getEntityForUpdateAndDelete(entityDTO.getId());
            dataDTO = super.delete(entityDTO, authentication, req,res);
            businessVerticalsService.deleteBusinessVerticalMapping(entityDTO.getId());
            BusinessVerticalsDTO businessVerticalsDTO = (BusinessVerticalsDTO) dataDTO.getData();
            if (businessVerticalsDTO != null) {
                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
                //  AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
                BusinessVerticals businessVerticals = businessVerticalsMpper.dtoToDomain(businessVerticalsDTO, new CycleAvoidingMappingContext());
                businessVerticals.setIsDeleted(true);
                createDataSharedService.updateEntityDataForAllMicroService(businessVerticals);
                response.put(APIConstants.MESSAGE, "Successfully deleted");
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete business verticals"+LogConstants.LOG_BY_NAME+entityDTO.getVname() + LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS+ LogConstants.LOG_SUCCESS+" " + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            } else {

                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                dataDTO.setResponseMessage(DeleteContant.BUSINESS_VERTICALS_DELETE_EXIST);
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete business verticals"+LogConstants.LOG_BY_NAME+entityDTO.getVname()+LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName()+  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE +respCode);
            }
        } catch (CustomValidationException ce) {

            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete business verticals"+LogConstants.LOG_BY_NAME+entityDTO.getVname() + LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE +respCode);
        } catch (Exception e) {

            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            dataDTO.setResponseCode(respCode);
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete business verticals"+LogConstants.LOG_BY_NAME+entityDTO.getVname()+ LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE +respCode);
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

//        @Override
//        public GenericDataDTO getAllWithoutPagination () {
//            return super.getAllWithoutPagination();
//        }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination (HttpServletRequest req,HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<BusinessVerticalsDTO> list = businessVerticalsService.getAllEntities().stream().filter(businessVerticalsDTO -> !businessVerticalsDTO.getIsDeleted() && businessVerticalsDTO.getStatus().equalsIgnoreCase("ACTIVE")).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
//            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
//            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());

        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return genericDataDTO;

    }

//        @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW + "\")")
        @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_VERTICALS + "\")")
        @Override
        public GenericDataDTO getEntityById (@PathVariable String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
            long startTime = System.nanoTime();  // Start measuring
            try {
                MDC.put("type", "Fetch");
                GenericDataDTO dataDTO = super.getEntityById(id, req,res);
                BusinessVerticalsDTO businessUnitDTO = (BusinessVerticalsDTO) dataDTO.getData();
                List<Long> region_id = businessUnitDTO.getRegion_id().stream().distinct().collect(Collectors.toList());
                businessUnitDTO.setRegion_id(region_id);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
//                    AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getVname());
                MDC.remove("type");
                return dataDTO;
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
                res.addHeader("Server-Timing", "app;dur=" + durationInMs);
            }
        }

//        @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW + "\")")
        @Override
        public GenericDataDTO getAll (@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req,HttpServletResponse res){
            String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            long startTime = System.nanoTime();  // Start measuring
            try {
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                requestDTO = setDefaultPaginationValues(requestDTO);
                if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())

                    genericDataDTO = businessVerticalsService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
                            , requestDTO.getPageSize()
                            , requestDTO.getSortBy()
                            , requestDTO.getSortOrder()
                            , requestDTO.getFilters());

                else
                    genericDataDTO = businessVerticalsService.search(requestDTO.getFilters()
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
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
                genericDataDTO.setTotalRecords(0);
//                logger.error("Unable to fetch all Entities   :  request: { Module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
            }
            finally {
                long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
                res.addHeader("Server-Timing", "app;dur=" + durationInMs);
            }
            return genericDataDTO;
        }

//        @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW + "\")")
        @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_VERTICALS + "\")")
        public GenericDataDTO search (@RequestParam(required = false, defaultValue = "${request.defaultPage}") List<GenericSearchModel>
        page
                , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
                , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
                , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String  sortBy
                , @RequestBody Integer filter){
                return businessVerticalsService.search(page, pageSize, sortOrder, sortBy, filter);
        }

    @PostMapping("/getAllVerticalsByRegion")
    public GenericDataDTO getAllVerticalsByRegion(@RequestBody List<Long> regionId, HttpServletRequest req){
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Fetch");
        MDC.put("userName", businessVerticalsService.getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        BusinessVerticalsDTO businessVerticalsDTO = new BusinessVerticalsDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            BusinessVerticalsService businessVerticalsService = SpringContext.getBean(BusinessVerticalsService.class);
            genericDataDTO.setDataList(businessVerticalsService.getAllVerticalsByRegion(regionId));
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "fetch all business vertical list"+ LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +respCode);

        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch business vertical list" + LogConstants.REQUEST_BY + businessVerticalsService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE +respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

}
