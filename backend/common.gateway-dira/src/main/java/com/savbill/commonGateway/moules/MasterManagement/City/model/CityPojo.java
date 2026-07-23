package com.savbill.commonGateway.moules.MasterManagement.City.model;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.moules.MasterManagement.State.model.StatePojo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
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


	public CityPojo(Integer id, String name, String status, Integer countryId,String countryName, String stateName, Boolean isDelete, Integer mvnoId, Integer displayId, String displayName) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.countryId = countryId;
		this.countryName = countryName;
		this.stateName = stateName;
		this.isDelete = isDelete;
		this.mvnoId = mvnoId;
		this.displayId = displayId;
		this.displayName = displayName;
	}

}
