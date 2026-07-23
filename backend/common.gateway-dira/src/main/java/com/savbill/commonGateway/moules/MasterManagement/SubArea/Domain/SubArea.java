package com.savbill.commonGateway.moules.MasterManagement.SubArea.Domain;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area;
import com.savbill.commonGateway.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmsubarea")
@EntityListeners(AuditableListener.class)
@NoArgsConstructor
public class SubArea extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subareaid")
    private Long id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "is_deleted" ,nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "countryid", nullable = false, length = 40)
    private Integer countryId;

    @Column(name = "cityid", nullable = false, length = 40)
    private Integer cityId;

    @Column(name = "stateid", nullable = false, length = 40)
    private Integer stateId;

    @Column(name = "mvnoid", nullable = false, length = 40)
    private Integer mvnoId;

    @Column(name = "buid")
    private Long buId;

    @JsonBackReference
    @ManyToOne()
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "areaid")
    private Area area;

    @Column(name = "filename")
    private String filename;

    @Column(name = "uniquename")
    private String uniquename;

    @Transient
    private Long areaId;

    public SubArea(Long id, String name, String status, Integer mvnoId) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.mvnoId = mvnoId;
    }

    public SubArea(Long id, String name, String status, Integer countryId, Integer cityId, Integer stateId, Long areaId, Integer mvnoId,String filename , String uniquename) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.countryId = countryId;
        this.cityId = cityId;
        this.stateId = stateId;
        this.areaId = areaId;
        this.mvnoId = mvnoId;
        this.filename = filename;
        this.uniquename = uniquename;
    }

    @Override
    public Long getPrimaryKey() {
        return this.id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

    @Override
    public void setBuId(Long buId) {
        this.buId = buId;
    }
}

