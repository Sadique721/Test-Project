package com.savbill.radius.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.radius.helper.ConcurrentPolicyDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "TBLMCONCURRENTPOLICY")
@NoArgsConstructor
public class ConcurrentPolicy {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concurrentpolicyid")
    private Long concurrentPolicyId;
	
    @Column(name = "name")
    private String name;

    @Column(name = "noofconcurrentconnections")
    private Long noOfConcurrentConnections;

    @Column(name = "status")
    private String status;

    @ApiModelProperty(hidden = true)
    @Column (name="mvnoid", nullable = false)
    private Integer mvnoId;

    public Integer getMvnoId() {
        return mvnoId;
    }

    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }

    public ConcurrentPolicy(ConcurrentPolicyDto concurrentPolicyDto) {
		this.name = concurrentPolicyDto.getName();
		this.noOfConcurrentConnections = concurrentPolicyDto.getNoOfConcurrentConnections();
		this.status = concurrentPolicyDto.getStatus();
	}




}
