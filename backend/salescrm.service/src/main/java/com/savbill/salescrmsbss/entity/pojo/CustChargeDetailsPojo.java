package com.savbill.salescrmsbss.entity.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

import com.savbill.salescrmsbss.entity.CustChargeDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustChargeDetailsPojo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cstchargeid", nullable = false, length = 40)
	private Integer id;

	private Integer planid;

	private Integer chargeid;

	private String chargeName;

	private String chargetype;

	private Double validity = 0.0;

	private Double price = 0.0;

	private Double actualprice = 0.0;

	private Long leadMasterId;

	private String remarks;

	private Date charge_date;

	private String chargeDateString;

	private Date startdate;

	private String startdateString;

	private Date enddate;

	private String enddateString;

	private Double taxamount;

	private Boolean is_reversed = false;

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

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate lastBillDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate nextBillDate;

	private Integer billingCycle;

	private Double discount=0.0;
	
	public CustChargeDetailsPojo(CustChargeDetails custChargeDetails) {
		this.id = custChargeDetails.getId();
		this.planid = custChargeDetails.getPlanid();
		this.chargeid = custChargeDetails.getChargeid();
		this.chargeName = custChargeDetails.getChargeName();
		this.chargetype = custChargeDetails.getChargetype();
		this.validity = custChargeDetails.getValidity();
		this.price = custChargeDetails.getPrice();
		this.actualprice = custChargeDetails.getActualprice();
		if(custChargeDetails.getLeadMaster() != null)
			this.leadMasterId = custChargeDetails.getLeadMaster().getId();
		this.remarks = custChargeDetails.getRemarks();
		this.charge_date = custChargeDetails.getCharge_date();
		this.chargeDateString = custChargeDetails.getChargeDateString();
		this.startdate = custChargeDetails.getStartdate();
		this.startdateString = custChargeDetails.getStartdateString();
		this.enddate = custChargeDetails.getEnddate();
		this.enddateString = custChargeDetails.getEnddateString();
		this.taxamount = custChargeDetails.getTaxamount();
		this.is_reversed = custChargeDetails.getIs_reversed();
		this.rev_date = custChargeDetails.getRev_date();
		this.revdateString = custChargeDetails.getRevdateString();
		this.rev_amt = custChargeDetails.getRev_amt();
		this.rev_remarks = custChargeDetails.getRev_remarks();
		this.isUsed = custChargeDetails.getIsUsed();
		this.purchaseEntityId = custChargeDetails.getPurchaseEntityId();
		this.ippooldtlsid = custChargeDetails.getIppooldtlsid();
		this.debitdocid = custChargeDetails.getDebitdocid();
		this.createDateString = custChargeDetails.getCreateDateString();
		this.updateDateString = custChargeDetails.getUpdateDateString();
		this.type = custChargeDetails.getType();
		this.planValidity = custChargeDetails.getPlanValidity();
		this.unitsOfValidity = custChargeDetails.getUnitsOfValidity();
		this.taxId = custChargeDetails.getTaxId();
		this.custPlanMapppingId = custChargeDetails.getCustPlanMapppingId();
		this.lastBillDate = custChargeDetails.getLastBillDate();
		this.nextBillDate = custChargeDetails.getNextBillDate();
		this.billingCycle = custChargeDetails.getBillingCycle();
		this.discount=custChargeDetails.getDiscount();
	}
}
