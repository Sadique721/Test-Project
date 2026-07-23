package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmservicearea")
@EntityListeners(AuditableListener.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)
public class ServiceArea extends Auditable implements IBaseData<Long> {
    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_area_id")
    private Long id;

    private String name;

    @Column(name = "site_name")
    private String siteName;

    private String status;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;


    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "latitude", nullable = false, length = 50)
    private String latitude;

    @Column(name = "longitude", nullable = false, length = 50)
    private String longitude;

    @Column(name = "areaid", nullable = true)
    private Long areaId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbltserviceareapincoderel", joinColumns = {@JoinColumn(name = "serviceareaid")}, inverseJoinColumns = {@JoinColumn(name = "pincodeid")})
    private List<Pincode> pincodeList = new ArrayList<>();

    @Column(name = "cityid", length = 40)
    private Long cityid;

    @Column(name="is_bind_with_plan",nullable = false)
    private Boolean isBindWithPlan;
    @Column(name = "radius", nullable = false, length = 50)
    private String radius;

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "service_area_id")
    private List<PolyGone> polyGoneList;

    @Column(name="service_area_type")
    private String serviceAreaType;

    @Column(name="blockno")
    private String blockNo;

    @DiffIgnore
    @Column(name="mvno_lists")
    private String mvnoLists;

    @ManyToMany
    @JoinTable(
            name = "tbltservicearealocationmapping",
            joinColumns = @JoinColumn(name = "service_area_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Set<LocationMaster> locations = new HashSet<>();

    @Transient
    private List<Long> locationIdList;

    public ServiceArea(Long id) {
        this.id = id;
    }


    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }

    @Override
    public void setBuId(Long buId) {

    }

    public ServiceArea(Long id, Integer mvnoId, String latitude, String longitude, String radius) {
        this.id = id;
        this.mvnoId = mvnoId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public ServiceArea(Long id , String name, Integer mvnoId, String latitude, String longitude, String radius) {
        this.id = id;
        this.name = name;
        this.mvnoId = mvnoId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public ServiceArea(Long id , String name) {
        this.id = id;
        this.name = name;
    }

    public ServiceArea(ServiceArea serviceArea) {
        this.id = serviceArea.getId();
        this.name = serviceArea.getName();
        this.status = serviceArea.getStatus();
        this.isDeleted = serviceArea.getIsDeleted();
        this.mvnoId = serviceArea.getMvnoId();
        this.latitude = serviceArea.getLatitude();
        this.longitude = serviceArea.getLongitude();
        this.areaId = serviceArea.getAreaId();
        this.radius = serviceArea.getRadius();
        List<Pincode> pincodes=new ArrayList<>();
        for(Pincode code: serviceArea.getPincodeList()){
            Pincode pincode=new Pincode(code);
            pincodes.add(pincode);
        }
        this.pincodeList =pincodes ;
        this.cityid = serviceArea.getCityid();
        this.setCreatedByName(serviceArea.getCreatedByName());
        this.setLastModifiedByName(serviceArea.getLastModifiedByName());
        this.radius=serviceArea.getRadius();
    }

    public ServiceArea(Long id, String name, String status, Integer mvnoId, String latitude, String longitude, String serviceAreaType) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.mvnoId = mvnoId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.serviceAreaType = serviceAreaType;
    }

    public ServiceArea(Long id, String name, String status, Integer mvnoId, String siteName, String latitude, String longitude, Long areaId, Long cityid, Boolean isBindWithPlan, String radius, String serviceAreaType, String blockNo) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.mvnoId = mvnoId;
        this.siteName = siteName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.areaId = areaId;
        this.cityid = cityid;
        this.isBindWithPlan = isBindWithPlan;
        this.radius = radius;
        this.serviceAreaType = serviceAreaType;
        this.blockNo = blockNo;
    }
}
