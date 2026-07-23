package com.savbill.partnermanagement.modules.servicePlan.controller;

import com.savbill.partnermanagement.core.controller.ExBaseAbstractController;
import com.savbill.partnermanagement.modules.Services.ServicesDTO;
import com.savbill.partnermanagement.modules.servicePlan.service.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicesController  extends ExBaseAbstractController<ServicesDTO> {
    private static String MODULE = " [ServicesController] ";
    @Autowired
    private ServicesService service;

    public ServicesController(ServicesService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ServicesController]";
    }

}
