package com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.DeleteContant;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchDTO;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.utils.APIConstants;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Api(value = "ItemConditionsMappingController", description = "REST APIs related to Item Warranty Entity!!!!", tags = "item-warranty-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.ITEM_WARRANTY_MANAGEMENT)
public class ItemWarrantyMappingController extends ExBaseAbstractController<ItemWarrantyMappingDto> {

    @Autowired
    ItemWarrantyMappingServiceImpl service;


    public ItemWarrantyMappingController(ItemWarrantyMappingServiceImpl service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ItemController]";
    }

    private final Logger LOGGER = Logger.getLogger(ItemWarrantyMappingController.class);
    @Autowired
    private Tracer tracer;
    @Autowired
    private ItemWarrantyMappingServiceImpl itemWarrantyMappingService;

    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    @Override
    public GenericDataDTO save(@Valid @RequestBody ItemWarrantyMappingDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO(); TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", itemWarrantyMappingService.getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        try {
            ItemWarrantyMappingDto productDto = service.saveEntity(entityDTO);
            genericDataDTO.setData(productDto);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Create ItemWarrantyMapping By Keyword By Id : "+entityDTO.getId()+ LogConstant.REQUEST_BY + itemWarrantyMappingService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search ItemWarrantyMapping By Keyword By Id : "+entityDTO.getId() +LogConstant.REQUEST_BY + itemWarrantyMappingService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED+ LogConstant.LOG_NO_RECORD_FOUND + APIConstants.ERROR_MESSAGE +ex.getMessage()+ LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy,
                                 @RequestBody GenericSearchDTO filter , HttpServletRequest req) {
          TraceContext traceContext =tracer.currentSpan().context();
              MDC.put("type", "Search");
              MDC.put("userName", itemWarrantyMappingService.getLoggedInUser().getUsername());
              MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
              MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter ,req);
            if(genericDataDTO.getDataList().isEmpty()) {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search ItemWarrantyMapping By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + itemWarrantyMappingService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED+ LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }else
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Search ItemWarrantyMapping By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + itemWarrantyMappingService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search ItemWarrantyMapping By Keyword : "+ filter.getFilter().get(0).getFilterValue() +LogConstant.REQUEST_BY + itemWarrantyMappingService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED+ APIConstants.EXPECTATION_FAILED + APIConstants.ERROR_MESSAGE +ex.getMessage()+ LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
           MDC.remove("type");
           MDC.remove("userName");
           MDC.remove("traceId");
           MDC.remove("spanId");

        } return genericDataDTO;
    }

    @Override
    public GenericDataDTO delete(@RequestBody ItemWarrantyMappingDto entityDTO, HttpServletRequest req) throws Exception {

        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", itemWarrantyMappingService.getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());  GenericDataDTO dataDTO = new GenericDataDTO();
        try{
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        boolean flag = service.deleteVerification(entityDTO.getId().intValue());
        if (flag) {
            dataDTO = super.delete(entityDTO, req);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Delete ItemWarrantyMapping"+LogConstant.LOG_BY_NAME + entityDTO.getWarranty() + LogConstant.REQUEST_BY + itemWarrantyMappingService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(DeleteContant.PRODUCT_NAME_EXITS);
        }}catch (Exception ex){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Delete ItemWarrantyMapping By Keyword"+LogConstant.LOG_BY_NAME + entityDTO.getWarranty() +LogConstant.REQUEST_BY + itemWarrantyMappingService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED+ LogConstant.LOG_NO_RECORD_FOUND + APIConstants.ERROR_MESSAGE +ex.getMessage()+ LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @Override
    public GenericDataDTO update(@Valid @RequestBody ItemWarrantyMappingDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        MDC.put("type", "Update");
        dataDTO = super.update(entityDTO, result, req);
        return dataDTO;
    }
}
