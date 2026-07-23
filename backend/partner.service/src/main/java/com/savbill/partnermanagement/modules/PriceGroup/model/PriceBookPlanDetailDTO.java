package com.savbill.partnermanagement.modules.PriceGroup.model;

import com.savbill.partnermanagement.core.dto.IBaseDto;
import com.savbill.partnermanagement.modules.Plan.dto.PostpaidPlanPojo;
import com.savbill.partnermanagement.modules.PlanGroup.dto.PlanGroupDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class PriceBookPlanDetailDTO implements IBaseDto {
    private Long id;
    private Double offerprice;
    private Double partnerofficeprice;
    private String revsharen = "No";
    private String registration = "No";
    private String renewal = "No";

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private PriceBookDTO priceBook;

    private PostpaidPlanPojo postpaidPlan;

    private Boolean isDeleted = false;
    
    private String revenueSharePercentage;
    
    private Boolean isTaxIncluded = true;
    
    private Integer mvnoId;

    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}

    @Override
    public Long getBuId() {
        return null;
    }


    public PlanGroupDTO planGroup;

}
