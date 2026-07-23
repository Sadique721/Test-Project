package com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItemHierarchy;

import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "NonSerializedItemHierarchyController", description = "REST APIs related to non-serialized item hierarchy Entity!!!!", tags = "non-serialized-item-hierarchy-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.NON_SERIALIZED_ITEM_HIERARCHY_MANAGEMENT)
public class NonSerializedItemHierarchyController extends ExBaseAbstractController<NonSerializedItemHierarchyDto> {

    private static String MODULE = " [NonSerializedItemHierarchyController] ";
    public NonSerializedItemHierarchyController(NonSerializedItemHierarchyServiceImpl service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[NonSerializedItemHierarchyController]";
    }
}
