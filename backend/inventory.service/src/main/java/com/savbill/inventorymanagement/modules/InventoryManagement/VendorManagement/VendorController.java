package com.savbill.inventorymanagement.modules.InventoryManagement.VendorManagement;

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
import com.savbill.inventorymanagement.kafka.KafkaConstant;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.modules.constants.UpdateDiffFinder;
import com.savbill.inventorymanagement.rabbitmq.MessageSender;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveUpdateVendorMessage;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.VENDOR)
@Api(value = "VendorController", description = "REST APIs related to Vendor Entity!!!!", tags = "vendor_Controller")
public class VendorController extends ExBaseAbstractController<VendorDto> {
    private static String MODULE = " [ManufacturerController] ";
    private static final Logger LOGGER = Logger.getLogger(VendorController.class);

    @Autowired
    VendorRepo vendorRepo;

    @Autowired
    VendorService vendorService;

    @Autowired
    VendorMapper vendorMapper;

    @Autowired
    Tracer tracer;


    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    public VendorController(VendorService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[VendorController]";
    }

    /**
     Save Vendor/ Manufacturer API
     * @Author Darshan
     * @param vendorDto
     * @param result
     * @param req
     * @return
     * @throws Exception
     */
    @Override
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Manufacturer.MANUFACTURER_CREATE + "\")")
    public GenericDataDTO save(@RequestBody VendorDto vendorDto, BindingResult result, HttpServletRequest req) throws Exception {
        String SUBMODULE = getModuleNameForLog() + "[save()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            if (vendorDto.getId() == null) {
                if(vendorDto.getName()!=null && (vendorDto.getName().trim().equalsIgnoreCase("")))
                {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(MessageConstants.INPUT_EMPTY_ERROR);
                }
                else if (vendorDto.getName().length() > 250) {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(MessageConstants.INPUT_SIZE_ERROR);
                } else {
                    boolean flag = vendorService.duplicateVarification(vendorDto, CommonConstants.OPERATION_ADD);
                    if (flag) {
                        if (getMvnoIdFromCurrentStaff() != null) {
                            vendorDto.setMvnoId(getMvnoIdFromCurrentStaff());
                        }
                        VendorDto vendorDto1 = vendorService.saveEntity(vendorDto);
                        SaveUpdateVendorMessage saveUpdateVendorMessage = new SaveUpdateVendorMessage(vendorDto1.getId(),vendorDto1.getName(),vendorDto1.getStatus(),vendorDto1.isDeleted(),vendorDto1.getMvnoId());
//                        messageSender.send(saveUpdateVendorMessage, RabbitMqConstants.QUEUE_SEND_SAVE_VENDOR_QUEUE);
                        kafkaMessageSender.send(new KafkaMessageData(saveUpdateVendorMessage,SaveUpdateVendorMessage.class.getSimpleName(), KafkaConstant.SAVE_VENDOR));
                        genericDataDTO.setData(vendorDto1);
                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
                        genericDataDTO.setResponseMessage(MessageConstants.CREATE_SUCCESSFUL);
                        LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Manufacture" +LogConstant.LOG_BY_NAME+vendorDto.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

                    }else {
                        genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        genericDataDTO.setResponseMessage(MessageConstants.VENDOR_NAME);
                        LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "create manufacturer" + LogConstant.LOG_BY_NAME+vendorDto.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

                    }
                }
            }
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "create manufacturer" + LogConstant.LOG_BY_NAME + vendorDto.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.EXPECTATION_FAILED.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /**
     Update Vendor/Manufacturer API
     * @Author Darshan
     * @param vendorDto
     * @param result
     * @param req
     * @return
     * @throws Exception
     */
    @Override
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Manufacturer.MANUFACTURER_EDIT + "\")")
    public GenericDataDTO update(@RequestBody VendorDto vendorDto, BindingResult result, HttpServletRequest req) throws Exception {
        String SUBMODULE = getModuleNameForLog() + "[update()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            Vendor old = vendorService.getById(vendorDto.getId());
            Vendor oldClone = new Vendor(old);
            if (vendorDto.getId() != null) {
                vendorService.getEntityForUpdateAndDelete(vendorDto.getId());
                if (vendorDto.getName().length() > 250) {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(MessageConstants.INPUT_SIZE_ERROR);
                } else {
                    boolean flag = vendorService.duplicateVarification(vendorDto, CommonConstants.OPERATION_UPDATE);
                    if(flag) {
                        if (getMvnoIdFromCurrentStaff() != null) {
                            vendorDto.setMvnoId(getMvnoIdFromCurrentStaff());
                        }
                        VendorDto vendorDto1 = vendorService.updateEntity(vendorDto);
                        SaveUpdateVendorMessage saveUpdateVendorMessage = new SaveUpdateVendorMessage(vendorDto1.getId(),vendorDto1.getName(),vendorDto1.getStatus(),vendorDto1.isDeleted(),vendorDto1.getMvnoId());
//                        messageSender.send(saveUpdateVendorMessage, RabbitMqConstants.QUEUE_SEND_UPDATE_VENDOR_QUEUE);
                        kafkaMessageSender.send(new KafkaMessageData(saveUpdateVendorMessage,SaveUpdateVendorMessage.class.getSimpleName(),KafkaConstant.UPDATE_VENDOR));
                        genericDataDTO.setData(vendorDto1);
                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
                        genericDataDTO.setResponseMessage(MessageConstants.UPDATE_SUCCESSFUL);
                        Vendor vendor = vendorMapper.dtoToDomain(vendorDto , new CycleAvoidingMappingContext());
                        LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Manufacturer" + LogConstant.LOG_BY_NAME+vendorDto.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + " , Updated Details " + UpdateDiffFinder.getUpdatedDiff(oldClone , vendor)+ LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

                    } else {
                        genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        genericDataDTO.setResponseMessage(MessageConstants.VENDOR_NAME);
                        LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Manufacturer" + LogConstant.LOG_BY_NAME+vendorDto.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

                    }
                }
            }
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to update manufacturer with name " + vendorDto.getName() + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Manufacturer" + LogConstant.LOG_BY_NAME+vendorDto.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  +APIConstants.ERROR_MESSAGE+e.getMessage()+ LogConstant.LOG_STATUS_CODE+HttpStatus.EXPECTATION_FAILED.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /**
     Delete Vendor/Manufacturer API
     * @Author Darshan
     * @param id
     * @param request
     * @return
     * @throws Exception
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Manufacturer.MANUFACTURER_DELETE + "\")")
    public GenericDataDTO delete(@PathVariable("id") Long id, HttpServletRequest request) throws Exception {
        String SUBMODULE = getModuleNameForLog() + "[delete()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, request.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        VendorDto vendorDto2 = new VendorDto();
        try {
            vendorService.getEntityForUpdateAndDelete(id);
            boolean flag = vendorService.deleteVerification(Math.toIntExact(id));
            if (flag) {
                Vendor vendor = vendorService.deleteEntity(id);
                SaveUpdateVendorMessage saveUpdateVendorMessage = new SaveUpdateVendorMessage(vendor.getId(),vendor.getName(),vendor.getStatus(),vendor.isDeleted(),vendor.getMvnoId());
//                messageSender.send(saveUpdateVendorMessage, RabbitMqConstants.QUEUE_SEND_UPDATE_VENDOR_QUEUE);
                kafkaMessageSender.send(new KafkaMessageData(saveUpdateVendorMessage,SaveUpdateVendorMessage.class.getSimpleName()));
                VendorDto vendorDto = vendorMapper.domainToDTO(vendor, new CycleAvoidingMappingContext());
                genericDataDTO.setData(vendorDto);
                genericDataDTO.setResponseMessage(MessageConstants.DELETE_SUCCESSFUL);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                LOGGER.info(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Manufacturer"+LogConstant.LOG_BY_NAME+vendorDto.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

            } else {
                Vendor vendor = vendorService.getById(id);
                VendorDto vendorDto = vendorMapper.domainToDTO(vendor, new CycleAvoidingMappingContext());
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(DeleteContant.VENDOR_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Manufacturer"+LogConstant.LOG_BY_NAME+vendorDto.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED +LogConstant.LOG_ERROR+DeleteContant.POP_MANAGMENET_DELETE_EXIST  + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL );
            }

        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Manufacturer" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE +ex.getMessage() +LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value() );

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /**
     Find Vendor/Manufacturer By Id
     * @Author Darshan
     * @param id
     * @return
     */
    @GetMapping("/getById")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Manufacturer.MANUFACTURER +"\")")
    public GenericDataDTO getVendorByID(@RequestParam(name = "id") Long id, HttpServletRequest request) {
        String SUBMODULE = getModuleNameForLog() + "[getVendorByID()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, request.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setData(vendorService.getVendor(id));
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            LOGGER.info(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Manufacturer" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (CustomValidationException exception) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(exception.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Manufacturer"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED+APIConstants.ERROR_MESSAGE +exception.getMessage() + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /**
     Search Vendor/ Manufacturer API
     * @Author Darshan
     * @param page
     * @param pageSize
     * @param sortOrder
     * @param sortBy
     * @param filter
     * @return
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POP_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_POP_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Manufacturer.MANUFACTURER +"\")")
    @Override
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
            genericDataDTO= super.search(page, pageSize, sortOrder, sortBy, filter,req);
            if(genericDataDTO.getDataList().isEmpty()) {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Manufacture Management using Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS+LogConstant.LOG_FAILED + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }else
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Search Manufacture Management using Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search Manufacture Management using Keyword : "+ filter.getFilter().get(0).getFilterValue() +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +APIConstants.EXPECTATION_FAILED + APIConstants.ERROR_MESSAGE+ ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }  return genericDataDTO;
    }

    /**
     Get List of All Vendor/Manufacturer API
     * @Author Darshan
     * @param requestDTO
     * @return
     */
    @PostMapping("/getAllVendor")
    @Override
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Manufacturer.MANUFACTURER +"\")")
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO ) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.getAll(requestDTO);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    /**
     Get All Active Vendor/Manufacturer for UI Dropdown
     * @Author Darshan
     * @return
     */
    @GetMapping("/findAll")
    public GenericDataDTO findAll() {
        return vendorService.findAllVendor();
    }

//    To Get the logger in user first Name
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

}
