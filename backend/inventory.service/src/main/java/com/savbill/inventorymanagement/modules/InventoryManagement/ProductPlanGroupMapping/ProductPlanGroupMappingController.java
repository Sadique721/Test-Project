package com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping;

import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.PRODUCT_PLAN_GROUP_MAPPING)
public class ProductPlanGroupMappingController extends ExBaseAbstractController<ProductPlanGroupMappingDto> {

    public ProductPlanGroupMappingController(ProductPlanGroupMappingService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ProductPlanGroupMappingController]";
    }

    @Autowired
    ProductPlanGroupMappingService productPlanGroupMappingService;

    @GetMapping("/getProductPlanGroupMappingDetails")
    public GenericDataDTO getProductPlanGroupMappingDetails(@RequestParam(name = "plangroupid") Long planGroupId) {
        return productPlanGroupMappingService.getProductPlanGroupMappingDetails(planGroupId);
    }
}
