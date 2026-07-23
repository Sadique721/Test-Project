package com.savbill.integrationsystem.rms.entity;


import com.savbill.integrationsystem.billgen.entity.ServiceArea;
import com.savbill.integrationsystem.core.data.IBaseData;
import com.savbill.integrationsystem.core.dto.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltwarehousemanagement")
public class WareHouse extends Auditable implements IBaseData<Long> {

    @Id
    @Column(name = "warehouse_id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "address1", nullable = false)
    private String address1;
    @Column(name = "address2", nullable = false)
    private String address2;
    @Column(name = "pincode", nullable = false)
    private String pincode;
    @Column(name = "state", nullable = false)
    private String state;
    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "country", nullable = false)
    private String country;
    @Column(name = "latitude", nullable = false)
    private String latitude;
    @Column(name = "longitude", nullable = false)
    private String longitude;
    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "rms_warehouse_id")
    private String rmsWarehouseId;

    @Column(name = "nav_warehouse_id")
    private String navWarehouseId;
    @Column(name = "warehouse_code")
    private String warehouseCode;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblwarehousemanagmentservicearearel", joinColumns = {@JoinColumn(name = "warehouse_id")}
            , inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();

    @Column (name = "warehousetype")
    private String warehouseType;

    @Column(name = "branch_id")
    private Long branchId;

//    @OneToMany
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @JoinTable(name = "tblwarehousemanagmentteamsmapping", joinColumns = {@JoinColumn(name = "warehouse_id")}
//            , inverseJoinColumns = {@JoinColumn(name = "team_id")})
//    private List<TeamsDTO> teamsIdsList = new ArrayList<>();

//    @Transient
//    private List<TeamsDTO> teamsList = new ArrayList<>();

    public WareHouse(Long id) {
        this.id = id;
    }

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }
}