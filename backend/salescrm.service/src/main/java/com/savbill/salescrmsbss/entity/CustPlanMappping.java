package com.savbill.salescrmsbss.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.savbill.salescrmsbss.entity.pojo.CustPlanMapppingPojo;
import com.savbill.salescrmsbss.entity.pojo.CustQuotaDtlsPojo;
import com.savbill.salescrmsbss.entity.pojo.LinkAcceptanceDTO;
import com.savbill.salescrmsbss.entity.pojo.PostpaidPlanPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLCUSTPACKAGEREL")
public class CustPlanMappping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cust_plan_mappping_id", nullable = false)
	private Integer id;

	private Integer planId;

	private Integer custid;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;

	private LocalDateTime startDate;

	private LocalDateTime endDate;

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

	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "custPlanMappping")
	private List<CustQuotaDtls> custQuotaDtlsList = new ArrayList<>();

	private String service;

	private Boolean isDelete;
	
	private Boolean isTrialPlan;

	private Double offerPrice;
	
	private Double taxAmount;
	
	private Long creditdocid;
	
	private Double walletBalUsed;
	
	private String purchaseType;
	
	private Long onlinePurchaseId;
	
	private String purchaseFrom;
	
	private Long debitdocid;

	private Double validity;

	private String planName;

	private Double discount;
	
	private Integer plangroupid;

	private Integer planValidityDays;

	private Boolean isInvoiceToOrg;

	private String billTo;

	private Double newAmount;

	private Integer renewalId;

	private Integer custRefId;

	@Column(name = "s_discount_type")
	private String discountType="One-time";

	@Column(name = "discount_expiry_date")
	private LocalDate discountExpiryDate;

	@Transient
	private LinkAcceptanceDTO linkAcceptanceDTO;

	@Transient
	private PostpaidPlanPojo postpaidPlanPojo;
	
	
	public CustPlanMappping(Integer id) {
		this.id = id;
	}
	
	public CustPlanMappping(CustPlanMapppingPojo custPlanMappingPojo) {
		this.id = custPlanMappingPojo.getId();
		this.planId = custPlanMappingPojo.getPlanId();
		this.custid = custPlanMappingPojo.getCustid();
		if(custPlanMappingPojo.getLeadMasterId() != null) {
			this.leadMaster = new LeadMaster(custPlanMappingPojo.getLeadMasterId());
		}
		this.startDate = custPlanMappingPojo.getStartDate();
		this.endDate = custPlanMappingPojo.getEndDate();
		this.expiryDate = custPlanMappingPojo.getExpiryDate();
		this.startDateString = custPlanMappingPojo.getStartDateString();
		this.endDateString = custPlanMappingPojo.getEndDateString();
		this.expiryDateString = custPlanMappingPojo.getExpiryDateString();
		this.status = custPlanMappingPojo.getStatus();
		this.qospolicyId = custPlanMappingPojo.getQospolicyId();
		this.uploadqos = custPlanMappingPojo.getUploadqos();
		this.downloadqos = custPlanMappingPojo.getDownloadqos();
		this.uploadts = custPlanMappingPojo.getUploadts();
		this.downloadts = custPlanMappingPojo.getDownloadts();
		this.service = custPlanMappingPojo.getService();
		this.isDelete = custPlanMappingPojo.getIsDelete();
		this.offerPrice = custPlanMappingPojo.getOfferPrice();
		this.taxAmount = custPlanMappingPojo.getTaxAmount();
		this.creditdocid = custPlanMappingPojo.getCreditdocid();
		this.walletBalUsed = custPlanMappingPojo.getWalletBalUsed();
		this.purchaseType = custPlanMappingPojo.getPurchaseType();
		this.onlinePurchaseId = custPlanMappingPojo.getOnlinePurchaseId();
		this.purchaseFrom = custPlanMappingPojo.getPurchaseFrom();
		this.debitdocid = custPlanMappingPojo.getDebitdocid();
		this.validity = custPlanMappingPojo.getValidity();
		this.planName = custPlanMappingPojo.getPlanName();
		this.discount = custPlanMappingPojo.getDiscount();
		this.plangroupid = custPlanMappingPojo.getPlangroupid();
		this.planValidityDays = custPlanMappingPojo.getPlanValidityDays();
		this.isInvoiceToOrg = custPlanMappingPojo.getIsInvoiceToOrg();
		this.billTo = custPlanMappingPojo.getBillTo();
		this.newAmount = custPlanMappingPojo.getNewAmount();
		this.renewalId = custPlanMappingPojo.getRenewalId();
		this.custRefId = custPlanMappingPojo.getCustRefId();
		this.discountType=custPlanMappingPojo.getDiscountType();
		this.discountExpiryDate=custPlanMappingPojo.getDiscountExpiryDate();
		this.linkAcceptanceDTO = custPlanMappingPojo.getLinkAcceptanceDTO();
		
		if(custPlanMappingPojo.getQuotaList() != null && custPlanMappingPojo.getQuotaList().size() > 0) {
			List<CustQuotaDtls> custQuotaDtlsList = new ArrayList<CustQuotaDtls>();
			for (CustQuotaDtlsPojo custQuotaDtlsPojo : custPlanMappingPojo.getQuotaList()) {
				custQuotaDtlsList.add(new CustQuotaDtls(custQuotaDtlsPojo));
			}
			this.custQuotaDtlsList = custQuotaDtlsList;
		}
		this.isTrialPlan = custPlanMappingPojo.getIstrialplan();
	}

	@Override
	public int hashCode() {
		return (id != null) ? id.hashCode() : 0;
	}
}
