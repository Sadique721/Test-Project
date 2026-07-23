package com.savbill.taskmanagement.core.modules.Pincode.domain;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.data.IBaseData;
import com.savbill.taskmanagement.core.modules.Area.domain.Area;
import com.savbill.taskmanagement.core.modules.common.AuditableListener;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblmpincode")
@EntityListeners(AuditableListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class Pincode extends Auditable implements IBaseData<Long> {
    @Id
    @Column(name = "pincodeid", nullable = false, length = 40)
    private Long id;

    @JsonCreator
    public Pincode(String pincode) {
        this.pincode = pincode;
    }

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
