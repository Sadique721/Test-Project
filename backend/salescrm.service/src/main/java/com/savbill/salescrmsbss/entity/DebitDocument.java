package com.savbill.salescrmsbss.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.DebitDocumentPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLTDEBITDOCUMENT")
public class DebitDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "debitdocumentid", nullable = false, length = 40)
	private Integer id;

	private String docnumber;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;

	private Integer planId;

	private LocalDateTime billdate;

	private LocalDateTime startdate;

	private LocalDateTime endate;

	private LocalDateTime duedate;

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

	private Boolean isDelete;

	private Long cstchargeid;

	private Integer custid;

	private String customerName;

	private String custType;

	private String paymentStatus;

	private Double adjustedAmount;
	
	public DebitDocument(DebitDocumentPojo debitDocumentPojo) {
		this.id = debitDocumentPojo.getId();
		this.docnumber = debitDocumentPojo.getDocnumber();
		if(debitDocumentPojo.getLeadMasterId() != null) {
			this.leadMaster = new LeadMaster(debitDocumentPojo.getLeadMasterId());
		}
		this.planId = debitDocumentPojo.getPlanId();
		this.billdate = debitDocumentPojo.getBilldate();
		this.startdate = debitDocumentPojo.getStartdate();
		this.endate = debitDocumentPojo.getEndate();
		this.duedate = debitDocumentPojo.getDuedate();
		this.latepaymentdate = debitDocumentPojo.getLatepaymentdate();
		this.subtotal = debitDocumentPojo.getSubtotal();
		this.tax = debitDocumentPojo.getTax();
		this.discount = debitDocumentPojo.getDiscount();
		this.totalamount = debitDocumentPojo.getTotalamount();
		this.previousbalance = debitDocumentPojo.getPreviousbalance();
		this.latepaymentfee = debitDocumentPojo.getLatepaymentfee();
		this.currentpayment = debitDocumentPojo.getCurrentpayment();
		this.currentcredit = debitDocumentPojo.getCurrentcredit();
		this.currentdebit = debitDocumentPojo.getCurrentdebit();
		this.totaldue = debitDocumentPojo.getTotaldue();
		this.amountinwords = debitDocumentPojo.getAmountinwords();
		this.dueinwords = debitDocumentPojo.getDueinwords();
		this.billrunid = debitDocumentPojo.getBillrunid();
		this.billrunstatus = debitDocumentPojo.getBillrunstatus();
		this.document = debitDocumentPojo.getDocument();
		this.isDelete = debitDocumentPojo.getIsDelete();
		this.cstchargeid = debitDocumentPojo.getCstchargeid();
		this.custid = debitDocumentPojo.getCustid();
		this.customerName = debitDocumentPojo.getCustomerName();
		this.custType = debitDocumentPojo.getCustType();
		this.paymentStatus = debitDocumentPojo.getPaymentStatus();
		this.adjustedAmount = debitDocumentPojo.getAdjustedAmount();
	}
}
