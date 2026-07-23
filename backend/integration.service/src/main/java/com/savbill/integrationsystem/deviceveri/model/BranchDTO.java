package com.savbill.integrationsystem.deviceveri.model;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=false)
public class BranchDTO extends Auditable<Long> implements IBaseDto{
	private Long id;
	private String name;
	private String status;
	private String branchCode;
	private Boolean isDeleted;
	private Long mvnoid;
	private LocalDateTime createddate;
	private LocalDateTime lastmodifieddate;
	private Integer createdbystaffid;
	private Integer lastmodifiedbystaffid;
	private String createbyname;
	private String updatebyname;
	@Override
	public Long getIdentityKey() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public Long getMvnoId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMvnoId(Long mvnoId) {
		// TODO Auto-generated method stub
		
	}
}
