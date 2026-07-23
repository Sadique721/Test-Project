package com.savbill.ticketmanagement.core.modules.Country.domain;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.modules.Country.dto.CountryPojo;
import com.savbill.ticketmanagement.core.modules.State.domian.State;
import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLMCOUNTRY")
@EntityListeners(AuditableListener.class)
public class Country extends Auditable {
	
	
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
    @Column(name = "COUNTRYID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    //@DiffIgnore
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "country")
    private List<State> stateList = new ArrayList<>();

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;


    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;
    public Country(CountryPojo pojo, Integer id) {
        this.id=pojo.getId();
        this.name=pojo.getName();
        this.isDelete=pojo.getIsDelete();
        this.status=pojo.getStatus();

    }

    public Country() {
    }

    public Country(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
