package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.BranchMessage;

import com.savbill.salescrmsbss.rabbitMq.message.UpdateBranchSharedData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblmbranch")
public class Branch {

	@Id
    @Column(name = "branchid")
    private Long id;

    private String name;

    private String status;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    public Branch(BranchMessage message) {
    	this.id = message.getId();
    	this.name = message.getName();
    	this.status = message.getStatus();
    	this.isDeleted = message.getIsDeleted();
        this.mvnoId = message.getMvnoId();
    }
    public Branch(UpdateBranchSharedData message) {
        this.id = message.getId();
        this.name = message.getName();
        this.status = message.getStatus();
        this.isDeleted = message.getIsDeleted();
        this.mvnoId = message.getMvnoId();
    }
    public Branch(Long id) {
    	this.id = id;
    }
}
