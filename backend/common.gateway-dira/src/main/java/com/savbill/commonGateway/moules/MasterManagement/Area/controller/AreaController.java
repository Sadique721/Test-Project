package com.savbill.commonGateway.moules.MasterManagement.Area.controller;


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
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area;
import com.savbill.commonGateway.moules.MasterManagement.Area.mapper.AreaMapper;
import com.savbill.commonGateway.moules.MasterManagement.Area.model.AreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.Area.service.AreaService;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.mapper.PincodeMapper;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.repository.PincodeRepository;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.messages.AreaMessage;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.AREA)
public class AreaController extends ExBaseAbstractController<AreaDTO> {

//    @Autowired
//    AuditLogService auditLogService;
    private static String MODULE = " [AreaController] ";
    @Autowired
    private AreaService areaService;


    @Autowired
    private MessageSender messageSender;


    @Autowired
    CreateDataSharedService createDataSharedService;


    @Autowired
    AreaMapper areaMapper;

    @Autowired
    PincodeRepository pincodeRepository;

    @Autowired
    PincodeMapper pincodeMapper;

    private AreaDTO areaDTO;
    @Autowired
    private Tracer tracer;


    private static final Logger LOGGER = LoggerFactory.getLogger(AreaController.class);

    public AreaController(AreaService service) {
        super(service);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.AREA + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, HttpServletResponse res) {
        long startTime = System.nanoTime();  // Start measuring
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.getAll(requestDTO,req,res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",areaService.getLoggedInUser().getUsername());
//        String traceids = (String) MDC.get(LogConstants.TRACE_ID);
//        MDC.put("traceId",traceids);
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
           dataDTO= super.getEntityById(id, req,res);
        AreaDTO areaDTO = (AreaDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_AREA,
//                AclConstants.OPERATION_AREA_VIEW, req.getRemoteAddr(), null, areaDTO.getId(), areaDTO.getName());
        Integer respCode = APIConstants.SUCCESS;
        LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom" )+LogConstants.REQUEST_FOR +"Fetch Entity" + LogConstants.LOG_BY_NAME+ id +  LogConstants.REQUEST_BY +areaService.getLoggedInUser().getUsername()  + LogConstants.LOG_STATUS +LogConstants.LOG_SUCCESS  +LogConstants.LOG_STATUS_CODE+respCode );
        return dataDTO;
    }catch (Exception ex){
        int respCode = HttpStatus.NOT_ACCEPTABLE.value();
        LOGGER.error(LogConstants.REQUEST_FROM  + req.getHeader("Request Form") + LogConstants.REQUEST_FOR+"fetch Entity"+ LogConstants.LOG_BY_NAME+id+ LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername()  + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ ex.getMessage() +LogConstants.LOG_STATUS_CODE +respCode);
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

    @Override
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try{
            genericDataDTO = areaService.getAllEntityWithoutPagination();
        }catch (Exception ex){
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            LOGGER.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }
    
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.AREA_CREATE+ "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody AreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Create");
        MDC.put("userName",areaService.getLoggedInUser().getUsername() );
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());


        if(getMvnoIdFromCurrentStaff() != null) {
    		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
    	}
    	boolean flag = areaService.duplicateVerifyAtSave(entityDTO.getName(),entityDTO.getCountryId(),entityDTO.getStateId(),entityDTO.getCityId(),entityDTO.getPincodeId());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
        if (flag) {
        	dataDTO = super.save(entityDTO, result, authentication, req,res);
            AreaDTO areaDTO = (AreaDTO) dataDTO.getData();
//            //RabbitMq
            AreaMessage areaMessage = new AreaMessage(areaDTO);
            //this.messageSender.send(areaMessage, RabbitMqConstants.QUEUE_AREA);
            Area area = areaMapper.dtoToDomain(areaDTO,new CycleAvoidingMappingContext());
            createDataSharedService.sendEntitySaveDataForAllMicroService(area);
            respCode= APIConstants.SUCCESS;
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_AREA,
//                    AclConstants.OPERATION_AREA_ADD, req.getRemoteAddr(), null, areaDTO.getId(), areaDTO.getName());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom" )+LogConstants.REQUEST_FOR +"Create area"+ LogConstants.LOG_BY_NAME+entityDTO.getName() +  LogConstants.REQUEST_BY +areaService.getLoggedInUser().getUsername()  + LogConstants.LOG_STATUS +LogConstants.LOG_SUCCESS  +LogConstants.LOG_STATUS_CODE+respCode );
        } else {
        	dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +LogConstants.REQUEST_FOR +"Create area"+ LogConstants.LOG_BY_NAME + entityDTO.getName()+ LogConstants.REQUEST_BY +areaService.getLoggedInUser().getUsername()  +LogConstants.LOG_STATUS +LogConstants.LOG_FAILED +  LogConstants.LOG_ERROR +LogConstants.LOG_STATUS_CODE+ respCode  );

        }
        }catch (Exception ex ) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom" )+ LogConstants.REQUEST_FOR + "create area" + LogConstants.LOG_BY_NAME +  entityDTO.getName()+LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername()  + LogConstants.LOG_STATUS +LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        } finally
         {
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");
        long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
        res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO;
    }
    
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.AREA+ "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest req,HttpServletResponse res) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Search");
        MDC.put("userName",areaService.getLoggedInUser().getUsername() );
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
       try {
           genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter , req,res);
           if (genericDataDTO.getDataList().isEmpty()){
               LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search area using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername()  + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_NOT_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.NULL_VALUE);
           }else
           LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search area using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername()  + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
       }catch (Exception ex){
           LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search area using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername()  + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);

       }finally {
           MDC.remove("type");
           MDC.remove("userName");
           MDC.remove("traceId");
           long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
           res.addHeader("Server-Timing", "app;dur=" + durationInMs);
       }
        return genericDataDTO;

}

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.AREA_EDIT+ "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody AreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        int respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", areaService.getLoggedInUser().getUsername() );
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        if(getMvnoIdFromCurrentStaff() != null) {
    		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
    	}


    	GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            areaService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean flag = areaService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId(), entityDTO.getCountryId(), entityDTO.getStateId(), entityDTO.getCityId(), entityDTO.getPincodeId());
            if (flag) {
                Area old=areaService.getById(entityDTO.getId());
                Area oldClone = new Area(old);
                dataDTO = super.update(entityDTO, result, authentication, req,res);
                AreaDTO areaDTO = (AreaDTO) dataDTO.getData();
                AreaMessage areaMessage = new AreaMessage(areaDTO);
                //this.messageSender.send(areaMessage, RabbitMqConstants.QUEUE_AREA);
                Area area = areaMapper.dtoToDomain(areaDTO, new CycleAvoidingMappingContext());
                createDataSharedService.updateEntityDataForAllMicroService(area);
               String respCodes = LogConstants.LOG_SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update Area" + LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername()  + " , Updated Area Service Details" + UpdateDiffFinder.getUpdatedDiff(oldClone,area)+ " " + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
//            if(areaDTO != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_AREA,
//                        AclConstants.OPERATION_AREA_EDIT, req.getRemoteAddr(), null, areaDTO.getId(), areaDTO.getName());
//            }
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Area"+ LogConstants.LOG_BY_NAME + entityDTO.getName()+ LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername() +  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE+ respCode);
            }
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Area"+ LogConstants.LOG_BY_NAME + entityDTO.getName()+ LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername() +  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Area"+ LogConstants.LOG_BY_NAME+entityDTO.getName()+ LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername() +  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
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

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.AREA_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody AreaDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        //GenericDataDTO dataDTO = super.delete(entityDTO, authentication, req);
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "Delete");
        MDC.put("userName", areaService.getLoggedInUser().getUsername() );
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try {
            Pincode pincode=pincodeRepository.findById(Long.valueOf(entityDTO.getPincodeId())).orElse(null);
            entityDTO.setPincode(pincodeMapper.domainToDTO(pincode,new CycleAvoidingMappingContext()));
            areaService.getEntityForUpdateAndDelete(entityDTO.getId());
//            boolean flag = areaService.deleteVerification(pincode);
            boolean areaInUse = areaService.isAreaReferencedInSubArea(entityDTO.getId());
            if (!areaInUse) {
                dataDTO = super.delete(entityDTO, authentication, req,res);
                AreaDTO areaDTO = (AreaDTO) dataDTO.getData();
//            //RabbitMq
                AreaMessage areaMessage = new AreaMessage(areaDTO);
                areaMessage.setIsDeleted(true);
                //this.messageSender.send(areaMessage, RabbitMqConstants.QUEUE_AREA);
                Area area = areaMapper.dtoToDomain(areaDTO, new CycleAvoidingMappingContext());
                area.setIsDeleted(true);
                createDataSharedService.sendEntitySaveDataForAllMicroService(area);
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Area"+LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername()  + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+respCode);

//            if(areaDTO != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_AREA,
//                        AclConstants.OPERATION_AREA_DELETE, req.getRemoteAddr(), null, areaDTO.getId(), areaDTO.getName());
//                logger.info("Area With name "+ entityDTO.getName()+" Is Deleted Ducessfully  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//            }
            } else {
                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                dataDTO.setResponseMessage(DeleteContant.AREA_DELETE_EXIST);
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Area"+ LogConstants.LOG_BY_NAME + entityDTO.getName()+LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername() +  LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE +respCode);
            }
//            AreaDTO areaDTO = (AreaDTO) dataDTO.getData();
            //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_AREA,
            //      AclConstants.OPERATION_AREA_DELETE, req.getRemoteAddr(), null, areaDTO.getId(), areaDTO.getName());
        }  catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            respCode = HttpStatus.METHOD_NOT_ALLOWED.value();
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete Area"+LogConstants.LOG_BY_NAME+ entityDTO.getName() + LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername() +  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);

        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return dataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[AreaController]";
    }

    @GetMapping("/pincode")
    public ResponseEntity<?> getAreaByPincode(@RequestParam(required = true,value = "pincodeId") Long pincodeId , HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        AreaDTO entityDto = new AreaDTO();
        MDC.put("type", "Fetch");
        MDC.put("userName", areaService.getLoggedInUser().getUsername() );
        MDC.put("traceId",traceContext.spanIdString());
        MDC.put("spanId",traceContext.traceIdString());
        try {
//            List<Area> areaList = areaRepository.findAreasByPincode(pincodeId);
            List<Area> areaList = areaService.getAreaByPincodeId(pincodeId);
            Integer respCode = APIConstants.SUCCESS;
            response.put("areaList", areaList);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Area" + LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+LogConstants.LOG_STATUS_CODE+respCode);
            return apiResponse(respCode, response);
        } catch (Exception e) {
            Integer respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "fetch Area" + LogConstants.REQUEST_BY + areaService.getLoggedInUser().getUsername()  + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+LogConstants.LOG_STATUS_CODE+respCode);
            return apiResponse(respCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanid");
        }
    }
    @GetMapping("/allAreas")
    public ResponseEntity<GenericDataDTO> getAllAreas(HttpServletRequest req,HttpServletResponse res) {
        GenericDataDTO response = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            response = areaService.getAllAreasWithDetails();
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            LOGGER.error("Error fetching area data: {}", ex.getMessage(), ex);
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Failed to load area data");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }
}
