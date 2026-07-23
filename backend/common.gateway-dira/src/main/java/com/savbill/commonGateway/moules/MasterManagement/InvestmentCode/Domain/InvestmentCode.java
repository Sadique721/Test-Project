package com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.Domain;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data

@Entity
@Table(name = "tbltinvestmentcode")
@EntityListeners(AuditableListener.class)
public class InvestmentCode extends Auditable implements IBaseData<Long> {

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investmentcode_id", nullable = false, length = 40)
    private Long id;

    @Column(name = "iccode")
    private String iccode;

    @Column(name = "icname")
    private String icname;


    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    public InvestmentCode() {

    }


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
    public InvestmentCode(InvestmentCode investmentCode){
        this.id=investmentCode.getId();
        this.iccode=investmentCode.getIccode();
        this.icname=investmentCode.getIcname();
        this.isDeleted=investmentCode.getIsDeleted();
        this.mvnoId=investmentCode.getMvnoId();
        this.status=investmentCode.getStatus();


    }
}
