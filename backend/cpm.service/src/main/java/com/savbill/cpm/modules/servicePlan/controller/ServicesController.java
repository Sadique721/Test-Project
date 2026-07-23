package com.savbill.cpm.modules.servicePlan.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.cpm.core.controller.ExBaseAbstractController;
import com.savbill.cpm.modules.servicePlan.model.ServicesDTO;
import com.savbill.cpm.modules.servicePlan.service.ServicesService;

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
