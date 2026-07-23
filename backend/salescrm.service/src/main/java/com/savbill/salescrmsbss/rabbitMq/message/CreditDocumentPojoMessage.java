package com.savbill.salescrmsbss.rabbitMq.message;

import com.savbill.salescrmsbss.entity.pojo.CreditDocumentPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDocumentPojoMessage {

	private Integer id;

    private String paymode;

    private String paymentdate;

    private String chequedate;

    private String paydetails1;

    private String paydetails2;

    private String paydetails3;

    private String paydetails4;

    private Double amount;

    private String status;

    private Integer approverid;

    private String remarks;

    private String referenceno;
    private String xmldocument;
    private Integer custId;

    private Boolean isDelete = false;

    private String chequeNo;    
    private String bankName;
    private String branch;
    private Boolean tdsflag=false;
    private Double tdsamount;
    private Boolean is_reversed=false;
    private String resevrsed_date;
    private Integer resverse_debitdoc_id;
    private Boolean tds_received=false;
    private String tds_received_date;
    private Integer tds_credit_doc_id;

    private Double adjustedAmount;
    
    private String customerName;
    
    private Long serviceAreaId;
    
    private Integer invoiceId;
    
    private String type;

    private String paytype;

    private Boolean batchAssigned;


    private Integer nextApprover;

    private Integer mvnoId;
    
    public CreditDocumentPojoMessage(CreditDocumentPojo creditDocumentPojo) {
    	this.id = creditDocumentPojo.getId();
    	this.paymode = creditDocumentPojo.getPaymode();
    	if(creditDocumentPojo.getPaymentdate() != null)
    		this.paymentdate = creditDocumentPojo.getPaymentdate().toString();
    	if(creditDocumentPojo.getChequedate() != null)
    		this.chequedate = creditDocumentPojo.getChequedate().toString();
    	this.paydetails1 = creditDocumentPojo.getPaydetails1();
    	this.paydetails2 = creditDocumentPojo.getPaydetails2();
    	this.paydetails3 = creditDocumentPojo.getPaydetails3();
    	this.paydetails4 = creditDocumentPojo.getPaydetails4();
    	this.amount = creditDocumentPojo.getAmount();
    	this.status = creditDocumentPojo.getStatus();
    	this.approverid = creditDocumentPojo.getApproverid();
    	this.remarks = creditDocumentPojo.getRemarks();
    	this.referenceno = creditDocumentPojo.getReferenceno();
    	this.xmldocument = creditDocumentPojo.getXmldocument();
    	this.custId = creditDocumentPojo.getCustId();
    	this.chequeNo = creditDocumentPojo.getChequeNo();
    	this.bankName = creditDocumentPojo.getBankName();
    	this.branch = creditDocumentPojo.getBranch();
    	this.tdsflag = creditDocumentPojo.getTdsflag();
    	this.tdsamount = creditDocumentPojo.getTdsamount();
    	this.is_reversed = creditDocumentPojo.getIs_reversed();
    	if(creditDocumentPojo.getTds_received_date() != null)
    		this.tds_received_date = creditDocumentPojo.getTds_received_date().toString();
    	this.resverse_debitdoc_id = creditDocumentPojo.getResverse_debitdoc_id();
    	this.tds_received = creditDocumentPojo.getTds_received();
    	if(creditDocumentPojo.getTds_received_date() != null)
    		this.tds_received_date = creditDocumentPojo.getTds_received_date().toString();
    	this.tds_credit_doc_id = creditDocumentPojo.getTds_credit_doc_id();
    	this.adjustedAmount = creditDocumentPojo.getAdjustedAmount();
    	this.customerName = creditDocumentPojo.getCustomerName();
    	this.serviceAreaId = creditDocumentPojo.getServiceAreaId();
    	this.invoiceId = creditDocumentPojo.getInvoiceId();
    	this.type = creditDocumentPojo.getType();
    	this.paytype = creditDocumentPojo.getPaytype();
    	this.batchAssigned = creditDocumentPojo.getBatchAssigned();
    }
}
