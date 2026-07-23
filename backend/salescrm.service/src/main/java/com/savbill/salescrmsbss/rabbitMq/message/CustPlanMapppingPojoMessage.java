package com.savbill.salescrmsbss.rabbitMq.message;

import java.util.ArrayList;
import java.util.List;

import com.savbill.salescrmsbss.entity.pojo.CustPlanMapppingPojo;
import com.savbill.salescrmsbss.entity.pojo.CustQuotaDtlsPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustPlanMapppingPojoMessage {

	private Integer id;

    private Integer planId;

    private Integer custid;

    private String startDate;

    private String endDate;

    private String expiryDate;

    private String startDateString;
    private String endDateString;
    private String expiryDateString;

    private String status;

    private Long qospolicyId;

    private String uploadqos;

    private String downloadqos;

    private String uploadts;

    private String downloadts;

    
    private List<CustQuotaDtlsPojo> quotaList = new ArrayList<>();

    private String service;

    private Boolean isDelete = false;
    
	private Boolean isTrialPlan = false;

    private Double offerPrice;
    private Double taxAmount;
    private Long creditdocid;
    private Double walletBalUsed = 0.0;
    private String purchaseType;
    private Long onlinePurchaseId;
    private String purchaseFrom;
    private Long debitdocid;
    
    private Double validity;
    
    private String planName;
    
    private Double discount;
    private Integer plangroupid;
    
    private Integer planValidityDays;
    
    private Boolean isInvoiceToOrg=false;
    
    private String billTo="CUSTOMER";

    private Double newAmount=0d;

    private Integer renewalId;

    private Integer custRefId;
    
    public CustPlanMapppingPojoMessage(CustPlanMapppingPojo custPlanMapppingPojo) {
    	this.id = custPlanMapppingPojo.getId();
    	this.planId = custPlanMapppingPojo.getPlanId();
    	this.custid = custPlanMapppingPojo.getCustid();
    	if(custPlanMapppingPojo.getStartDate() != null)
    	    this.startDate = custPlanMapppingPojo.getStartDate().toString();
    	if(custPlanMapppingPojo.getEndDate() != null)
    		this.endDate = custPlanMapppingPojo.getEndDate().toString();
    	if(custPlanMapppingPojo.getExpiryDate() != null)
    		this.expiryDate = custPlanMapppingPojo.getExpiryDate().toString();
    	this.startDateString = custPlanMapppingPojo.getStartDateString();
    	this.endDateString = custPlanMapppingPojo.getEndDateString();
    	this.expiryDateString = custPlanMapppingPojo.getExpiryDateString();
    	this.status = custPlanMapppingPojo.getStatus();
    	this.qospolicyId = custPlanMapppingPojo.getQospolicyId();
    	this.uploadqos = custPlanMapppingPojo.getUploadqos();
    	this.downloadqos = custPlanMapppingPojo.getDownloadqos();
    	this.uploadts = custPlanMapppingPojo.getUploadts();
    	this.downloadts = custPlanMapppingPojo.getDownloadts();
    	this.quotaList = custPlanMapppingPojo.getQuotaList();
    	this.service = custPlanMapppingPojo.getService();
    	this.offerPrice = custPlanMapppingPojo.getOfferPrice();
    	this.taxAmount = custPlanMapppingPojo.getTaxAmount();
    	this.creditdocid = custPlanMapppingPojo.getCreditdocid();
    	this.walletBalUsed = custPlanMapppingPojo.getWalletBalUsed();
    	this.purchaseType = custPlanMapppingPojo.getPurchaseType();
    	this.onlinePurchaseId = custPlanMapppingPojo.getOnlinePurchaseId();
    	this.purchaseFrom = custPlanMapppingPojo.getPurchaseFrom();
    	this.debitdocid = custPlanMapppingPojo.getDebitdocid();
    	this.validity = custPlanMapppingPojo.getValidity();
    	this.planName = custPlanMapppingPojo.getPlanName();
    	this.discount = custPlanMapppingPojo.getDiscount();
    	this.plangroupid = custPlanMapppingPojo.getPlangroupid();
    	this.planValidityDays = custPlanMapppingPojo.getPlanValidityDays();
    	this.isInvoiceToOrg = custPlanMapppingPojo.getIsInvoiceToOrg();
    	this.billTo = custPlanMapppingPojo.getBillTo();
    	this.newAmount = custPlanMapppingPojo.getNewAmount();
    	this.renewalId = custPlanMapppingPojo.getRenewalId();
    	this.custRefId = custPlanMapppingPojo.getCustRefId();   
    	this.isTrialPlan = custPlanMapppingPojo.getIstrialplan();
    }

}
