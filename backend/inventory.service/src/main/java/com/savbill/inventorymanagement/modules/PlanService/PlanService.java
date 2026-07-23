package com.savbill.inventorymanagement.modules.PlanService;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "tblmservices")
@EntityListeners(AuditableListener.class)
public class PlanService extends Auditable<Integer> implements IBaseData {
	
	
	/*
CREATE TABLE TBLMSERVICES
(
	serviceid SERIAL PRIMARY KEY,
	servicename varchar(255),
	CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);  
	 */

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "servicename", nullable = false, length = 40)
    private String name;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "is_dtv")
    private Boolean is_dtv;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltserviceinventorymapping",
            joinColumns = @JoinColumn(name = "serviceid", referencedColumnName = "serviceid"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "product_id"))
    private List<ProductCategory> productCategories = new ArrayList<>();

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}