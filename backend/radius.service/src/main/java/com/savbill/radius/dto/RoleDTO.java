package com.savbill.radius.dto;


import com.savbill.radius.entity.Auditable;
import com.savbill.radius.entity.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
public class RoleDTO extends Auditable implements IBaseDto {
	

	private Long id;

	@NotNull
    private String rolename;
    
	@NotNull
    private String status;

	@NotNull
	private Boolean sysRole = false;

    //private Set<Integer> staffuserIds;

    private Integer mvnoId;

	private Integer lcoId;


	public Boolean getDelete() {
		return isDelete;
	}

	public void setDelete(Boolean delete) {
		isDelete = delete;
	}

	private Boolean isDelete = false;

//	private Integer lcoId;

	@JsonIgnore
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
}
