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
@Table(name = "tblmpostpaidplan")
public class PostpaidPlanData implements IBaseData<Long> {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="POSTPAIDPLANID") 
	private Long postpaidplanid;
	@Column(name="NAME") 
	private String name;
	@Column(name="DISPLAYNAME") 
	private String displayname;
	@Column(name="PLANCODE") 
	private String plancode;
	@Column(name="DESCRIPTION") 
	private String description;
	@Column(name="PLANCATEGORY") 
	private String plancategory;
	@Column(name="MAXALLOWEDCHILD") 
	private Double maxallowedchild;
	@Column(name="STARTDATE") 
	private LocalDateTime startdate;
	@Column(name="ENDDATE") 
	private LocalDateTime enddate;
	@Column(name="QUOTA") 
	private Double quota;
	@Column(name="QUOTAUNIT") 
	private String quotaunit;
	@Column(name="UPLOADQOS") 
	private String uploadqos;
	@Column(name="DOWNLOADQOS") 
	private String downloadqos;
	@Column(name="STATUS") 
	private String status;
	@Column(name="PLANSTATUS") 
	private String planstatus;
	@Column(name="CHILDQUOTA") 
	private Double childquota;
	@Column(name="CHILDQUOTAUNIT") 
	private String childquotaunit;
	@Column(name="SLICE") 
	private Double slice;
	@Column(name="SLICEUNIT") 
	private String sliceunit;
	@Column(name="PARAM1") 
	private String param1;
	@Column(name="PARAM2") 
	private String param2;
	@Column(name="PARAM3") 
	private String param3;
	@Column(name="ATTACHEDTOALLHOTSPOT") 
	private String attachedtoallhotspot;
	@Column(name="CREATEDATE") 
	private LocalDateTime createdate;
	@Column(name="CREATEDBYSTAFFID") 
	private Double createdbystaffid;
	@Column(name="LASTMODIFIEDBYSTAFFID") 
	private Double lastmodifiedbystaffid;
	@Column(name="LASTMODIFIEDDATE") 
	private LocalDateTime lastmodifieddate;
	@Column(name="MVNOID") 
	private Long mvnoid;
	@Column(name="TAXID") 
	private Long taxid;
	@Column(name="serviceid") 
	private Long serviceid;
	@Column(name="plantype") 
	private String plantype;
	@Column(name="dbr") 
	private Double dbr;
	@Column(name="plangroup") 
	private String plangroup;
	@Column(name="validity") 
	private Double validity;
	@Column(name="UPLOADTS") 
	private String uploadts;
	@Column(name="DOWNLOADTS") 
	private String downloadts;
	@Column(name="allowoverusage") 
	private Integer allowoverusage;
	@Column(name="saccode") 
	private String saccode;
	@Column(name="quotatype") 
	private String quotatype;
	@Column(name="quotaunittime") 
	private String quotaunittime;
	@Column(name="quotatime") 
	private Double quotatime;
	@Column(name="maxconcurrentsession") 
	private Double maxconcurrentsession;
	@Column(name="qospolicy_id") 
	private Integer qospolicyId;
	@Column(name="radiusprofile_id") 
	private Integer radiusprofileId;
	@Column(name="offerprice") 
	private Double offerprice;
	@Column(name="is_deleted") 
	private Integer isDeleted;
	@Column(name="is_delete") 
	private Integer isDelete;
	@Column(name="quotadid") 
	private Double quotadid;
	@Column(name="quotaintercom") 
	private Double quotaintercom;
	@Column(name="quotaunitdid") 
	private String quotaunitdid;
	@Column(name="quotaunitintercom") 
	private String quotaunitintercom;
	@Column(name="createbyname") 
	private String createbyname;
	@Column(name="updatebyname") 
	private String updatebyname;
	@Column(name="taxamount") 
	private Double taxamount;
	@Column(name="datacategory") 
	private String datacategory;
	@Column(name="quotarestinterval") 
	private String quotarestinterval;
	@Column(name="unitsofvalidity") 
	private String unitsofvalidity;
	@Column(name="timebasepolicyid") 
	private Long timebasepolicyid;
	@Column(name="plan_id") 
	private Long planId;


	@Override
	public Long getPrimaryKey() {
		// TODO Auto-generated method stub
		return postpaidplanid;
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
