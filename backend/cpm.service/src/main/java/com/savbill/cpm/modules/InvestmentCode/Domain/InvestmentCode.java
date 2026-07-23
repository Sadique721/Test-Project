package com.savbill.cpm.modules.InvestmentCode.Domain;

import com.savbill.cpm.core.data.IBaseData;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.spring.security.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltinvestmentcode")
@EntityListeners(AuditableListener.class)
public class InvestmentCode extends Auditable implements IBaseData<Long> {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investmentcode_id", nullable = false, length = 40)
    private Long id;

    @Column(name = "iccode")
    private String iccode;

    @Column(name = "icname")
    private String icname;


    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

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
