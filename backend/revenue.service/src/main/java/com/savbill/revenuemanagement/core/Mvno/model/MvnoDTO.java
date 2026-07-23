package com.savbill.revenuemanagement.core.Mvno.model;

import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.dto.common.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class MvnoDTO extends Auditable implements IBaseDto {
 
	private Long id;
	
	private String name;

	private String username;

	private String password;

	private String address;

	private String fullName;

	private String suffix;
	
	private String description;
	
	private String email;
	
	private String phone;
	
	private String status;
	
	private String logfile;
	
	private String mvnoHeader;
	
	private String mvnoFooter;
	
    private Boolean isDelete = false;
	
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		return null;
	}

	@Override
	public void setMvnoId(Integer mvnoId) {
	}

	@Override
	public Long getBuId() {
		return null;
	}

}
