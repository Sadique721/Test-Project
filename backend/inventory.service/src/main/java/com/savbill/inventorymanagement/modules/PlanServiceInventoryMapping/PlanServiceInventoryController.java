package com.savbill.inventorymanagement.modules.PlanServiceInventoryMapping;

import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL +UrlConstants.PLAN_SERVICE_INVENTORY)
public class PlanServiceInventoryController extends ExBaseAbstractController<PlanServiceInventoryMappingPojo> {

    public PlanServiceInventoryController(PlanServiceInventoryService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PlanServiceInventoryController]";
    }

    @Autowired
    PlanServiceInventoryService planServiceInventoryService;

    @GetMapping("/getPlanServiceInventoryByServiceId")
    public GenericDataDTO getPlanServiceInventoryByServiceId(@Valid @RequestParam Integer serviceId) {
        return planServiceInventoryService.getPlanServiceInventoryByServiceId(serviceId);
    }
}
