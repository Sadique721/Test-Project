package com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.controller;


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
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.BusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.mapper.BusinessUnitMapper;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.model.BusinessUnitDTO;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.repository.BusinessUnitRepository;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.service.BusinessUnitService;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.service.InvestmentCodeService;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserBusinessUnitMapping.StaffUserBusinessUnitMappingRepository;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.messages.BusinessUnitMessage;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BUSINESS_UNIT)
public class BusinessUnitController extends ExBaseAbstractController<BusinessUnitDTO> {
//    @Autowired
//    AuditLogService auditLogService;
    private static String MODULE = " [BusinessUnitController] ";
    @Autowired
    BusinessUnitService businessUnitService;

    @Autowired
    private StaffUserBusinessUnitMappingRepository staffUserBusinessUnitMappingRepository;
    
    @Autowired
    private MessageSender messageSender;

    @Autowired
    BusinessUnitRepository businessUnitRepository;

    @Autowired
    InvestmentCodeService investmentCodeService;

    @Autowired
    BusinessUnitMapper businessUnitMapper;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    KafkaMessageSender kafkaMessageSender;

    public BusinessUnitController(BusinessUnitService service) {
        super(service);
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessUnitController.class);

    @Autowired
    private Tracer tracer;
    @Override
    public String getModuleNameForLog() {
        return "[BusinessUnitController]";
    }

