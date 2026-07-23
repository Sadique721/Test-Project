package com.savbill.inventorymanagement.modules.MasterManagement.City;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tblmcity")
@EntityListeners(AuditableListener.class)
public class City extends Auditable implements IBaseData<Long> {
	
	
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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cityid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "countryid", nullable = false, length = 40)
    private Integer countryId;

//    @ManyToOne
//    @JsonBackReference
//    @JoinColumn(name = "stateid")
//    @ToString.Exclude
//    private State state;
    @Column(name = "stateid", nullable = false, length = 40)
    private Integer stateId;

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
        this.stateId= pojo.getStateId();
        this.countryId=pojo.getCountryId();
    }

    @Override
    public Long getPrimaryKey() {
        return Long.valueOf(id);
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDelete;
    }
}
