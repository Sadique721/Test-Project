package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.SaveStateSharedDataMessage;
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
@Table(name = "TBLMSTATE")
public class State {

    @Id
    @Column(name = "STATEID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "COUNTRYID")
    @ToString.Exclude
    private Country country;

    @Column( name = "is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    
    public State(SaveStateSharedDataMessage stateMessage) {
    	this.id = stateMessage.getId();
    	this.name = stateMessage.getName();
    	this.status = stateMessage.getStatus();
    	this.mvnoId = stateMessage.getMvnoId();
    	this.country = stateMessage.getCountry();
    	this.isDeleted = stateMessage.getIsDeleted();
    }

}
