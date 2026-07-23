package com.savbill.salescrmsbss.rabbitMq.message;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import com.savbill.salescrmsbss.entity.pojo.CustChargeDetailsPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustChargeDetailsPojoMessage {

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

	private String charge_date;

	private String chargeDateString;

	private String startdate;

	private String startdateString;

	private String enddate;

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

	public CustChargeDetailsPojoMessage(CustChargeDetailsPojo custChargeDetailsPojo) {
		this.id = custChargeDetailsPojo.getId();
		this.planid = custChargeDetailsPojo.getPlanid();
		this.chargeid = custChargeDetailsPojo.getChargeid();
		this.chargeName = custChargeDetailsPojo.getChargeName();
		this.chargetype = custChargeDetailsPojo.getChargetype();
		this.validity = custChargeDetailsPojo.getValidity();
		this.price = custChargeDetailsPojo.getPrice();
		this.actualprice = custChargeDetailsPojo.getActualprice();
		this.leadMasterId = custChargeDetailsPojo.getLeadMasterId();
		this.remarks = custChargeDetailsPojo.getRemarks();
		if(custChargeDetailsPojo.getCharge_date() != null) {
			this.charge_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(custChargeDetailsPojo.getCharge_date());
		}
		this.chargeDateString = custChargeDetailsPojo.getChargeDateString();
		if(custChargeDetailsPojo.getStartdate() != null)
			this.startdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(custChargeDetailsPojo.getStartdate());
		this.startdateString = custChargeDetailsPojo.getStartdateString();
		if(custChargeDetailsPojo.getEnddate() != null)
			this.enddate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(custChargeDetailsPojo.getEnddate());
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
		if (custChargeDetailsPojo.getLastBillDate() != null)
			this.lastBillDate = custChargeDetailsPojo.getLastBillDate().toString();
		if (custChargeDetailsPojo.getNextBillDate() != null)
			this.nextBillDate = custChargeDetailsPojo.getNextBillDate().toString();
		this.billingCycle = custChargeDetailsPojo.getBillingCycle();
	}
}
