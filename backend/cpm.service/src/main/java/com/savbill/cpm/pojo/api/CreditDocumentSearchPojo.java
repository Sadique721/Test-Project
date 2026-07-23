package com.savbill.cpm.pojo.api;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreditDocumentSearchPojo {

    private Integer id;
    private String customerName;
    private Double amount;
    private String documentno;
    private String invoiceNumber;
    private String paydetails2;
    private Double tdsamount;
    private Double abbsAmount;
    private String referenceno;
    private String paymode;
    private String type;
    private LocalDate paymentdate;
    private String status;
    private Integer approverid;
    private Integer nextTeamHierarchyMappingId;
    private  String filename;

    private String createdByName;

    private String remarks;

    private Integer custId;

    private boolean batchAssigned;

    private String mvnoName;
    private  String currency;
    private String paytype;
    private String paymentreferenceno;
    private Double adjustedAmount;
    private LocalDateTime createdate;
    private String acctno;


    public CreditDocumentSearchPojo(Integer id, String customerName, Double amount, String documentno, String invoiceNumber, String paydetails2, Double tdsamount, Double abbsAmount, String referenceno, String paymode, String type, LocalDate paymentdate, String status, Integer approverid, Integer nextTeamHierarchyMappingId,String createdByName,String remarks,String filename,Integer custId,boolean batchAssigned, String mvnoName, String currency, String paytype, String paymentreferenceno, Double adjustedAmount, LocalDateTime createdate,String acctno) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.documentno = documentno;
        this.invoiceNumber = invoiceNumber;
        this.paydetails2 = paydetails2;
        this.tdsamount = tdsamount;
        this.abbsAmount = abbsAmount;
        this.referenceno = referenceno;
        this.paymode = paymode;
        this.type = type;
        this.paymentdate = paymentdate;
        this.status = status;
        this.approverid = approverid;
        this.nextTeamHierarchyMappingId = nextTeamHierarchyMappingId;
        this.createdByName = createdByName;
        this.remarks = remarks;
        this.filename=filename;
        this.custId = custId;
        this.batchAssigned = batchAssigned;
        this.mvnoName = mvnoName;
        this.currency = currency;
        this.paytype=paytype;
        this.paymentreferenceno=paymentreferenceno;
        this.adjustedAmount=adjustedAmount;
        this.createdate=createdate;
        this.acctno=acctno;
    }

}
