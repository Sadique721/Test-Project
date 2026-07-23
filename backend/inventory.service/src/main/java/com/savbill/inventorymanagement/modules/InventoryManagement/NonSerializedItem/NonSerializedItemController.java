package com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.acl.constants.AclConstants;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.modules.constants.UpdateDiffFinder;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Api(value = "NonSerializedItemController", description = "REST APIs related to non-serialized item Entity!!!!", tags = "non-serialized-item-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.NON_SERIALIZED_ITEM_MANAGEMENT)
public class NonSerializedItemController extends ExBaseAbstractController<NonSerializedItemDto> {

    @Autowired
    NonSerializedItemServiceImpl nonSerializedItemService;

    private static String MODULE = " [NonSerializedItemController] ";


    @Autowired
    NonSerializedItemRepository nonSerializedItemRepository;

    @Autowired
    InwardRepository inwardRepository;

    @Autowired
    private Tracer tracer;

    private final Logger LOGGER = Logger.getLogger(NonSerializedItemController.class);

    public NonSerializedItemController(NonSerializedItemServiceImpl nonSerializedItemService) {
        super(nonSerializedItemService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[NonSerializedItemController]";
    }


    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_VIEW + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ADD + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody NonSerializedItemDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        try {
            boolean flag = nonSerializedItemService.duplicateVerifyAtSave(entityDTO.getName());
            if (flag) {
                if (getMvnoIdFromCurrentStaff() != null) {
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                }
                NonSerializedItemDto entity = nonSerializedItemService.saveEntity(entityDTO);
                genericDataDTO.setData(entity);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Non Serialized Item"+ LogConstant.LOG_BY_NAME+entityDTO.getName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.NON_SERIALIZED_ITEM_NAME_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR  + "create olt slot"+LogConstant.LOG_BY_NAME+entityDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + LogConstant.LOG_DUPLICATE_RECORD_FOUND +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR  + "create olt slot" + LogConstant.LOG_BY_NAME+entityDTO.getName() +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_VIEW + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page,
                                 @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize,
                                 @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder,
                                 @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy,
                                 @RequestBody GenericSearchDTO filter, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO=super.search(page, pageSize, sortOrder, sortBy, filter ,req);
            if(genericDataDTO.getDataList().isEmpty()) {

                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Non-SerializeedItem By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS+LogConstant.LOG_FAILED + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }else
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Search Non-SerializeedItem By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        }catch (Exception ex){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search Non-SerializeedItem By Keyword : "+ filter.getFilter().get(0).getFilterValue() +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +APIConstants.EXPECTATION_FAILED+ APIConstants.ERROR_MESSAGE +ex.getMessage()+ LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody NonSerializedItemDto entityDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        NonSerializedItemDto nonSerializedItemDto=new NonSerializedItemDto();
        try {
            if (getMvnoIdFromCurrentStaff() != null) {
              entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            boolean flag = nonSerializedItemService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                dataDTO = super.delete(entityDTO, req);
            }
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleting NonSerialized Item" + LogConstant.LOG_BY_NAME +entityDTO.getName() +LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        }catch (Exception ex){
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.NON_SERIALIZED_ITEM_NAME_EXITS);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleting NonSerialized Item" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  +LogConstant.LOG_ERROR + LogConstant.LOG_NO_RECORD_FOUND +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody NonSerializedItemDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        boolean flag = nonSerializedItemService.duplicateVerifyAtEdit(entityDTO.getName(), (entityDTO.getId()));
        if (flag) {
            NonSerializedItemDto nonSerializedItemDto = nonSerializedItemService.getEntityById(entityDTO.getId());
            if(getMvnoIdFromCurrentStaff() != null) {
                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            dataDTO = super.update(entityDTO, result, req);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+LogConstant.REQUEST_FOR + "Update olt slot"+LogConstant.LOG_BY_NAME+entityDTO.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + " Update olt slot " + UpdateDiffFinder.getUpdatedDiff(nonSerializedItemDto,entityDTO)+ LogConstant.LOG_STATUS+LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(MessageConstants.NON_SERIALIZED_ITEM_NAME_EXITS);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update olt slot" +  LogConstant.LOG_BY_NAME+entityDTO.getName()+LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + LogConstant.LOG_DUPLICATE_RECORD_FOUND +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");

        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_ALL + "\",\"" + AclConstants.OPERATION_NON_SERIALIZED_ITEM_VIEW + "\")")
//    @PostMapping("/searchItems")
//    public GenericDataDTO searchNonSerializedItems(@RequestBody PaginationRequestDTO requestDTO, @ModelAttribute SearchItemsPojo entity) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if (entity != null) {
//                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                requestDTO = setDefaultPaginationValues(requestDTO);
//                NonSerializedItemServiceImpl nonSerializedItemService1 = SpringContext.getBean(NonSerializedItemServiceImpl.class);
//                genericDataDTO = nonSerializedItemService1.searchItems(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), entity);
//            }
//            if (genericDataDTO.getDataList().isEmpty()) {
//                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "No Data Found", null);
//            }
//        } catch (CustomValidationException ce) {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//        } catch (Exception ex) {
//            genericDataDTO.setTotalRecords(0);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return genericDataDTO;
//    }
//
//
//    @RequestMapping(value = "/getAllSuibiuseItem/currentInwardId", method = RequestMethod.GET)
//    public List<ItemDto> getAllSuibsuOwnedItem(@RequestParam("currentInwardId") Long currentInwardId) {
//        List<ItemDto> itemList = null;
//        try {
//            itemList = itemService.findItemsSuibiseOwned(currentInwardId);
//        } catch (Exception ex) {
//            throw new RuntimeException(ex.getMessage());
//        }
//        return itemList;
//    }
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
