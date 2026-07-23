package com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.Domain.InvestmentCode;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tblmbusinessunit")
@EntityListeners(AuditableListener.class)
public class BusinessUnit extends Auditable implements IBaseData<Long> {
    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "businessunitid")
    private Long id;

    private String buname;

    private String bucode;

    private String status;

    @Column(name = "plan_binding_type",length = 50)
    private String planBindingType;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @ManyToMany(fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblmicnamebumapping",joinColumns = {@JoinColumn(name = "businessunitid")}, inverseJoinColumns = {@JoinColumn(name = "investmentcode_id")} )
    private List<InvestmentCode> investmentCodeid=new ArrayList<>();

//    @JsonManagedReference
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessUnit")
//    private List<PlanService> planServiceList = new ArrayList<>();
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


    public BusinessUnit(BusinessUnit businessUnit) {
        this.id = businessUnit.getId();
        this.buname = businessUnit.getBuname();
        this.bucode = businessUnit.getBucode();
        this.status = businessUnit.getStatus();
        this.planBindingType = businessUnit.getPlanBindingType();
        this.isDeleted = businessUnit.getIsDeleted();
        this.mvnoId = businessUnit.getMvnoId();
        this.investmentCodeid = businessUnit.getInvestmentCodeid();
    }
}
