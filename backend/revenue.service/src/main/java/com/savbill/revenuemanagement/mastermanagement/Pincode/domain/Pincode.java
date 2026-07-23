package com.savbill.revenuemanagement.mastermanagement.Pincode.domain;


import com.savbill.revenuemanagement.core.data.IBaseData;
import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.savbill.revenuemanagement.mastermanagement.Area.domain.Area;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblmpincode")
@EntityListeners(AuditableListener.class)

public class Pincode extends Auditable implements IBaseData<Long> {
    @Id
    @Column(name = "pincodeid", nullable = false, length = 40)
    private Long id;

    private String pincode;
    private String status;
    private Boolean isDeleted = false;

    @Column(name = "COUNTRYID", nullable = false, length = 40)
    private Integer countryId;

    @Column(name = "CITYID", nullable = false, length = 40)
    private Integer cityId;

    @Column(name = "STATEID", nullable = false, length = 40)
    private Integer stateId;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "pincode")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Area> areaList = new ArrayList<>();
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

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
        return this.isDeleted;
    }
}
