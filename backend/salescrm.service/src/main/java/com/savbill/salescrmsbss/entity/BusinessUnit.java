package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.BusinessUnitMessage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblmbusinessunit")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessUnit {

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
    
    public BusinessUnit(BusinessUnitMessage businessUnitMessage) {
    	this.id = businessUnitMessage.getId();
    	this.buname = businessUnitMessage.getBuname();
    	this.bucode = businessUnitMessage.getBucode();
    	this.status = businessUnitMessage.getStatus();
    	this.isDeleted = businessUnitMessage.getIsDeleted();
    	this.mvnoId = businessUnitMessage.getMvnoId();
        if(businessUnitMessage.getPlanBindingType()!= null)
            this.planBindingType = businessUnitMessage.getPlanBindingType();
    }
}