    //Get All Business Unit with Pagination
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_UNIT + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req,HttpServletResponse res) {
        GenericDataDTO response = null;
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

    //Save Business Unit
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_UNIT_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody BusinessUnitDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        MDC.put("userName", businessUnitService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            boolean flag = businessUnitService.duplicateVerifyAtSave(entityDTO.getBuname());
            boolean flagforUcode = businessUnitService.duplicateVerifyAtSaveUcode(entityDTO.getBucode());

            if (flag && flagforUcode) {

                dataDTO = super.save(entityDTO, result, authentication, req,res);
                BusinessUnitDTO businessUnitDTO = (BusinessUnitDTO) dataDTO.getData();

                //send message
                BusinessUnitMessage businessUnitMessage = new BusinessUnitMessage();
                businessUnitMessage.setId(businessUnitDTO.getId());
                businessUnitMessage.setBuname(businessUnitDTO.getBuname());
                businessUnitMessage.setBucode(businessUnitDTO.getBucode());
                businessUnitMessage.setStatus(businessUnitDTO.getStatus());
                businessUnitMessage.setIsDeleted(businessUnitDTO.getIsDeleted());
                businessUnitMessage.setMvnoId(businessUnitDTO.getMvnoId());
                businessUnitMessage.setPlanBindingType(businessUnitDTO.getPlanBindingType());

                //messageSender.send(message,RabbitMqConstants.QUEUE_BUSINESS_UNIT,RabbitMqConstants.QUEUE_BUSINESS_UNIT_SEND_TASK_MGMT_SUCCESS,RabbitMqConstants.QUEUE_BUSINESS_UNIT_KPI);
                //messageSender.send(message, RabbitMqConstants.QUEUE_BUSINESS_UNIT);
                kafkaMessageSender.send(new KafkaMessageData(businessUnitMessage,businessUnitMessage.getClass().getSimpleName()));
                BusinessUnit businessUnit = businessUnitMapper.dtoToDomain(businessUnitDTO, new CycleAvoidingMappingContext());
                createDataSharedService.sendEntitySaveDataForAllMicroService(businessUnit);

                dataDTO.setResponseMessage("Successfully Created");
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Business Unit" + LogConstants.LOG_BY_NAME + entityDTO.getBuname() + LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
            } else if (!flagforUcode) {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.BUSINESS_UNIT_CODE_EXITS);
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create  Business Unit" + LogConstants.LOG_BY_NAME + entityDTO.getBuname() + LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + respCode);
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.BUSINESS_UNIT_NAME_EXITS);
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create  Business Unit" + LogConstants.LOG_BY_NAME + entityDTO.getBuname() + LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + respCode);
            }
            return dataDTO;
        }catch (CustomValidationException e){
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create  Business Unit"  +LogConstants.LOG_BY_NAME+entityDTO.getBuname()+  LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ respCode);
        }catch (Exception e){
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create  Business Unit"  +LogConstants.LOG_BY_NAME+entityDTO.getBuname()+  LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ respCode);
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"Create  Business Unit"  +LogConstants.LOG_BY_NAME+entityDTO.getBuname()+  LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;
    }

    //Update Business Unit
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_UNIT_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody BusinessUnitDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", businessUnitService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());


            if (getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
        long startTime = System.nanoTime();  // Start measuring
        try {
            GenericDataDTO dataDTO = new GenericDataDTO();
            boolean flag = businessUnitService.duplicateVerifyAtEdit(entityDTO.getBuname(), entityDTO.getId());
            boolean flagforUcode = businessUnitService.duplicateVerifyUcodeAtEdit(entityDTO.getBucode(), entityDTO.getId());

            if (flag && flagforUcode) {
                BusinessUnit old1 =  businessUnitService.getById(entityDTO.getId());
                BusinessUnit old1Clone = new BusinessUnit(old1);
                dataDTO = super.update(entityDTO, result, authentication, req,res);
                BusinessUnitDTO businessUnitDTO = (BusinessUnitDTO) dataDTO.getData();
                if (businessUnitDTO != null) {
                    //send message
                    BusinessUnitMessage message = new BusinessUnitMessage();
                    message.setId(businessUnitDTO.getId());
                    message.setBuname(businessUnitDTO.getBuname());
                    message.setBucode(businessUnitDTO.getBucode());
                    message.setStatus(businessUnitDTO.getStatus());
                    message.setIsDeleted(businessUnitDTO.getIsDeleted());
                    message.setMvnoId(businessUnitDTO.getMvnoId());
                    message.setPlanBindingType(businessUnitDTO.getPlanBindingType());

                    //messageSender.send(message, RabbitMqConstants.QUEUE_BUSINESS_UNIT,RabbitMqConstants.QUEUE_BUSINESS_UNIT_SEND_TASK_MGMT_SUCCESS,RabbitMqConstants.QUEUE_BUSINESS_UNIT_KPI);
                    BusinessUnit businessUnit = businessUnitMapper.dtoToDomain(businessUnitDTO, new CycleAvoidingMappingContext());
                    createDataSharedService.updateEntityDataForAllMicroService(businessUnit);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_UNIT,
//                AclConstants.OPERATION_BUSINESS_UNIT_EDIT, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getBuname());
                    dataDTO.setResponseMessage("Successfully Updated");
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update Business unit"+ LogConstants.LOG_BY_NAME + LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername() + " , Updated Business Unit Details " + UpdateDiffFinder.getUpdatedDiff(old1Clone,businessUnit)+ LogConstants.LOG_STATUS +" "+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

                }
            } else if (!flagforUcode) {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.BUSINESS_UNIT_CODE_EXITS);
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Business Unit"+ LogConstants.LOG_BY_NAME+entityDTO.getBuname()+ LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + respCode);
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.BUSINESS_UNIT_NAME_EXITS);
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Business Unit"+ LogConstants.LOG_BY_NAME+entityDTO.getBuname()+ LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + respCode);
            }
            return dataDTO;

        }catch (Exception ex){
            LOGGER.error(ex.getMessage(),ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update business Unit"+ LogConstants.LOG_BY_NAME+entityDTO.getBuname()+ LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);

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

    //Delete Business Unit
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_UNIT_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody BusinessUnitDTO entityDTO, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", businessUnitService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        long startTime = System.nanoTime();  // Start measuring
        try{
        GenericDataDTO dataDTO = new GenericDataDTO();
        boolean flag = businessUnitService.deleteVerification(entityDTO.getId().intValue());
        boolean flag2 = businessUnitService.deleteVerificationForSubBusinessunit(entityDTO.getId().intValue());
        if(flag && flag2){
            dataDTO = super.delete(entityDTO, authentication, req,res);
            BusinessUnitDTO businessUnitDTO = (BusinessUnitDTO) dataDTO.getData();

            if(businessUnitDTO != null) {
            	 //send message
                businessUnitService.deleteIcNameBumapping(businessUnitDTO.getId());
                BusinessUnitMessage message = new BusinessUnitMessage();
                message.setId(businessUnitDTO.getId());
                message.setBuname(businessUnitDTO.getBuname());
                message.setBucode(businessUnitDTO.getBucode());
                message.setStatus(businessUnitDTO.getStatus());
                message.setIsDeleted(true);
                message.setMvnoId(businessUnitDTO.getMvnoId());
                //messageSender.send(message, RabbitMqConstants.QUEUE_BUSINESS_UNIT, RabbitMqConstants.QUEUE_BUSINESS_UNIT_SEND_TASK_MGMT_SUCCESS,RabbitMqConstants.QUEUE_BUSINESS_UNIT_KPI);
                BusinessUnit businessUnit = businessUnitMapper.dtoToDomain(businessUnitDTO, new CycleAvoidingMappingContext());
                businessUnit.setIsDeleted(true);
                createDataSharedService.deleteEntityDataForAllMicroService(businessUnit);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_UNIT,
//                        AclConstants.OPERATION_BUSINESS_UNIT_DELETE, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getBuname());
                dataDTO.setResponseMessage("Successfully Deleted");
                respCode = APIConstants.SUCCESS;
                LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Business Unit"+ LogConstants.LOG_BY_NAME+entityDTO.getBuname()+ LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +respCode);

            }
        } else {
            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            dataDTO.setResponseMessage(DeleteContant.BUSINESS_UNIT_EXIST);
            respCode = HttpStatus.METHOD_NOT_ALLOWED.value();
            LOGGER.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Delete Business Unit"  +LogConstants.LOG_BY_NAME+entityDTO.getBuname()+ LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  +"Business Unit with same name already exist"+ LogConstants.LOG_STATUS_CODE + respCode);
        }
            return dataDTO;
        }catch (Exception e){
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"Delete Business Unit" +LogConstants.LOG_BY_NAME+entityDTO.getBuname() +LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE +respCode);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return null;
    }

    //Search Business Unit
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_UNIT + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,HttpServletResponse res) {
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Search");
        MDC.put("userName",businessUnitService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        long startTime = System.nanoTime();  // Start measuring

        MDC.put("spanId",traceContext.spanIdString());
        try {
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter , req,res);
            if(genericDataDTO.getDataList().isEmpty()){
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Business Unit using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.NULL_VALUE);
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Business Unit using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Business Unit using keyword : " +filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + APIConstants.EXPECTATION_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        } return genericDataDTO;
    }

    //Get Entity By Id
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_UNIT + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
        long startTime = System.nanoTime();  // Start measuring
        try {
            MDC.put("type", "Fetch");
            GenericDataDTO dataDTO = super.getEntityById(id, req,res);
            BusinessUnitDTO businessUnitDTO = (BusinessUnitDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_UNIT,
//                AclConstants.OPERATION_BUSINESS_UNIT_VIEW, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getBuname());
            // logger.info("Country Search Successfull  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),RESP_CODE);
            MDC.remove("type");
            return dataDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

    //Get all without Pagination
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return super.getAllWithoutPagination();
//    }
    @PreAuthorize("validatePermission(\"" + MenuConstants.Masters.BUSINESS_UNIT + "\")")
    @GetMapping("/BusinessUnit/{id}")
    public HashMap<String, Object> getBusinessUnitById(@PathVariable Long id, HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", businessUnitService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        BusinessUnitDTO businessUnitDTO = new BusinessUnitDTO();
        try{
            if (id==null){
                respCode=APIConstants.NOT_FOUND;
                response.put(APIConstants.ERROR_TAG, "Country Not Found!");
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Business Unit"+ LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE +  respCode);
            }else {
                Optional<BusinessUnit> businessUnit = businessUnitRepository.findById(id);
                if (businessUnit.isPresent()){
                    List<String> icnames=investmentCodeService.getIcnameListByBuId(id);
                    BusinessUnitDTO pojo=businessUnitService.convertBumodeltoPojo(businessUnit);
                    pojo.setIcnames(icnames);
                    response.put("BuById",pojo);
                    respCode = APIConstants.SUCCESS;
                    LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Business Unit"+ LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +respCode);
                }else {
                    respCode = APIConstants.NOT_FOUND;
                    LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "fetch Business Unit"+ LogConstants.REQUEST_BY + businessUnitService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE +respCode);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        MDC.remove("type");
        return  response;
    }



//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_VIEW + "\")")
    @GetMapping(value = "/getBUFromStaff")
    public GenericDataDTO getBUFromStaff(HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO dataDTO = businessUnitService.getBUFromStaff();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_UNIT,
//                AclConstants.OPERATION_BUSINESS_UNIT_VIEW, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getBuname());
        // logger.info("Country Search Successfull  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),RESP_CODE);
        MDC.remove("type");
        return dataDTO;
    }

    @GetMapping(value = "/getBUFromCurrentStaff")
    public GenericDataDTO getBUFromCurrentStaff(HttpServletRequest req) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO dataDTO = businessUnitService.getBUFromCurrentStaff();
        MDC.remove("type");
        return dataDTO;
    }

    @Override
    public GenericDataDTO getAllWithoutPagination (HttpServletRequest req,HttpServletResponse res) {

            Integer respCode = APIConstants.FAIL;
            HashMap<String, Object> response = new HashMap<>();
            GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        long startTime = System.nanoTime();  // Start measuring
        try {
            List<BusinessUnitDTO> list = businessUnitService.getAllEntities().stream().filter(x -> !x.getIsDeleted() && x.getStatus().equalsIgnoreCase("ACTIVE")).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            respCode = APIConstants.SUCCESS;
            return genericDataDTO;
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");

        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return genericDataDTO;

    }
}
