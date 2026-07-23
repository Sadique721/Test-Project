package com.savbill.inventorymanagement.modules.InventoryManagement.ReturnProduct;

import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController@Api(value = "ReturnController", description = "REST APIs related to return Entity!!!!", tags = "ruturn_inventory")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.RETURN)
public class ReturnController extends ExBaseAbstractController<ReturnDto> {

    @Autowired
    private ReturnService returnService;

    public ReturnController(ReturnService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[RetrunController]";
    }

    @PostMapping("/savereturn" )
    public ReturnDto saveReturn(@RequestBody ReturnDto returnDto) throws Exception {
        return returnService.saveReturn(returnDto);
    }

    @GetMapping("/getReturnforCustomer")
    public List<Return> getforCustomer(@RequestParam("id") Long id) throws Exception {
        List<Return> list = new ArrayList<>();
        list = returnService.getreturnforcustomer(id);
        return list;
    }
}
