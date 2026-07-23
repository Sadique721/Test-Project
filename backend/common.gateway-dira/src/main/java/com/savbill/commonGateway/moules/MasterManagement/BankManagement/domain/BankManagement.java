package com.savbill.commonGateway.moules.MasterManagement.BankManagement.domain;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmbankmanagement")
@EntityListeners(AuditableListener.class)
public class BankManagement extends Auditable implements IBaseData<Long> {

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bankid")
    private Long id;

    private String bankname;

    private String accountnum;

    private String ifsccode;

    private String bankholdername;

    private String status;

    @Column(name = "bank_code")
    private String bankcode;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    private String banktype;


    @Override
    public Long getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    public BankManagement() {
    }

    @Override
    public void setBuId(Long buId) {

    }
    public BankManagement(BankManagement bankManagement){
        this.accountnum= bankManagement.getAccountnum();
        this.id = bankManagement.id;
        this.bankname = bankManagement.getBankname();
        this.bankcode = bankManagement.getBankcode();
        this.bankholdername = bankManagement.getBankholdername();
        this.isDeleted = bankManagement.getIsDeleted();
        this.ifsccode = bankManagement.getIfsccode();
        this.status = bankManagement.getStatus();
        this.mvnoId = bankManagement.getMvnoId();
        this.banktype = bankManagement.getBanktype();
        this.accountnum = bankManagement.getAccountnum();
    }

}
