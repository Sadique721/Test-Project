package com.savbill.cpm.modules.SubBusinessUnit.Domain;

import com.savbill.cpm.core.data.IBaseData;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.spring.security.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltsubbusinessunit")
@EntityListeners(AuditableListener.class)
public class SubBusinessUnit extends Auditable implements IBaseData<Long> {


    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_bu_id", nullable = false, length = 40)
    private Long id;

    @Column(name = "subbuname")
    private String subbuname;

    @Column(name = "subbucode")
    private String subbucode;

    @Column(name = "businessunitid")
    private Long businessunitid;

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
