package com.savbill.salescrmsbss.entity.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.savbill.salescrmsbss.entity.CustPlanMappping;
import com.savbill.salescrmsbss.entity.CustQuotaDtls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustPlanMapppingPojo {

	private Integer id;

	private Integer planId;

	private Integer custid;

	private Long leadMasterId;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime startDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime endDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime expiryDate;

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
	
	private Boolean istrialplan = false;

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

	private String billTo = "CUSTOMER";

	private Double newAmount = 0d;

	private Integer renewalId;

	private Integer custRefId;

	private String discountType="One-time";

	private LocalDate discountExpiryDate;

	private LinkAcceptanceDTO linkAcceptanceDTO;

	private  PostpaidPlanPojo postpaidPlanPojo;

	public CustPlanMapppingPojo(CustPlanMappping custPlanMapping) {
		this.id = custPlanMapping.getId();
		this.planId = custPlanMapping.getPlanId();
		this.custid = custPlanMapping.getCustid();
		if (custPlanMapping.getLeadMaster() != null) {
			this.leadMasterId = custPlanMapping.getLeadMaster().getId();
		}
		this.startDate = custPlanMapping.getStartDate();
		this.endDate = custPlanMapping.getEndDate();
		this.expiryDate = custPlanMapping.getExpiryDate();
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
		this.discountType=custPlanMapping.getDiscountType();
		this.discountExpiryDate=custPlanMapping.getDiscountExpiryDate();
		this.linkAcceptanceDTO = custPlanMapping.getLinkAcceptanceDTO();
		this.postpaidPlanPojo = custPlanMapping.getPostpaidPlanPojo();

		if (custPlanMapping.getCustQuotaDtlsList() != null && custPlanMapping.getCustQuotaDtlsList().size() > 0) {
			List<CustQuotaDtlsPojo> custQuotaDtlsPojoList = new ArrayList<CustQuotaDtlsPojo>();
			for (CustQuotaDtls custQuotaDtls : custPlanMapping.getCustQuotaDtlsList()) {
				custQuotaDtlsPojoList.add(new CustQuotaDtlsPojo(custQuotaDtls));
			}
			this.quotaList = custQuotaDtlsPojoList;
		}
		this.istrialplan = custPlanMapping.getIsTrialPlan();

	}
}
