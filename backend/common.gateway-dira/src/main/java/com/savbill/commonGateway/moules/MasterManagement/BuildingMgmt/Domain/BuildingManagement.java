package com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "tblmbuildingmanagement")
@EntityListeners(AuditableListener.class)
@NoArgsConstructor
@Where(clause = "is_deleted = false")
public class
BuildingManagement extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "building_mgmt_id",nullable = false)
    private Long buildingMgmtId;

    @Column(name = "building_name",nullable = false)
    private String buildingName;

    @Column(name = "pincode_id")
    private Integer pincodeId;

    @Column(name = "area_id")
    private Integer areaId;

    @Column(name = "sub_area_id")
    private Integer subAreaId;


    @Column(name = "mvnoid",nullable = false)
    private Integer mvnoId;

    @Column(name = "buid")
    private Integer buid;

    @Column(name = "is_deleted",nullable = false)
    private Boolean isDeleted;


    @Column(name = "building_type")
    private String buildingType;


    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "buildingManagement")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<BuildingMapping> buildingMappings = new ArrayList<>();

    public BuildingManagement(Long buildingMgmtId, String buildingName, Integer mvnoId, String buildingType) {
        this.buildingMgmtId = buildingMgmtId;
        this.buildingName = buildingName;
        this.mvnoId = mvnoId;
        this.buildingType = buildingType;
    }

    public BuildingManagement(Long buildingMgmtId, String buildingName, Integer pincodeId, Integer areaId, Integer subAreaId, String buildingType,Integer mvnoId) {
        this.buildingMgmtId = buildingMgmtId;
        this.buildingName = buildingName;
        this.pincodeId = pincodeId;
        this.areaId = areaId;
        this.subAreaId = subAreaId;
        this.buildingType = buildingType;
        this.mvnoId = mvnoId;
//        this.buildingMappings = (List<BuildingMapping>) buildingMappings;
    }

    @Override
    public Long getPrimaryKey() {
        return this.buildingMgmtId;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    @Override
    public void setBuId(Long buId) {

    }
}
