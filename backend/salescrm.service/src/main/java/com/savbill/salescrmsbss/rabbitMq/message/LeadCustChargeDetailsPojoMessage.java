package com.savbill.salescrmsbss.rabbitMq.message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.savbill.salescrmsbss.entity.pojo.CustChargeDetailsPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadCustChargeDetailsPojoMessage {

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

	private String chargeDateString;

	private String startdateString;

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

	private String lastBillDate;

	private String nextBillDate;

	private Integer billingCycle;
	
	public LeadCustChargeDetailsPojoMessage(CustChargeDetailsPojo custChargeDetails) {
		this.id = custChargeDetails.getId();
		this.planid = custChargeDetails.getPlanid();
		this.chargeid = custChargeDetails.getChargeid();
		this.chargeName = custChargeDetails.getChargeName();
		this.chargetype = custChargeDetails.getChargetype();
		this.validity = custChargeDetails.getValidity();
		this.price = custChargeDetails.getPrice();
		this.actualprice = custChargeDetails.getActualprice();
		this.leadMasterId = custChargeDetails.getLeadMasterId();
		this.remarks = custChargeDetails.getRemarks();
		this.chargeDateString = custChargeDetails.getChargeDateString();
		this.startdateString = custChargeDetails.getStartdateString();
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
		if (custChargeDetails.getLastBillDate() != null) {
			this.lastBillDate = custChargeDetails.getLastBillDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		if (custChargeDetails.getNextBillDate() != null) {
			this.nextBillDate = custChargeDetails.getNextBillDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		}
		this.billingCycle = custChargeDetails.getBillingCycle();
	}
}
