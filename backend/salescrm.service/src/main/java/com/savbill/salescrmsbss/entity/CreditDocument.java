package com.savbill.salescrmsbss.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.CreditDocumentPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTCREDITDOC")
public class CreditDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CREDITDOCID", nullable = false, length = 40)
	private Integer id;

	private String paymode;

	private LocalDate paymentdate;

	private LocalDate chequedate;

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

	private Boolean isDelete;

	private String chequeNo;
	
	private String bankName;
	
	private String branch;
	
	private Boolean tdsflag;
	
	private Double tdsamount;
	
	private Boolean is_reversed;
	
	private LocalDate resevrsed_date;
	
	private Integer resverse_debitdoc_id;
	
	private Boolean tds_received;
	
	private LocalDate tds_received_date;
	
	private Integer tds_credit_doc_id;

	private Double adjustedAmount;

	private String customerName;

	private Long serviceAreaId;

	private Integer invoiceId;

	private String type;

	private String paytype;

	private Boolean batchAssigned;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;
	
	public CreditDocument(CreditDocumentPojo creditDocumentPojo) {
		this.id = creditDocumentPojo.getId();
		this.paymode = creditDocumentPojo.getPaymode();
		this.paymentdate = creditDocumentPojo.getPaymentdate();
		this.chequedate = creditDocumentPojo.getChequedate();
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
		this.isDelete = creditDocumentPojo.getIsDelete();
		this.chequeNo = creditDocumentPojo.getChequeNo();
		this.bankName = creditDocumentPojo.getBankName();
		this.branch = creditDocumentPojo.getBranch();
		this.tdsflag = creditDocumentPojo.getTdsflag();
		this.tdsamount = creditDocumentPojo.getTdsamount();
		this.is_reversed = creditDocumentPojo.getIs_reversed();
		this.resevrsed_date = creditDocumentPojo.getResevrsed_date();
		this.resverse_debitdoc_id = creditDocumentPojo.getResverse_debitdoc_id();
		this.tds_received = creditDocumentPojo.getTds_received();
		this.tds_received_date = creditDocumentPojo.getTds_received_date();
		this.tds_credit_doc_id = creditDocumentPojo.getTds_credit_doc_id();
		this.adjustedAmount = creditDocumentPojo.getAdjustedAmount();
		this.customerName = creditDocumentPojo.getCustomerName();
		this.serviceAreaId = creditDocumentPojo.getServiceAreaId();
		this.invoiceId = creditDocumentPojo.getInvoiceId();
		this.type = creditDocumentPojo.getType();
		this.paytype = creditDocumentPojo.getPaytype();
		this.batchAssigned = creditDocumentPojo.getBatchAssigned();
		if(creditDocumentPojo.getLeadMasterId() != null) {
			this.leadMaster = new LeadMaster(creditDocumentPojo.getLeadMasterId());
		}
	}
}
