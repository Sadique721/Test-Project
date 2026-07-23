package com.savbill.ticketmanagement.core.modules.Area.domain;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.data.IBaseData;
import com.savbill.ticketmanagement.core.modules.Pincode.domain.Pincode;
import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
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
