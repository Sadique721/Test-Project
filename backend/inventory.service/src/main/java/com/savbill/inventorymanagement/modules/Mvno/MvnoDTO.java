package com.savbill.inventorymanagement.modules.Mvno;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class MvnoDTO extends Auditable implements IBaseDto {
 
	private Long id;
	
	private String name;

	private String username;

	private String password;

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

}
