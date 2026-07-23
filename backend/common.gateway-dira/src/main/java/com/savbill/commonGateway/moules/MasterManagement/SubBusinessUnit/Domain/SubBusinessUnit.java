package com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Domain;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmsubbusinessunit")
@EntityListeners(AuditableListener.class)
public class SubBusinessUnit extends Auditable implements IBaseData<Long> {


    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_bu_id", nullable = false, length = 40)
    private Long id;

    public SubBusinessUnit() {
    }

    @Column(name = "subbuname")
    private String subbuname;

    @Column(name = "subbucode")
    private String subbucode;

    @Column(name = "businessunitid")
    private Long businessunitid;

    private Boolean isDeleted = false;

    @DiffIgnore
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

    @Override
    public void setBuId(Long buId) {

    }

    public SubBusinessUnit (SubBusinessUnit subBusinessUnit){
        this.id = subBusinessUnit.getId();
        this.isDeleted = subBusinessUnit.getIsDeleted();
        this.status = subBusinessUnit.getStatus();
        this.mvnoId = subBusinessUnit.getMvnoId();
        this.subbucode = subBusinessUnit.getSubbucode();
        this.subbuname = subBusinessUnit.getSubbuname();
    }
}
