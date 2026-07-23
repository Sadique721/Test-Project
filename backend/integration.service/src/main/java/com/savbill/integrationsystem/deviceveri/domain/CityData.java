package com.savbill.integrationsystem.deviceveri.domain;

import com.savbill.integrationsystem.core.data.IBaseData;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblmcity")
public class CityData implements IBaseData<Long>{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name="CITYID")
	private Long id;
	@Column (name="NAME")
	private String name;
	@Column (name="STATEID")
	private Long stateid;
	@Column (name="status")
	private String status;
	@Column (name="CREATEDATE")
	private LocalDateTime createdate;
	@Column (name="CREATEDBYSTAFFID")
	private LocalDateTime createdbystaffid;
	@Column (name="LASTMODIFIEDBYSTAFFID")
	private Integer lastmodifiedbystaffid;
	@Column (name="LASTMODIFIEDDATE")
	private Integer lastmodifieddate;
	@Column (name="countryid")
	private Long countryid;
	@Column (name="is_delete")
	private Boolean isDelete;
	@Column (name="createbyname")
	private String createbyname;
	@Column (name="updatebyname")
	private String updatebyname;
	@Column (name="MVNOID")
	private Long mvnoid;

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
