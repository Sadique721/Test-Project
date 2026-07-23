package com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.*;
import com.savbill.inventorymanagement.core.constants.*;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.modules.constants.UpdateDiffFinder;
import com.savbill.inventorymanagement.rabbitmq.MessageSender;
import com.savbill.inventorymanagement.rabbitmq.PopManagementMessage;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.DecimalFormat;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.POP_MANAGEMENT)
public class PopManagementController extends ExBaseAbstractController<PopManagementDTO> {
    
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    PopManagementMapper popManagementMapper;

    @Autowired
    PopManagementService popManagementService;
    private static final Logger LOGGER= Logger.getLogger(PopManagementController.class);

    public PopManagementController(PopManagementService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PopManagementController]";
    }

    //Get All PopManagement With Pagination
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Pop.POP + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO){
        return super.getAll(requestDTO);
    }
    @Autowired
    Tracer tracer;
    //Save Pop Management
    @PreAuthorize("validatePermission(\"" +ACLMenuConstants.Pop.POP_CREATE+ "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody PopManagementDTO popManagementDTO, BindingResult result, HttpServletRequest request) throws Exception {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, request.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        if(getMvnoIdFromCurrentStaff() != null) {
            popManagementDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        boolean flag = popManagementService.duplicateVarification(popManagementDTO, CommonConstants.OPERATION_ADD);
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
        if (flag){
            dataDTO = super.save(popManagementDTO, result, request);
            PopManagementDTO popManagementDTO1 = (PopManagementDTO) dataDTO.getData();
            //send message
            PopManagementMessage popManagementMessage = new PopManagementMessage(popManagementDTO1);
//            this.messageSender.send(popManagementMessage, RabbitMqConstants.QUEUE_APIGW_SEND_POP_MANAGEMENT);
            kafkaMessageSender.send(new KafkaMessageData(popManagementMessage, PopManagementMessage.class.getSimpleName()));
            LOGGER.info(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Pop management" +LogConstant.LOG_BY_NAME+ popManagementDTO.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_POP_MANAGEMENT,AclConstants.OPERATION_POP_MANAGEMENT_ADD, request.getRemoteAddr(), null, popManagementDTO1.getId(), popManagementDTO1.getName());
        }
        else{
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.POP_MANAGEMENT_NAME_EXITS);
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Pop management" +LogConstant.LOG_BY_NAME+popManagementDTO.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + LogConstant.LOG_STATUS_CODE+APIConstants.FAIL);

        }

        }catch (Exception ex ){
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Pop management" + LogConstant.LOG_BY_NAME+popManagementDTO.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE+HttpStatus.METHOD_NOT_ALLOWED);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    //Search Pop Management
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POP_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_POP_MANAGEMENT_VIEW + "\")")
    @Override
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Pop.POP + "\")")
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter ,req);
            if(genericDataDTO.getDataList().isEmpty()){
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Search Pop management By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED+LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }else
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Search Pop management By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search pop management By Keyword : "+ filter.getFilter().get(0).getFilterValue() +LogConstant.REQUEST_BY +getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +APIConstants.EXPECTATION_FAILED + APIConstants.ERROR_MESSAGE +ex.getMessage()+ LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    // Get Entyty By Id
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Pop.POP + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = super.getEntityById(id, req);
        PopManagementDTO popManagementDTO = (PopManagementDTO) dataDTO.getData();

        if(popManagementDTO!=null)
        {
            DecimalFormat df = new DecimalFormat("0.0000000");

            if(popManagementDTO.getLatitude()!=null && !popManagementDTO.getLatitude().isEmpty() && isNumeric(popManagementDTO.getLatitude()))
                popManagementDTO.setLatitude(df.format(Double.parseDouble(popManagementDTO.getLatitude())));

            if(popManagementDTO.getLongitude()!=null && !popManagementDTO.getLongitude().isEmpty() && isNumeric(popManagementDTO.getLongitude()))
                popManagementDTO.setLongitude(df.format(Double.parseDouble(popManagementDTO.getLongitude())));
        }
        return dataDTO;
    }

