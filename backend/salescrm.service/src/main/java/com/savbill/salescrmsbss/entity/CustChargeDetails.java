package com.savbill.salescrmsbss.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.CustChargeDetailsPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
@Table(name = "tblcustchargedtls")
public class CustChargeDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cstchargeid", nullable = false, length = 40)
	private Integer id;

	private Integer planid;

	private Integer chargeid;

	private String chargeName;

	private String chargetype;

	private Double validity;

	private Double price;

	private Double actualprice;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;

	private String remarks;

	private Date charge_date;

	private String chargeDateString;

	private Date startdate;

	private String startdateString;

	private Date enddate;

	private String enddateString;

	private Double taxamount;

	private Boolean is_reversed;

	private LocalDateTime rev_date;

	private String revdateString;

	private Double rev_amt;

	private String rev_remarks;

	private Boolean isUsed;

	private Long purchaseEntityId;

	private Long ippooldtlsid;

	private Long debitdocid;

	private String createDateString;

	private String updateDateString;

	private String type;

	private Integer planValidity;

	private String unitsOfValidity;

	private Integer taxId;

	private Integer custPlanMapppingId;

	private LocalDate lastBillDate;

	private LocalDate nextBillDate;

	private Integer billingCycle;

	private Double discount;
	
	public CustChargeDetails() {}
	
	public CustChargeDetails(CustChargeDetailsPojo custChargeDetailsPojo) {
		this.id = custChargeDetailsPojo.getId();
		this.planid = custChargeDetailsPojo.getPlanid();
		this.chargeid = custChargeDetailsPojo.getChargeid();
		this.chargeName = custChargeDetailsPojo.getChargeName();
		this.chargetype = custChargeDetailsPojo.getChargetype();
		this.validity = custChargeDetailsPojo.getValidity();
		this.price = custChargeDetailsPojo.getPrice();
		this.actualprice = custChargeDetailsPojo.getActualprice();
		if(custChargeDetailsPojo.getLeadMasterId() != null)
			this.leadMaster = new LeadMaster(custChargeDetailsPojo.getLeadMasterId());
		this.remarks = custChargeDetailsPojo.getRemarks();
		this.charge_date = custChargeDetailsPojo.getCharge_date();
		this.chargeDateString = custChargeDetailsPojo.getChargeDateString();
		this.startdate = custChargeDetailsPojo.getStartdate();
		this.startdateString = custChargeDetailsPojo.getStartdateString();
		this.enddate = custChargeDetailsPojo.getEnddate();
		this.enddateString = custChargeDetailsPojo.getEnddateString();
		this.taxamount = custChargeDetailsPojo.getTaxamount();
		this.is_reversed = custChargeDetailsPojo.getIs_reversed();
		this.rev_date = custChargeDetailsPojo.getRev_date();
		this.revdateString = custChargeDetailsPojo.getRevdateString();
		this.rev_amt = custChargeDetailsPojo.getRev_amt();
		this.rev_remarks = custChargeDetailsPojo.getRev_remarks();
		this.isUsed = custChargeDetailsPojo.getIsUsed();
		this.purchaseEntityId = custChargeDetailsPojo.getPurchaseEntityId();
		this.ippooldtlsid = custChargeDetailsPojo.getIppooldtlsid();
		this.debitdocid = custChargeDetailsPojo.getDebitdocid();
		this.createDateString = custChargeDetailsPojo.getCreateDateString();
		this.updateDateString = custChargeDetailsPojo.getUpdateDateString();
		this.type = custChargeDetailsPojo.getType();
		this.planValidity = custChargeDetailsPojo.getPlanValidity();
		this.unitsOfValidity = custChargeDetailsPojo.getUnitsOfValidity();
		this.taxId = custChargeDetailsPojo.getTaxId();
		this.custPlanMapppingId = custChargeDetailsPojo.getCustPlanMapppingId();
		this.lastBillDate = custChargeDetailsPojo.getLastBillDate();
		this.nextBillDate = custChargeDetailsPojo.getNextBillDate();
		this.billingCycle = custChargeDetailsPojo.getBillingCycle();
		this.discount=custChargeDetailsPojo.getDiscount();
	}

}
