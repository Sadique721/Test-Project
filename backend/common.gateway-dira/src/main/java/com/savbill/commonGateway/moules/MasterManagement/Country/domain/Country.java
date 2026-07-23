package com.savbill.commonGateway.moules.MasterManagement.Country.domain;


import com.savbill.commonGateway.common.domain.Auditable2;
import com.savbill.commonGateway.moules.MasterManagement.Country.model.CountryPojo;
import com.savbill.commonGateway.spring.security.AuditableListener2;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;
//import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLMCOUNTRY")
@EntityListeners(AuditableListener2.class)
@NoArgsConstructor
public class Country extends Auditable2 {
	
	
	/*
	 CREATE TABLE TBLMCOUNTRY
  (
    COUNTRYID serial,
    NAME      VARCHAR(64) NOT NULL,
    STATUS    CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_MCOUNTRY PRIMARY KEY (COUNTRYID)
  );
  
	 */

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COUNTRYID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

//    @DiffIgnore
//    @JsonManagedReference
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "country")
//    private List<State> stateList = new ArrayList<>();

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    public Country(CountryPojo pojo, Integer id) {
        this.id=pojo.getId();
        this.name=pojo.getName();
        this.isDelete=pojo.getIsDelete();
        this.status=pojo.getStatus();

    }

    public Country(Country country) {
        this.id = country.getId();
        this.name = country.getName();
        this.status = country.getStatus();
        this.isDelete = country.getIsDelete();
    }

    //    @Column(name = "MVNOID", nullable = false, length = 40)
//    private Integer mvnoId;
}
