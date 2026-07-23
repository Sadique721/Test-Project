package com.savbill.integrationsystem.deviceveri.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.integrationsystem.core.data.IBaseData;

import lombok.Data;

@Data
@Entity
@Table(name = "tbltcreditdebitmapping")
public class CreditDebitMappingData implements IBaseData<Long> {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="creddebtmappingid") 
	private Long creddebtmappingid;
	@Column(name="CREDITDOCID") 
	private Long creditdocid;
	@Column(name="debitdocumentid") 
	private Long debitdocumentid;
	@Column(name="adjustedamount") 
	private Double adjustedamount;
	@Column(name="is_deleted") 
	private Integer isDeleted;
	@Column(name="withdrawal_id") 
	private Long withdrawalId;


	@Override
	public Long getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeleteFlag(boolean deleteFlag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDeleteFlag() {
		// TODO Auto-generated method stub
		return false;
	}
}
