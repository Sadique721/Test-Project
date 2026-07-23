package com.savbill.integrationsystem.deviceveri.model;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=false)
public class CreditDebitMappingDTO extends Auditable<Long> implements IBaseDto{
	private Long creddebtmappingid;
	private Long creditdocid;
	private Long debitdocumentid;
	private Double adjustedamount;
	private Integer isDeleted;
	private Long withdrawalId;

	@Override
	public Long getIdentityKey() {
		// TODO Auto-generated method stub
		return creddebtmappingid;
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
