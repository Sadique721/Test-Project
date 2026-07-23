package com.savbill.integrationsystem.deviceveri.domain;

import java.time.LocalDateTime;

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
@Table(name = "tblmservices")
public class ServicesData implements IBaseData<Long>{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="serviceid") 
	private Long serviceid;
	@Column(name="servicename") 
	private String servicename;
	@Column(name="CREATEDATE") 
	private LocalDateTime createdate;
	@Column(name="CREATEDBYSTAFFID") 
	private Double createdbystaffid;
	@Column(name="LASTMODIFIEDBYSTAFFID") 
	private Double lastmodifiedbystaffid;
	@Column(name="LASTMODIFIEDDATE") 
	private LocalDateTime lastmodifieddate;
	@Column(name="createbyname") 
	private String createbyname;
	@Column(name="updatebyname") 
	private String updatebyname;
	@Column(name="MVNOID") 
	private Long mvnoid;
	@Column(name="icname") 
	private String icname;
	@Column(name="iccode") 
	private String iccode;
	@Column(name="BUID") 
	private Long buid;
	@Column(name="is_qosv") 
	private Boolean isQosv;
	@Column(name="expiry") 
	private String expiry;
	/*
	@Column(name="ledger_id") 
	private String ledgerId;
	*/
	@Column(name="is_dtv") 
	private Boolean isDtv;
	@Column(name="investmentcode_id") 
	private Long investmentcodeId;



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
