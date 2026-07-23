package com.savbill.taskmanagement.core.modules.State.domian;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.modules.City.domain.City;
import com.savbill.taskmanagement.core.modules.Country.domain.Country;
import com.savbill.taskmanagement.core.modules.common.AuditableListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLMSTATE")
@EntityListeners(AuditableListener.class)
public class State extends Auditable {

	
	
	/*
	CREATE TABLE TBLMSTATE
  (
    STATEID   serial,
    NAME      VARCHAR(64) NOT NULL,
    COUNTRYID NUMERIC(20),
    STATUS    CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_MSTATE PRIMARY KEY (STATEID)
  );  
	 */

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

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "state")
    private List<City> cityList = new ArrayList<>();

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    public State() {
    }
//    public State(StatePojo state, Integer id) throws Exception {
//        CountryService stateService = SpringContext.getBean(CountryService.class);
//        this.id=state.getId();
//        this.name=state.getName();
//        this.status=state.getStatus();
//        this.country  =stateService.convertCountryPojoToCountryModel(state.getCountryPojo());;
//    }
}
