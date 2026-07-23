package com.savbill.integrationsystem.billgen.controller;

import com.savbill.integrationsystem.billgen.model.BillGenDTO;
import com.savbill.integrationsystem.billgen.service.BillGenService;
import com.savbill.integrationsystem.core.controller.ExBaseAbstractController;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("api/v1/billgen")
@Api(value = "BillGenController", description = "REST APIs related to Bill Gen !!!!", tags = "BillGenController")
public class BillGenController extends ExBaseAbstractController<BillGenDTO> {

    private String MODULE = "[ProfileController]";



    public BillGenController(BillGenService billGenService) {
        super(billGenService);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }
    
}
