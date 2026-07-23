package com.savbill.integrationsystem.deviceveri.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.integrationsystem.core.data.IBaseData;

import lombok.Data;

@Data
@Entity
@Table(name = "tblcustpackagerel")
public class CustomerPackageRelData implements IBaseData<Long>{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custpackageid", length = 40)
    private Long custpackageid;
	
	@Column(name = "custid")
    private Long custid;
	
	@Column(name = "planid")
    private Long planid;
	
	@Column(name = "startdate")
    private LocalDateTime startdate;
	
	@Column(name = "enddate")
    private LocalDateTime enddate;
	
	@Column(name = "expirydate")
    private LocalDateTime expirydate;
	
	@Column(name = "status")
    private String status;
	
	/*
	@Column(name = "CREATEDATE")
    private LocalDateTime createDate;
	
	@Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime lastModifiedDate;
	
	@Column(name = "createbyname")
    private String createbyname;
	
	@Column(name = "updatebyname")
    private String updatebyname;
	
	@Column(name = "CREATEDBYSTAFFID")
    private Long createdStaffId;
	
	@Column(name = "LASTMODIFIEDBYSTAFFID")
    private Long LastModifiedStaffId;
	*/
	
	@Column(name = "service")
    private String service;
	
	@Column(name = "custservicemappingid")
    private Long custservicemappingid;

	@Column(name = "qospolicyid")
    private Long qospolicyid;
	
	@Column(name = "uploadqos")
    private String uploadqos;
	
	@Column(name = "downloadqos")
    private String downloadqos;

	@Column(name = "uploadts")
    private String uploadts;
	
	@Column(name = "downloadts")
    private String downloadts;
	
	@Column(name = "createdbystaffid")
    private Long createdbystaffid;
	
	@Column(name = "lastmodifieddate")
    private LocalDateTime lastmodifieddate;

	@Column(name = "is_delete")
    private Boolean isDelete;

	@Column(name = "offer_price")
    private Double offerPrice;

	@Column(name = "tax_amount")
    private Double taxAmount;

	@Column(name = "createbyname")
    private String createbyname;
	
	@Column(name = "updatebyname")
    private String updatebyname;

	@Column(name = "creditdocid")
    private Long creditdocid;

	@Column(name = "debitdocid")
    private Long debitdocid;

	@Column(name = "wallet_bal_used")
    private Double walletBalUsed;

	@Column(name = "purchase_type")
    private String purchaseType;

	@Column(name = "online_purchase_id")
    private Long onlinePurchaseId;
	
	@Column(name = "purchase_from")
    private String purchaseFrom;

	@Column(name = "discount")
    private Double discount;

	@Column(name = "dbr")
    private Double dbr;

	@Column(name = "plangroupid")
    private Long plangroupid;

	@Column(name = "bill_to")
    private String billTo;

	@Column(name = "is_invoice_to_org")
    private Integer isInvoiceToOrg;

	@Column(name = "new_amount")
    private Double newAmount;

	@Column(name = "renewal_id")
    private String renewalId;

	@Column(name = "cust_ref_id")
    private Long custRefId;
	
	@Column(name = "next_approver")
    private Long nextApprover;

	@Column(name = "next_staff")
    private Long nextStaff;
	
	@Column(name = "staff_approver_status")
    private String staffApproverStatus;

	@Column(name = "cust_ref_name")
    private String custRefName;

	@Column(name = "cust_plan_status")
    private String custPlanStatus;

	@Column(name = "isinvoicestop")
    private Integer isinvoicestop;

	@Column(name = "istrialplan")
    private Integer istrialplan;

	@Column(name = "traildebitdocid")
    private Long traildebitdocid;

	@Column(name = "is_trial_validity")
    private Integer isTrialValidity;

	@Column(name = "trial_plan_validity_count")
    private Long trialPlanValidityCount;

	@Column(name = "billable_cust_id")
    private Long billableCustId;

	@Column(name = "invoice_type")
	private String invoiceType;

	@Override
	public Long getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeleteFlag(boolean deleteFlag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDeleteFlag() {
		// TODO Auto-generated method stub
		return false;
	}
}
