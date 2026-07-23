package com.savbill.commonGateway.moules.MasterManagement.City.domain;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import com.savbill.commonGateway.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

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
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Transient
    private  String stateName;

    public City() {
    }
    public City(City pojo, Integer id) {
        this.id=pojo.getId();
        this.name=pojo.getName();
        this.status=pojo.getStatus();
        this.state= pojo.getState();
        this.countryId=pojo.getCountryId();
    }
}
