package com.savbill.ticketmanagement.core.modules.BusinessUnit.domain;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.data.IBaseData;
import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmbusinessunit")
@EntityListeners(AuditableListener.class)
public class BusinessUnit extends Auditable implements IBaseData<Long> {
    @Id
    @Column(name = "businessunitid")
    private Long id;

    private String buname;

    private String bucode;

    private String status;

    @Column(name = "plan_binding_type",length = 50)
    private String planBindingType;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    public BusinessUnit() {

    }

//    @ManyToMany(fetch = FetchType.LAZY)
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @JoinTable(name = "tblmicnamebumapping",joinColumns = {@JoinColumn(name = "businessunitid")}, inverseJoinColumns = {@JoinColumn(name = "investmentcode_id")} )
//    private List<InvestmentCode> investmentCodeid=new ArrayList<>();


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

    public BusinessUnit(Long id, String buname) {
        this.id = id;
        this.buname = buname;
    }
}
