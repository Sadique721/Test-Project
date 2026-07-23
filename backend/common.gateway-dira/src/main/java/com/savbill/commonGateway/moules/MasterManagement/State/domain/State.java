package com.savbill.commonGateway.moules.MasterManagement.State.domain;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
import com.savbill.commonGateway.moules.MasterManagement.Country.service.CountryService;
import com.savbill.commonGateway.moules.MasterManagement.State.model.StatePojo;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "TBLMSTATE")
@EntityListeners(AuditableListener.class)
@NoArgsConstructor
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
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

//    @JsonManagedReference
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "state")
//    private List<City> cityList = new ArrayList<>();

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    public State(StatePojo state, Integer id) throws Exception {
        CountryService stateService = SpringContext.getBean(CountryService.class);
        this.id=state.getId();
        this.name=state.getName();
        this.status=state.getStatus();
        this.country  =stateService.convertCountryPojoToCountryModel(state.getCountryPojo());;
    }

    public State(State state) {
        this.id = state.getId();
        this.name = state.getName();
        this.status = state.getStatus();
        if(state.getCountry() != null)
            this.country = new Country(state.getCountry());
        this.isDeleted = state.getIsDeleted();
        this.mvnoId = state.getMvnoId();
    }
}
