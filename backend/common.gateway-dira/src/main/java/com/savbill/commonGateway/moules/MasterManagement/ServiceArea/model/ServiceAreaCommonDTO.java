package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model;

import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.PolyGone;
import lombok.Data;

import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ServiceAreaCommonDTO {
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

    private LocalDateTime createdate;
    private LocalDateTime updatedate;
    private String createdByName;
    private String lastModifiedByName;
    private Integer createdById;
    private Integer lastModifiedById;


    public ServiceAreaCommonDTO(Long id, String name, String siteName, String status, Boolean isDeleted,
                          Integer mvnoId, String latitude, String longitude, Long areaId, Long cityid,
                          String radius, String serviceAreaType, String blockNo, String mvnoLists,
                          LocalDateTime createdate, LocalDateTime updatedate,
                          String createdByName, String lastModifiedByName,
                          Integer createdById, Integer lastModifiedById) {
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
        this.createdate = createdate;
        this.updatedate = updatedate;
        this.createdByName = createdByName;
        this.lastModifiedByName = lastModifiedByName;
        this.createdById = createdById;
        this.lastModifiedById = lastModifiedById;
    }
}
