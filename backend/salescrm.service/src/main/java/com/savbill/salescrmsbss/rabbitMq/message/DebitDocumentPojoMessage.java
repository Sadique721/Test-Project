package com.savbill.salescrmsbss.rabbitMq.message;

import java.util.List;

import com.savbill.salescrmsbss.entity.pojo.DebitDocumentPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebitDocumentPojoMessage {

	private Integer id;

    private String docnumber;

    private Integer planId;

    private String billdate;

    private String startdate;

    private String endate;

    private String duedate;

    private String latepaymentdate;

    private Double subtotal;

    private Double tax;

    private Double discount;

    private Double totalamount;

    private Double previousbalance;

    private Double latepaymentfee;

    private Double currentpayment;

    private Double currentdebit;

    private Double currentcredit;

    private Double totaldue;

    private String amountinwords;

    private String dueinwords;

    private Integer billrunid;

    private String billrunstatus;

    private String document;

    private Boolean isDelete = false;

    private Long cstchargeid;
    
    private Integer custid;

    private String customerName;

    private String custType;

    private String paymentStatus;

    private Double adjustedAmount;

    private List<CreditDocumentMessage> creditDocumentMessageList;

    private String custRefName;

    private  String refundAbleAmount;
    
    public DebitDocumentPojoMessage(DebitDocumentPojo debitDocumentPojo) {
    	this.id = debitDocumentPojo.getId();
    	this.docnumber = debitDocumentPojo.getDocnumber();
    	this.planId = debitDocumentPojo.getPlanId();
    	if(debitDocumentPojo.getBilldate() != null)
    	    this.billdate = debitDocumentPojo.getBilldate().toString();
    	if(debitDocumentPojo.getStartdate() != null)
    		this.startdate = debitDocumentPojo.getStartdate().toString();
    	if(debitDocumentPojo.getEndate() != null)
    		this.endate = debitDocumentPojo.getEndate().toString();
    	if(debitDocumentPojo.getDuedate() != null)
    		this.duedate = debitDocumentPojo.getDuedate().toString();
    	if(debitDocumentPojo.getLatepaymentdate() != null)
    		this.latepaymentdate = debitDocumentPojo.getLatepaymentdate().toString();
    	this.subtotal = debitDocumentPojo.getSubtotal();
    	this.tax = debitDocumentPojo.getTax();
    	this.discount = debitDocumentPojo.getDiscount();
    	this.totalamount = debitDocumentPojo.getTotalamount();
    	this.previousbalance = debitDocumentPojo.getPreviousbalance();
    	this.latepaymentfee = debitDocumentPojo.getLatepaymentfee();
    	this.currentpayment = debitDocumentPojo.getCurrentpayment();
    	this.currentdebit = debitDocumentPojo.getCurrentdebit();
    	this.currentcredit = debitDocumentPojo.getCurrentcredit();
    	this.totaldue = debitDocumentPojo.getTotaldue();
    	this.amountinwords = debitDocumentPojo.getAmountinwords();
    	this.dueinwords = debitDocumentPojo.getDueinwords();
    	this.billrunid = debitDocumentPojo.getBillrunid();
    	this.billrunstatus = debitDocumentPojo.getBillrunstatus();
    	this.document = debitDocumentPojo.getDocument();
    	this.cstchargeid = debitDocumentPojo.getCstchargeid();
    	this.custid = debitDocumentPojo.getCustid();
    	this.customerName = debitDocumentPojo.getCustomerName();
    	this.custType = debitDocumentPojo.getCustType();
    	this.paymentStatus = debitDocumentPojo.getPaymentStatus();
    	this.adjustedAmount = debitDocumentPojo.getAdjustedAmount();    			
    }
}
