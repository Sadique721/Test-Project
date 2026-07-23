package com.savbill.inventorymanagement.modules.ChargeManagement;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@ToString
@Table(name = "tblmcharges")
@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners(AuditableListener.class)
public class Charge extends Auditable implements IBaseData {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chargeid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "chargename", nullable = false, length = 40)
    private String name;

    @Column(name = "description", nullable = false, length = 40)
    private String desc;

    @Column(name = "chargetype", nullable = false, length = 40)
    private String chargetype;

    @Column(name = "price", nullable = false, length = 40)
    private double price;

    @Column(name = "actual_price", length = 40)
    private double actualprice;

//    @JoinColumn(name = "taxid")
//    @OneToOne(cascade = CascadeType.ALL)
//    private Tax tax;
    @Column(name = "taxid")
    private Integer taxId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    private String chargecategory;
    
    @Column(name = "MVNOID", length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", length = 40, updatable = false)
    private Long buId;

    @Column(name = "service", length = 40)
    private String service;

    @Column(name = "status",length = 40)
    private String status;

    @Column(name = "taxamount")
    private Double taxamount;

    @Column(name = "isinventorycharge")
    private Boolean isinventorycharge;

    @Transient
    private Long productId;
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
