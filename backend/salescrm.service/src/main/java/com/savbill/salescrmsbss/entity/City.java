package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.SaveCitySharedDataMessage;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLMCITY")
public class City {

	@Id
    @Column(name = "CITYID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "COUNTRYID", nullable = false, length = 40)
    private Integer countryId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "STATEID")
    @ToString.Exclude
    private State state;

    @Column(name = "is_delete",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    
    public City(SaveCitySharedDataMessage message) {
    	this.id = message.getId();
    	this.countryId = message.getCountryId();
    	this.isDelete = message.getIsDelete();
    	this.mvnoId = message.getMvnoId();
    	this.name = message.getName();
    	this.status = message.getStatus();
    	this.state = message.getState();
    }
}
