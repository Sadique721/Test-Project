package com.savbill.revenuemanagement.mastermanagement.City.dto;

import com.savbill.revenuemanagement.core.dto.common.Auditable;

import com.savbill.revenuemanagement.mastermanagement.State.dto.StatePojo;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CityPojo extends Auditable {
	
	private Integer id;

	@NotNull
    private String name;
	
	@NotNull
    private String status;
	
	@NotNull
	private Integer countryId;

	private String stateName;
	private String countryName;

	@NotNull
    private StatePojo statePojo;

	private Boolean isDelete = false;

	private Integer mvnoId;

	private Integer displayId;
	private String displayName;
}
