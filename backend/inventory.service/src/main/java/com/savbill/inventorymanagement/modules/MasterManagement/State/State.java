package com.savbill.inventorymanagement.modules.MasterManagement.State;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.MasterManagement.Country.Country;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tblmstate")
@EntityListeners(AuditableListener.class)
public class State extends Auditable implements IBaseData<Long> {

	
	
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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stateid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "COUNTRYID")
    @ToString.Exclude
    private Country country;
//
//    @JsonManagedReference
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "state")
//    private List<City> cityList = new ArrayList<>();
//    @Column(name = "countryid")
//    private Long countryId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    public State() {
    }

    @Override
    public Long getPrimaryKey() {
        return Long.valueOf(id);
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }
//    public State(StatePojo state, Integer id) throws Exception {
//        CountryService stateService = SpringContext.getBean(CountryService.class);
//        this.id=state.getId();
//        this.name=state.getName();
//        this.status=state.getStatus();
//        this.country  =stateService.convertCountryPojoToCountryModel(state.getCountryPojo());;
//    }
}
