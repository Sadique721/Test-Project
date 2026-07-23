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
public class SubscriberaddressrelDTO extends Auditable<Long> implements IBaseDto{
	private Long id;
	private Long subscriberid;
	private String addresstype;
	private String address1;
	private String address2;
	private Long cityid;
	private Long stateid;
	private Long countryid;
	private Boolean isDelete;
	private String landmark;
	private String createbyname;
	private String updatebyname;
	private Integer createdbystaffid;
	private Integer lastmodifiedbystaffid;
	private LocalDateTime createdate;
	private LocalDateTime lastmodifieddate;
	private Long pincodeid;
	private Long areaid;
	private Long nextTeamHirMapping;
	private Long nextStaff;
	private String status;
	private String version;
	private String landmark1;
	private Long shiftId;
	private Long shiftedPartnerId;
	private Long shiftedServiceAreaId;
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
