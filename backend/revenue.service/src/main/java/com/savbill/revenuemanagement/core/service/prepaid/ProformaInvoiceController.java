package com.savbill.revenuemanagement.core.service.prepaid;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.customer.PlanAndChargeRequest;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.ProformaDebitDocument;
import com.savbill.revenuemanagement.core.repository.debit.ProfomaDebitDocDetailsRepository;
import com.savbill.revenuemanagement.core.repository.debit.ProfomaDebitDocRepository;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class ProformaInvoiceController {
    private final Logger logger = LoggerFactory.getLogger(ProformaInvoiceController.class);

    @Autowired
    Tracer tracer;
    @Autowired
    ProfomaDebitDocDetailsRepository profomaDebitDocDetailsRepository;
    @Autowired
    ProformaInvoiceService proformaInvoiceService;
    @Autowired
    ProfomaDebitDocRepository profomaDebitDocRepository;

    @PostMapping(value = "/ProformaInvoice")
    public  ResponseEntity<GenericDataDTO> processMessageApi(@RequestBody PlanAndChargeRequest request) {
        DebitDocument debitDocument = null;
        GenericDataDTO response = new GenericDataDTO();
        ProformaDebitDocument proformaDebitDocument=null;
        CustomerBillingMessage message = new CustomerBillingMessage();
        Map<String, Object> data = new HashMap<>();
        data.put("currentUserLoggedInId",proformaInvoiceService.getLoggedInUserId());
        data.put("postpaidAdvance", "Advance");
        data.put("mvnoId", request.getMvnoId());
        data.put("custId", request.getCustId());
        data.put("billRunId", 74);
        data.put("Bullable_CUST_ID", null); // explicitly setting null

        message.setData(data);
        TraceContext traceContext =tracer.currentSpan().context();
        message.setTraceContext(traceContext);
        try {
                proformaDebitDocument = proformaInvoiceService.createPrepaidInvoiceCaf(message, request);

        if(proformaDebitDocument!=null)
        {
            Optional<ProformaDebitDocument> proformaDebitDocument1=profomaDebitDocRepository.findById(proformaDebitDocument.getId());
            if(proformaDebitDocument1.isPresent())
            {
                String xml = proformaInvoiceService.setInvoiceXml(proformaDebitDocument1.get(),proformaDebitDocument1.get().getProfomaDebitDocumentDetails());
                proformaDebitDocument1.get().setDuedate(proformaDebitDocument1.get().getEndate());
                proformaDebitDocument1.get().setDocument(xml);
                proformaDebitDocument1.get().setProfomaDebitDocumentDetails(null);
                profomaDebitDocRepository.save(proformaDebitDocument1.get());
            }
            response.setResponseCode(HttpStatus.CREATED.value());
            response.setResponseMessage("Proforma invoice created successfully");
            response.setData(proformaDebitDocument);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }else
        {
            response.setResponseCode(HttpStatus.NOT_FOUND.value());
            response.setResponseMessage("Proforma Invoice creation Failed");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error in processMessage: "+ex.getMessage());
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Error processing message: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
