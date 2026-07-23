package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.dto.IBaseDto;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.PolyGone;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAreaDTO extends Auditable implements IBaseDto {
    private Long id;
    private String name;
    private String siteName;
    private String status;
    private Boolean isDeleted = false;
    private String latitude;
    private String longitude;
    
    private Long areaid;
    private Integer mvnoId;

    private List<Integer> pincodes;
    private List<Integer> pincodeIds;

    private Long cityid;

    private List<PolyGone> polyGoneList;

    private Long displayId;
    private String displayName;
    private String radius;
    private double radiusDis;
    private String serviceAreaType;
    private String blockNo;

    private List<Integer> mvnoIds;

    private String mvnoLists;

    private List<Long> locationIds;

    @Transient
    private Integer branchId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}

    public ServiceAreaDTO(Long id, String name, String latitude, String longitude, Integer mvnoId, String radius) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mvnoId = mvnoId;
        this.radius = radius;
    }

    // Copy constructor
    public ServiceAreaDTO(ServiceAreaDTO other) {
        this.id = other.id;
        this.name = other.name;
        this.siteName = other.siteName;
        this.status = other.status;
        this.isDeleted = other.isDeleted;
        this.latitude = other.latitude;
        this.longitude = other.longitude;
        this.areaid = other.areaid;
        this.mvnoId = other.mvnoId;
        this.pincodes = other.pincodes != null ? new ArrayList<>(other.pincodes) : null;
        this.cityid = other.cityid;
        this.polyGoneList = other.polyGoneList != null ? new ArrayList<>(other.polyGoneList) : null;
        this.displayId = other.displayId;
        this.displayName = other.displayName;
        this.radius = other.radius;
        this.radiusDis = other.radiusDis;
        this.serviceAreaType = other.serviceAreaType;
        this.blockNo = other.blockNo;
        this.mvnoIds = other.mvnoIds != null ? new ArrayList<>(other.mvnoIds) : null;
    }



    public ServiceAreaDTO(Long id, String name, String siteName, String status, Boolean isDeleted, Integer mvnoId, String latitude, String longitude, Long areaId, Long cityid, String radius, String serviceAreaType, String blockNo, String mvnoLists) {
        this.id = id;
        this.name = name;
        this.siteName = siteName;
        this.status = status;
        this.isDeleted = isDeleted;
        this.mvnoId = mvnoId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.areaid = areaId;
        this.cityid = cityid;
        this.radius = radius;
        this.serviceAreaType = serviceAreaType;
        this.blockNo = blockNo;
        this.mvnoLists = mvnoLists;
        this.displayId = id;
        this.displayName = name;
    }
}
