package com.savbill.integrationsystem.middleware.Invoice.controller;

import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.middleware.Invoice.service.TraInvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstants.INVOICE_INTIGRATION)
public class InvoiceIntigrationController {

    @Autowired
    private TraInvoiceService traInvoiceService;

    private static final Logger logger = LoggerFactory.getLogger(InvoiceIntigrationController.class);

//    @PostMapping(value = "/sendTraInvoice")
//    public GenericDataDTO SendInvoiceToTRA(@Valid @RequestBody TraInvoiceDTO entityDTO, BindingResult result,
//                                           HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        entityDTO = traInvoiceService.createTrainvoiceDTO();
//        dataDTO = traInvoiceService.sendTraInvoiceRequest(entityDTO , "http://154.72.68.222:9010/api/sign?invoice+1" , "Basic ZxZoaZMUQbUJDljA7kTExQ==");
//        logger.info("Government Master created Successfully With name " + entityDTO.getCustomerName()
//                + "  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"));
//        return dataDTO;
//    }


}
