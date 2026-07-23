package com.savbill.integrationsystem.deviceveri.domain;

import com.savbill.integrationsystem.core.data.IBaseData;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblmsubscriberaddressrel")
public class SubscriberaddressrelData implements IBaseData<Long>{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name="ADDRESSID")
	private Long id;
	@Column (name="SUBSCRIBERID")
	private Long subscriberid;
	@Column (name="ADDRESSTYPE")
	private String addresstype;
	@Column (name="ADDRESS1")
	private String address1;
	@Column (name="ADDRESS2")
	private String address2;
	@Column (name="CITYID")
	private Long cityid;
	@Column (name="STATEID")
	private Long stateid;
	@Column (name="COUNTRYID")
	private Long countryid;
	@Column (name="is_delete")
	private Boolean isDelete;
	@Column (name="landmark")
	private String landmark;
	@Column (name="createbyname")
	private String createbyname;
	@Column (name="updatebyname")
	private String updatebyname;
	@Column (name="CREATEDBYSTAFFID")
	private Integer createdbystaffid;
	@Column (name="LASTMODIFIEDBYSTAFFID")
	private Integer lastmodifiedbystaffid;
	@Column (name="createdate")
	private LocalDateTime createdate;
	@Column (name="lastmodifieddate")
	private LocalDateTime lastmodifieddate;
	@Column (name="PINCODEID")
	private Long pincodeid;
	@Column (name="AREAID")
	private Long areaid;
	@Column (name="next_team_hir_mapping")
	private Long nextTeamHirMapping;
	@Column (name="next_staff")
	private Long nextStaff;
	@Column (name="status")
	private String status;
	@Column (name="version")
	private String version;
	@Column (name="landmark1")
	private String landmark1;
	@Column (name="shift_id")
	private Long shiftId;
	@Column (name="shifted_partner_id")
	private Long shiftedPartnerId;
	@Column (name="shifted_service_area_id")
	private Long shiftedServiceAreaId;

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
