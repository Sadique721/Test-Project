package com.savbill.revenuemanagement.productmanagement.Discount.dto;


import com.savbill.revenuemanagement.productmanagement.Discount.domain.DiscountPlanMapping;

import javax.validation.constraints.NotNull;

public class DiscountPlanMappingPojo {
	
	private Integer id;
    
	@NotNull
	private Integer planId;

	public DiscountPlanMappingPojo() {}
	
	public DiscountPlanMappingPojo(DiscountPlanMapping discountPlanMapping) {
		this.id = discountPlanMapping.getId();
		this.planId = discountPlanMapping.getPlanId();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}

	@Override
	public String toString() {
		return "DiscountPlanMappingPojo [id=" + id + ", planId=" + planId + "]";
	}
}
