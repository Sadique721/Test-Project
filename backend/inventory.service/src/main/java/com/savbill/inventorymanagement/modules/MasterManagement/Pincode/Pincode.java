package com.savbill.inventorymanagement.modules.MasterManagement.Pincode;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmpincode")
@EntityListeners(AuditableListener.class)

public class Pincode extends Auditable implements IBaseData<Long> {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pincodeid", nullable = false, length = 40)
    private Long id;

    private String pincode;
    private String status;
    private Boolean isDeleted = false;

    @Column(name = "countryid", nullable = false, length = 40)
    private Integer countryId;

    @Column(name = "cityid", nullable = false, length = 40)
    private Integer cityId;

    @Column(name = "stateid", nullable = false, length = 40)
    private Integer stateId;

//    @JsonManagedReference
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "pincode")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @LazyCollection(LazyCollectionOption.FALSE)
//    private List<Area> areaList = new ArrayList<>();

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
