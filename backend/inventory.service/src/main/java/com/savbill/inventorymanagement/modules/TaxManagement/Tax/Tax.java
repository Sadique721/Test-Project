package com.savbill.inventorymanagement.modules.TaxManagement.Tax;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.TaxManagement.TaxSlab.TaxTypeSlab;
import com.savbill.inventorymanagement.modules.TaxManagement.TaxTier.TaxTypeTier;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblmtax")
@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners(AuditableListener.class)
public class Tax extends Auditable implements IBaseData {
	
	
	/*
	 CREATE TABLE TBLMTAX
	  (
	    TAXID                 serial,
	    NAME                  VARCHAR(64) NOT NULL,
	    DESCRIPTION           VARCHAR(255),
	    TAXTYPE               VARCHAR(8),
	    STATUS                CHAR(1) DEFAULT 'Y' NOT NULL,
		    CREATEDATE            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
		    CREATEDBYSTAFFID      NUMERIC(20),
		    LASTMODIFIEDBYSTAFFID NUMERIC(20),
		    LASTMODIFIEDDATE      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
		    MVNOID                bigint UNSIGNED,
	    PRIMARY KEY (TAXID),
	    FOREIGN KEY (MVNOID) REFERENCES TBLMMVNO (MVNOID)
	  );
	 */

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taxid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "description", nullable = false, length = 150)
    private String desc;

    @Column(name = "taxtype", nullable = false, length = 40)
    private String taxtype;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxid", cascade = CascadeType.ALL)
    @OrderBy("id asc")
    @ToString.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TaxTypeTier> tieredList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxid", cascade = CascadeType.ALL)
    @OrderBy("id asc")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TaxTypeSlab> slabList = new ArrayList<>();

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;


    public Tax() {
    }

    public Tax(Integer id) {
        this.id = id;
    }


    public Tax(String name, String status) {
        super();
        this.name = name;
        this.status = status;
    }

    @Override
    public Serializable getPrimaryKey() {
        return id;
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
