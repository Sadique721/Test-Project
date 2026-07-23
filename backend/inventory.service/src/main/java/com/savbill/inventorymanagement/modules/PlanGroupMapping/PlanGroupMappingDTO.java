package com.savbill.inventorymanagement.modules.PlanGroupMapping;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlan;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlanPojo;
import lombok.Data;

import javax.persistence.Transient;

@Data
public class PlanGroupMappingDTO extends Auditable {

	private Integer planGroupMappingId;
	private Integer planId;
	@Transient
	private PostpaidPlanPojo postpaidPlanPojo;
	private Integer planGroupId;
	private PostpaidPlan plan;
	private Double validity;
	private Integer mvnoId;
	private String service;
	private Double newOfferPrice;
//	private List<PlanGroupMappingChargeRelDto> chargeList;

}
