package com.savbill.salescrmsbss.entity.pojo;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.savbill.salescrmsbss.entity.DebitDocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebitDocumentPojo {

	private Integer id;

	private String docnumber;

	private Long leadMasterId;

	private Integer planId;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime billdate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime startdate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime endate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime duedate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime latepaymentdate;

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
	
	public DebitDocumentPojo(DebitDocument debitDocument) {
		this.id = debitDocument.getId();
		this.docnumber = debitDocument.getDocnumber();
		if(debitDocument.getLeadMaster() != null) {
			this.leadMasterId = debitDocument.getLeadMaster().getId();
		}
		this.planId = debitDocument.getPlanId();
		this.billdate = debitDocument.getBilldate();
		this.startdate = debitDocument.getStartdate();
		this.endate = debitDocument.getEndate();
		this.duedate = debitDocument.getDuedate();
		this.latepaymentdate = debitDocument.getLatepaymentdate();
		this.subtotal = debitDocument.getSubtotal();
		this.tax = debitDocument.getTax();
		this.discount = debitDocument.getDiscount();
		this.totalamount = debitDocument.getTotalamount();
		this.previousbalance = debitDocument.getPreviousbalance();
		this.latepaymentfee = debitDocument.getLatepaymentfee();
		this.currentpayment = debitDocument.getCurrentpayment();
		this.currentcredit = debitDocument.getCurrentcredit();
		this.currentdebit = debitDocument.getCurrentdebit();
		this.totaldue = debitDocument.getTotaldue();
		this.amountinwords = debitDocument.getAmountinwords();
		this.dueinwords = debitDocument.getDueinwords();
		this.billrunid = debitDocument.getBillrunid();
		this.billrunstatus = debitDocument.getBillrunstatus();
		this.document = debitDocument.getDocument();
		this.isDelete = debitDocument.getIsDelete();
		this.cstchargeid = debitDocument.getCstchargeid();
		this.custid = debitDocument.getCustid();
		this.customerName = debitDocument.getCustomerName();
		this.custType = debitDocument.getCustType();
		this.paymentStatus = debitDocument.getPaymentStatus();
		this.adjustedAmount = debitDocument.getAdjustedAmount();
	}
}
