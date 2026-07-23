package com.savbill.cpm.pojo.api;

import com.savbill.cpm.model.common.Auditable;

import com.savbill.cpm.model.postpaid.PlanGroupMappingChargeRelDto;
import com.savbill.cpm.model.postpaid.PostpaidPlan;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

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
	private List<PlanGroupMappingChargeRelDto> chargeList;
	private String mvnoName;

}
