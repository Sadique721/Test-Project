package com.savbill.salescrmsbss.rabbitMq.message;

import java.time.format.DateTimeFormatter;

import com.savbill.salescrmsbss.entity.pojo.CustPlanMapppingPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadCustPlanMapppingPojoMessage {

	private Integer id;

	private Integer planId;

	private Integer custid;

	private Long leadMasterId;

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

	private String service;

	private Boolean isDelete = false;
	
	private Boolean isTrialPlan = false;

	private Double offerPrice;

	private Double taxAmount;

	private Long creditdocid;

	private Double walletBalUsed = 0.0;;

	private String purchaseType;

	private Long onlinePurchaseId;

	private String purchaseFrom;

	private Long debitdocid;

	private Double validity;

	private String planName;

	private Double discount;

	private Integer plangroupid;

	private Integer planValidityDays;

	private Boolean isInvoiceToOrg = false;

	private String billTo = "CUSTOMER";;

	private Double newAmount = 0d;;

	private Integer renewalId;

	private Integer custRefId;

	public LeadCustPlanMapppingPojoMessage(CustPlanMapppingPojo custPlanMapping) {
		this.id = custPlanMapping.getId();
		this.planId = custPlanMapping.getPlanId();
		this.custid = custPlanMapping.getCustid();
		this.leadMasterId = custPlanMapping.getLeadMasterId();
		if (custPlanMapping.getStartDate() != null) {
			this.startDate = custPlanMapping.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		if (custPlanMapping.getEndDate() != null) {
			this.endDate = custPlanMapping.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		if (custPlanMapping.getExpiryDate() != null) {
			this.expiryDate = custPlanMapping.getExpiryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		this.startDateString = custPlanMapping.getStartDateString();
		this.endDateString = custPlanMapping.getEndDateString();
		this.expiryDateString = custPlanMapping.getExpiryDateString();
		this.status = custPlanMapping.getStatus();
		this.qospolicyId = custPlanMapping.getQospolicyId();
		this.uploadqos = custPlanMapping.getUploadqos();
		this.downloadqos = custPlanMapping.getDownloadqos();
		this.uploadts = custPlanMapping.getUploadts();
		this.downloadts = custPlanMapping.getDownloadts();
		this.service = custPlanMapping.getService();
		this.isDelete = custPlanMapping.getIsDelete();
		this.offerPrice = custPlanMapping.getOfferPrice();
		this.taxAmount = custPlanMapping.getTaxAmount();
		this.creditdocid = custPlanMapping.getCreditdocid();
		this.walletBalUsed = custPlanMapping.getWalletBalUsed();
		this.purchaseType = custPlanMapping.getPurchaseType();
		this.onlinePurchaseId = custPlanMapping.getOnlinePurchaseId();
		this.purchaseFrom = custPlanMapping.getPurchaseFrom();
		this.debitdocid = custPlanMapping.getDebitdocid();
		this.validity = custPlanMapping.getValidity();
		this.planName = custPlanMapping.getPlanName();
		this.discount = custPlanMapping.getDiscount();
		this.plangroupid = custPlanMapping.getPlangroupid();
		this.planValidityDays = custPlanMapping.getPlanValidityDays();
		this.isInvoiceToOrg = custPlanMapping.getIsInvoiceToOrg();
		this.billTo = custPlanMapping.getBillTo();
		this.newAmount = custPlanMapping.getNewAmount();
		this.renewalId = custPlanMapping.getRenewalId();
		this.custRefId = custPlanMapping.getCustRefId();
		this.isTrialPlan = custPlanMapping.getIstrialplan();
	}
}
