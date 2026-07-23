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
public class CountryDTO extends Auditable<Long> implements IBaseDto{
	private Long id;
	private String name;
	private String status;
	private LocalDateTime createdate;
	private Integer createdbystaffid;
	private Integer lastmodifiedbystaffid;
	private LocalDateTime lastmodifieddate;
	private Boolean isDelete;
	private String createbyname;
	private String updatebyname;
	private Long mvnoid;

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
