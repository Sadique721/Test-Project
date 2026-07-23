package com.savbill.commonGateway.moules.MasterManagement.Area.domain;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Domain.SubArea;
import com.savbill.commonGateway.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblmarea"
)
@EntityListeners(AuditableListener.class)
@NoArgsConstructor
public class Area extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "areaid")
    private Long id;

    private String name;
    private String status;
    private Boolean isDeleted = false;

    @Column(name = "COUNTRYID", nullable = false, length = 40)
    private Integer countryId;

    @Column(name = "CITYID", nullable = false, length = 40)
    private Integer cityId;

    @Column(name = "STATEID", nullable = false, length = 40)
    private Integer stateId;

    @JsonBackReference
    @ManyToOne()
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "pincodeid")
    private Pincode pincode;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "area")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<SubArea> areaList = new ArrayList<>();

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Transient
    private Long pincodeId;

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

    @Override
    public void setBuId(Long buId) {

    }

    public Area(Area area) {
        this.id = area.getId();
        this.name = area.getName();
        this.status = area.getStatus();
        this.isDeleted = area.getIsDeleted();
        this.countryId = area.getCountryId();
        this.cityId = area.getCityId();
        this.stateId = area.getStateId();
        this.mvnoId = area.getMvnoId();
    }
    public Area(Long area) {
    this.id=area;
    }

    public Area(Long id, String name, String status, Integer countryId, Integer cityId, Integer stateId, Long pincodeId, Integer mvnoId) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.countryId = countryId;
        this.cityId = cityId;
        this.stateId = stateId;
        this.pincodeId = pincodeId;
        this.mvnoId = mvnoId;
    }

    public Area(Long id, String name, Integer mvnoId) {
        this.id = id;
        this.name = name;
        this.mvnoId = mvnoId;
    }
    public Area(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Area(Long id, String name, Integer countryId, Integer cityId, Integer stateId, Long pincodeId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
        this.cityId = cityId;
        this.stateId = stateId;
        this.pincodeId = pincodeId;
    }

}
