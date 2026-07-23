package com.savbill.salescrmsbss.entity.pojo;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.savbill.salescrmsbss.entity.CreditDocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDocumentPojo {

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

	private Boolean isDelete = false;

	private String chequeNo;
	
	private String bankName;
	
	private String branch;
	
	private Boolean tdsflag = false;
	
	private Double tdsamount;
	
	private Boolean is_reversed = false;
	
	private LocalDate resevrsed_date;
	
	private Integer resverse_debitdoc_id;
	
	private Boolean tds_received = false;
	
	private LocalDate tds_received_date;
	
	private Integer tds_credit_doc_id;

	private Double adjustedAmount;

	private String customerName;

	private Long serviceAreaId;

	private Integer invoiceId;

	private String type;

	private String paytype;

	private Boolean batchAssigned;
	
	private Long leadMasterId;
	
	public CreditDocumentPojo(CreditDocument creditDocument) {
		this.id = creditDocument.getId();
		this.paymode = creditDocument.getPaymode();
		this.paymentdate = creditDocument.getPaymentdate();
		this.chequedate = creditDocument.getChequedate();
		this.paydetails1 = creditDocument.getPaydetails1();
		this.paydetails2 = creditDocument.getPaydetails2();
		this.paydetails3 = creditDocument.getPaydetails3();
		this.paydetails4 = creditDocument.getPaydetails4();
		this.amount = creditDocument.getAmount();
		this.status = creditDocument.getStatus();
		this.approverid = creditDocument.getApproverid();
		this.remarks = creditDocument.getRemarks();
		this.referenceno = creditDocument.getReferenceno();
		this.xmldocument = creditDocument.getXmldocument();
		this.custId = creditDocument.getCustId();
		this.isDelete = creditDocument.getIsDelete();
		this.chequeNo = creditDocument.getChequeNo();
		this.bankName = creditDocument.getBankName();
		this.branch = creditDocument.getBranch();
		this.tdsflag = creditDocument.getTdsflag();
		this.tdsamount = creditDocument.getTdsamount();
		this.is_reversed = creditDocument.getIs_reversed();
		this.resevrsed_date = creditDocument.getResevrsed_date();
		this.resverse_debitdoc_id = creditDocument.getResverse_debitdoc_id();
		this.tds_received = creditDocument.getTds_received();
		this.tds_received_date = creditDocument.getTds_received_date();
		this.tds_credit_doc_id = creditDocument.getTds_credit_doc_id();
		this.adjustedAmount = creditDocument.getAdjustedAmount();
		this.customerName = creditDocument.getCustomerName();
		this.serviceAreaId = creditDocument.getServiceAreaId();
		this.invoiceId = creditDocument.getInvoiceId();
		this.type = creditDocument.getType();
		this.paytype = creditDocument.getPaytype();
		this.batchAssigned = creditDocument.getBatchAssigned();
		if(creditDocument.getLeadMaster() != null) {
			this.leadMasterId = creditDocument.getLeadMaster().getId();
		}
	}
}
