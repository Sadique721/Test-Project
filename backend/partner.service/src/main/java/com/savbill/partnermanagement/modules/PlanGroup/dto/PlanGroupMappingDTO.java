package com.savbill.partnermanagement.modules.PlanGroup.dto;

import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.modules.Plan.domain.PostpaidPlan;
import com.savbill.partnermanagement.modules.Plan.dto.PostpaidPlanPojo;
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

}