//    //Get All without pagination
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return popManagementService.getAllWithoutPagination();
//    }

    //Update POP Management
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POP_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_POP_MANAGEMENT_EDIT + "\")")
    @Override
    @PreAuthorize("validatePermission(\"" +ACLMenuConstants.Pop.POP_EDIT+ "\")")
    public GenericDataDTO update(@Valid @RequestBody PopManagementDTO entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            PopManagement old = popManagementService.getPOPManagement(entityDTO.getId());
            PopManagement oldClone = new PopManagement(old);

            popManagementService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean flag = popManagementService.duplicateVarification(entityDTO, CommonConstants.OPERATION_UPDATE);
            PopManagementDTO popManagementDTO = null;
            if (flag) {
                if (getMvnoIdFromCurrentStaff() != null) {
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                }
                dataDTO = super.update(entityDTO, result, req);
                popManagementDTO = (PopManagementDTO) dataDTO.getData();
                //send message
                PopManagementMessage popManagementMessage = new PopManagementMessage(popManagementDTO);
//                this.messageSender.send(popManagementMessage, RabbitMqConstants.QUEUE_APIGW_SEND_POP_MANAGEMENT);
                kafkaMessageSender.send(new KafkaMessageData(popManagementMessage, PopManagementMessage.class.getSimpleName()));
                PopManagement popManagement = popManagementMapper.dtoToDomain(entityDTO , new CycleAvoidingMappingContext());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update POP management" + LogConstant.LOG_BY_NAME + popManagementDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + " , Updated Details" + UpdateDiffFinder.getUpdatedDiff(oldClone, popManagement) + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.POP_MANAGEMENT_NAME_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update POP management" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);

            }
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update POP management" +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    //Delete POP management
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POP_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_POP_MANAGEMENT_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" +ACLMenuConstants.Pop.POP_DELETE+ "\")")
    @Override
    public GenericDataDTO delete(@RequestBody PopManagementDTO entityDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            popManagementService.getEntityForUpdateAndDelete(entityDTO.getId());
            popManagementService.validatePOP(entityDTO);
            boolean flag = popManagementService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                dataDTO = super.delete(entityDTO, req);
                PopManagementDTO popManagementDTO = (PopManagementDTO) dataDTO.getData();
                if (popManagementDTO != null) {
                    //send message
                    PopManagementMessage popManagementMessage = new PopManagementMessage(popManagementDTO);
                    popManagementMessage.setIsDeleted(true);
                    kafkaMessageSender.send(new KafkaMessageData(popManagementMessage, PopManagementMessage.class.getSimpleName()));
//                    this.messageSender.send(popManagementMessage, RabbitMqConstants.QUEUE_APIGW_SEND_POP_MANAGEMENT);
//                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_POP_MANAGEMENT, AclConstants.OPERATION_POP_MANAGEMENT_EDIT, req.getRemoteAddr(), null, entityDTO.getId(), entityDTO.getName());
                    LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Pop Management"+LogConstant.LOG_BY_NAME+entityDTO.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

                }
            } else {
                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                dataDTO.setResponseMessage(DeleteContant.POP_MANAGMENET_DELETE_EXIST);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Pop Management"+LogConstant.LOG_BY_NAME+entityDTO.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR +DeleteContant.POP_MANAGMENET_DELETE_EXIST+LogConstant.LOG_STATUS_CODE + APIConstants.FAIL );
            }

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Pop Management"+LogConstant.LOG_BY_NAME+entityDTO.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED +APIConstants.ERROR_MESSAGE +e.getMessage()+ LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value() );
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

//    For get logged in user first name
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }


    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    @GetMapping("/api-count")
    public ResponseEntity<Integer> getApiCount() {
        int count = popManagementService.countApis();
        return ResponseEntity.ok(count);
    }
}
