package com.savbill.integrationsystem.deviceveri.model;

import java.time.LocalDateTime;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=false)
public class CustomerPackageRelDTO extends Auditable<Long> implements IBaseDto{
    private Long custpackageid;
    private Long custid;
    private Long planid;
    private LocalDateTime startdate;
    private LocalDateTime enddate;
    private LocalDateTime expirydate;
    private String status;
	/*
    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;
    private String createbyname;
    private String updatebyname;
    private Long createdStaffId;
    private Long LastModifiedStaffId;
	*/
    private String service;
    private Long qospolicyid;
    private String uploadqos;
    private String downloadqos;
    private String uploadts;
    private String downloadts;
    private Long createdbystaffid;
    private LocalDateTime lastmodifieddate;
    private Boolean isDelete;
    private Double offerPrice;
    private Double taxAmount;
    private String createbyname;
    private String updatebyname;
    private Long creditdocid;
    private Long debitdocid;
    private Double walletBalUsed;
    private String purchaseType;
    private Long onlinePurchaseId;
    private String purchaseFrom;
    private Double discount;
    private Double dbr;
    private Long plangroupid;
    private String billTo;
    private Integer isInvoiceToOrg;
    private Double newAmount;
    private String renewalId;
    private Long custRefId;
    private Long nextApprover;
    private Long nextStaff;
    private String staffApproverStatus;
    private String custRefName;
    private String custPlanStatus;
    private Integer isinvoicestop;
    private Integer istrialplan;
    private Long traildebitdocid;
    private Integer isTrialValidity;
    private Long trialPlanValidityCount;
    private Long billableCustId;
    private Long custservicemappingid;
    
	@Override
	public Long getIdentityKey() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Long getMvnoId() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setMvnoId(Long mvnoId) {
		// TODO Auto-generated method stub
		
	}
}
