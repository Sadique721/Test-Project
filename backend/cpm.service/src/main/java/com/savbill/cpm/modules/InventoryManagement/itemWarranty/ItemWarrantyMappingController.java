package com.savbill.cpm.modules.InventoryManagement.itemWarranty;


import com.savbill.cpm.constants.DeleteContant;
import com.savbill.cpm.constants.UrlConstants;
import com.savbill.cpm.core.controller.ExBaseAbstractController;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchDTO;
import com.savbill.cpm.service.common.ClientServiceSrv;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Api(value = "ItemConditionsMappingController", description = "REST APIs related to Item Warranty Entity!!!!", tags = "item-warranty-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.ITEM_WARRANTY_MANAGEMENT)
public class ItemWarrantyMappingController extends ExBaseAbstractController<ItemWarrantyMappingDto> {

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    ItemWarrantyMappingServiceImpl service;


    public ItemWarrantyMappingController(ItemWarrantyMappingServiceImpl service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ItemController]";
    }


    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    @Override
    public GenericDataDTO save(@Valid @RequestBody ItemWarrantyMappingDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        try {
            ItemWarrantyMappingDto productDto = service.saveEntity(entityDTO);
            genericDataDTO.setData(productDto);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req) {
        return super.search(page, pageSize, sortOrder, sortBy, filter , req);
    }

    @Override
    public GenericDataDTO delete(@RequestBody ItemWarrantyMappingDto entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        if (getMvnoIdFromCurrentStaff() != null) {
            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        boolean flag = service.deleteVerification(entityDTO.getId().intValue());
        if (flag) {
            dataDTO = super.delete(entityDTO, authentication, req);

        } else {
            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            dataDTO.setResponseMessage(DeleteContant.PRODUCT_NAME_EXITS);
        }
        return dataDTO;
    }

//    @Override
//    public GenericDataDTO update(@Valid @RequestBody ItemWarrantyMappingDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        dataDTO = super.update(entityDTO, result, authentication, req);
//        return dataDTO;
//    }
}
