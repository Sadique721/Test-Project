package com.savbill.revenuemanagement.productmanagement.servicePlan.controller;


import com.savbill.revenuemanagement.core.controller.common.ExBaseAbstractController;
import com.savbill.revenuemanagement.productmanagement.servicePlan.model.ServicesDTO;
import com.savbill.revenuemanagement.productmanagement.servicePlan.service.ServicesService;
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
