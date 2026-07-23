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
public class PostpaidPlanDTO extends Auditable<Long> implements IBaseDto{
	private Long postpaidplanid;
	private String name;
	private String displayname;
	private String plancode;
	private String description;
	private String plancategory;
	private Double maxallowedchild;
	private LocalDateTime startdate;
	private LocalDateTime enddate;
	private Double quota;
	private String quotaunit;
	private String uploadqos;
	private String downloadqos;
	private String status;
	private String planstatus;
	private Double childquota;
	private String childquotaunit;
	private Double slice;
	private String sliceunit;
	private String param1;
	private String param2;
	private String param3;
	private String attachedtoallhotspot;
	private LocalDateTime createdate;
	private Double createdbystaffid;
	private Double lastmodifiedbystaffid;
	private LocalDateTime lastmodifieddate;
	private Long mvnoid;
	private Long taxid;
	private Long serviceid;
	private String plantype;
	private Double dbr;
	private String plangroup;
	private Double validity;
	private String uploadts;
	private String downloadts;
	private Integer allowoverusage;
	private String saccode;
	private String quotatype;
	private String quotaunittime;
	private Double quotatime;
	private Double maxconcurrentsession;
	private Integer qospolicyId;
	private Integer radiusprofileId;
	private Double offerprice;
	private Integer isDeleted;
	private Integer isDelete;
	private Double quotadid;
	private Double quotaintercom;
	private String quotaunitdid;
	private String quotaunitintercom;
	private String createbyname;
	private String updatebyname;
	private Double taxamount;
	private String datacategory;
	private String quotarestinterval;
	private String unitsofvalidity;
	private Long timebasepolicyid;
	private Long planId;
    
    @Override
    public Long getIdentityKey() {
        return postpaidplanid;
    }

    @Override
    public Long getMvnoId() {
        return mvnoid;
    }

    @Override
    public void setMvnoId(Long mvnoId) {
    	
    }
}
