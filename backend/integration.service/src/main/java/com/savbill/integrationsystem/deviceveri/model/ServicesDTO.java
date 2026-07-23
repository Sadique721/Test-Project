package com.savbill.integrationsystem.deviceveri.model;

import java.time.LocalDateTime;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=false)
public class ServicesDTO extends Auditable<Long> implements IBaseDto{
	private Long serviceid;
	private String servicename;
	private LocalDateTime createdate;
	private Double createdbystaffid;
	private Double lastmodifiedbystaffid;
	private LocalDateTime lastmodifieddate;
	private String createbyname;
	private String updatebyname;
	private Long mvnoid;
	private String icname;
	private String iccode;
	private Long buid;
	private Boolean isQosv;
	private String expiry;
	private String ledgerId;
	private Boolean isDtv;
	private Long investmentcodeId;

	@Override
	public Long getIdentityKey() {
		// TODO Auto-generated method stub
		return serviceid;
	}

	@Override
	public Long getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoid;
	}

	@Override
	public void setMvnoId(Long mvnoId) {
		// TODO Auto-generated method stub
		
	}
}
