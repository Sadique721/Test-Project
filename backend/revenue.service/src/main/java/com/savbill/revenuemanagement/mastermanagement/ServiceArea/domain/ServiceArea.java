package com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain;


import com.savbill.revenuemanagement.core.data.IBaseData;
import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.savbill.revenuemanagement.mastermanagement.Pincode.domain.Pincode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmservicearea")
@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners(AuditableListener.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)
public class ServiceArea extends Auditable implements IBaseData {
    @Id
    @Column(name = "service_area_id")
    private Long id;

    private String name;

    private String status;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "servicearea")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @LazyCollection(LazyCollectionOption.FALSE)
//    private List<NetworkDevices> networkDevicesList = new ArrayList<>();
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    
    @Column(name = "latitude", nullable = false, length = 50)
    private String latitude;
    
    @Column(name = "longitude", nullable = false, length = 50)
    private String longitude;

    @Column(name = "areaid", nullable = true)
    private Long areaId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tblserviceareapincoderel", joinColumns = {@JoinColumn(name = "serviceareaid")}, inverseJoinColumns = {@JoinColumn(name = "pincodeid")})
    private List<Pincode> pincodeList = new ArrayList<>();

    @Column(name = "cityid", length = 40)
    private Long cityid;

    public ServiceArea(Long id) {
        this.id = id;
    }



    @JoinColumn

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

    public ServiceArea(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
