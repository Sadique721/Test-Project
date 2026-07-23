package com.savbill.inventorymanagement.modules.MasterManagement.Area;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.MasterManagement.Pincode.Pincode;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmarea")
@EntityListeners(AuditableListener.class)
public class Area extends Auditable implements IBaseData<Long> {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "areaid")
    private Long id;

    private String name;
    private String status;
    private Boolean isDeleted = false;

    @Column(name = "countryid", nullable = false, length = 40)
    private Integer countryId;

    @Column(name = "cityid", nullable = false, length = 40)
    private Integer cityId;

    @Column(name = "stateid", nullable = false, length = 40)
    private Integer stateId;

    @JsonBackReference
    @ManyToOne()
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "pincodeid")
    private Pincode pincode;
//    @Column(name = "pincodeid", nullable = false, length = 40)
//    private Integer pincodeId;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

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
