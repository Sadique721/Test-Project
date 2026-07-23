package com.savbill.integrationsystem.billgen.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@ToString
@Table(name = "tblmbranch")
public class Branch {
    @Id
    @Column(name = "branchid")
    private Long id;
    private String name;
    private String status;

    @Column(name = "branch_code", length = 40)
    private String branch_code;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    public Branch(){}

    public Branch(SaveBranchSharedDataMessage message){
        this.id=message.getId();
        this.name=message.getName();
        this.status=message.getStatus();
        this.branch_code= message.getBranch_code();
        this.isDeleted=message.getIsDeleted();
        this.mvnoId=message.getMvnoId();

    }
}