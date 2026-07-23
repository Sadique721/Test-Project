package com.savbill.integrationsystem.deviceveri.domain;

import com.savbill.integrationsystem.core.data.IBaseData;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblmcustledger")
public class CustomerLedgerData implements IBaseData<Long>{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name="CUSTLEDGERID")
	private Long id;
	@Column (name="TOTALDUE")
	private Double totaldue;
	@Column (name="TOTALPAID")
	private Double totalpaid;
	@Column (name="CUSTID")
	private Long custid;
	@Column (name="CREATEDATE")
	private LocalDateTime createdate;
	@Column (name="LASTMODIFIEDDATE")
	private LocalDateTime lastmodifieddate;
	@Column (name="createbyname")
	private String createbyname;
	@Column (name="updatebyname")
	private String updatebyname;
	@Column (name="CREATEDBYSTAFFID")
	private Integer createdbystaffid;
	@Column (name="LASTMODIFIEDBYSTAFFID")
	private Integer lastmodifiedbystaffid;

	@Override
	public Long getPrimaryKey() {
		return id;
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
