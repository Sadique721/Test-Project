package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlan;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateCustplanMappingSharedData {



    private Integer id;


    private Integer planId;



    private String service;


    private LocalDateTime startDate;


    private LocalDateTime endDate;


    private LocalDateTime expiryDate;


    private String status;


    private Integer custid;


    private PostpaidPlan postpaidPlan;

    private Long debitdocid;


    private Boolean isDelete;


    private Boolean isInvoiceToOrg;


    private String billTo;
    private Integer createdById;
    private Integer lastModifiedById;
}
