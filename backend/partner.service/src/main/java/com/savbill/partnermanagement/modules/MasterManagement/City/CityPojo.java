package com.savbill.partnermanagement.modules.MasterManagement.City;

import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.core.dto.IBaseDto;
import com.savbill.partnermanagement.modules.MasterManagement.State.StatePojo;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CityPojo extends Auditable implements IBaseDto {
	
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

	@Override
	public Long getIdentityKey() {
		return Long.valueOf(id);
	}

	@Override
	public Long getBuId() {
		return null;
	}
}
