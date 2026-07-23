package com.savbill.inventorymanagement.modules.InventoryManagement.ItemGroup;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.DeleteContant;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.ITEM_ASSEMBLY)
@Api(value = "ItemAssemblyController", description = "REST APIs related to ItemAssembly Entity!!!!", tags = "Item_Assembly_Controller")
public class ItemAssemblyController extends ExBaseAbstractController<ItemAssemblyDto> {

    @Autowired
    ItemAssemblyServiceImp itemAssemblyServiceImp;

    @Autowired
    Tracer tracer;

    @Autowired
    ItemAssemblyProductMappingRepo itemAssemblyProductMappingRepo;
    private final ItemAssemblyRepo itemAssemblyRepo;
    private static Logger LOGGER = Logger.getLogger(ItemAssemblyController.class);
    public ItemAssemblyController(ItemAssemblyServiceImp service,
                                  ItemAssemblyRepo itemAssemblyRepo) {
        super(service);
        this.itemAssemblyRepo = itemAssemblyRepo;
    }
   @PostMapping("/itemGroupSave")
    public GenericDataDTO save(@RequestBody ItemAssemblyDto entityDTO, HttpServletRequest req) throws Exception {
       TraceContext traceContext =tracer.currentSpan().context();
       MDC.put("type", "Create");
       MDC.put("userName", getLoggedInUser().getUsername());
       MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
       MDC.put("spanId", traceContext.spanIdString());

       GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {

            if (entityDTO.getItemAssemblyName().length() > 50) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.INPUT_SIZE_ERROR);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Item Group"+LogConstant.LOG_BY_NAME+entityDTO.getItemAssemblyName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + MessageConstants.INPUT_SIZE_ERROR +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            } else {
                    ItemAssemblyDto itemAssemblyDto = itemAssemblyServiceImp.saveEntity(entityDTO);
                    genericDataDTO.setData(itemAssemblyDto);
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Item Group" +LogConstant.LOG_BY_NAME+entityDTO.getItemAssemblyName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
                }

        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Unable To Save The Item Group" +LogConstant.LOG_BY_NAME+entityDTO.getItemAssemblyName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @Override
    public GenericDataDTO delete(@RequestBody ItemAssemblyDto entityDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());

        }
        boolean flag = itemAssemblyServiceImp.deleteVerification(entityDTO.getId().intValue());
        if (flag) {
            itemAssemblyRepo.deleteById(entityDTO.getId());
            dataDTO.setResponseMessage(MessageConstants.DELETE_SUCCESSFUL);
            dataDTO.setResponseCode(HttpStatus.OK.value());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete item group"+LogConstant.LOG_BY_NAME + entityDTO.getItemAssemblyName()+" And By Id : " +entityDTO.getId()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(DeleteContant.ITEM_ASSEMBLY_EXIST);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete item group"+ LogConstant.LOG_BY_NAME + entityDTO.getItemAssemblyName()+" And By Id : "+entityDTO.getId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + DeleteContant.ITEM_ASSEMBLY_EXIST +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");

        return dataDTO;

    }



    @PostMapping("/getAllItemGroup")
    public GenericDataDTO getAllItemGroup(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fatch All Items Group" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO = itemAssemblyServiceImp.getAllItemProduct(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());

            return genericDataDTO;

        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fatch All Items Group" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            throw new RuntimeException(ex.getMessage());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }


    @PostMapping("/searchByNameItemGroup")
    public GenericDataDTO searchByNameCategory(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Category" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = itemAssemblyServiceImp.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),requestDTO.getSortBy(), requestDTO.getSortOrder());
        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "search Category" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            throw ex;
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }


//    For Find GetLoggedInUser first name
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


