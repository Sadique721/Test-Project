package com.savbill.revenuemanagement.mastermanagement.City.domain;

import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.savbill.revenuemanagement.mastermanagement.State.domian.State;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "TBLMCITY")
@EntityListeners(AuditableListener.class)
public class City extends Auditable {
	
	
	/*
	CREATE TABLE TBLMCITY
  (
    CITYID  serial,
    NAME    VARCHAR(64) NOT NULL,
    STATEID NUMERIC(20),
    STATUS  CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_MCITY PRIMARY KEY (CITYID)
  );
	 */

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

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;


    public City() {
    }
    public City(City pojo, Integer id) {
        this.id=pojo.getId();
        this.name=pojo.getName();
        this.status=pojo.getStatus();
        this.state= pojo.getState();
        this.countryId=pojo.getCountryId();
    }

    public City(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
