package com.savbill.integrationsystem.etims;


import com.savbill.integrationsystem.middleware.Invoice.service.TraInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KRAUtils {
    @Autowired
    TraInvoiceService traInvoiceService;


    public String linkToQr(String link) throws Exception {
        return traInvoiceService.generateQRCodeBase64(link);
    }

}
