package com.savbill.revenuemanagement.core.dto.invoice;

import com.savbill.revenuemanagement.core.entity.customers.Customers;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RecordPayment {

    private String referenceno;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate chequedate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentdate;

    private String chequeno;
    private String bank;
    @ToString.Exclude
    private Customers customer;
    private String customerid;
    private String paymode;
    private Double amount;
    private String paymentreferenceno;
    private String remark;
    private String branch;

    private Integer mvnoId;
    
    private Integer invoiceId;
    
    private String type;

    private String paytype;
    private Long buId;
    private String reciptNo;

    private Integer nextApprover;

    private String filename;
    private String uniquename;
    private Double barteramount;
    private Double tdsAmount;
    private Double abbsAmount;
    private Integer creditDocId;
    private String onlinesource;
    private Integer withDrawCreditdocId;

}
