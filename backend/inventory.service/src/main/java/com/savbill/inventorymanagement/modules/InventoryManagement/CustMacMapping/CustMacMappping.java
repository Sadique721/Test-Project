package com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = " tbltcustmacmapping")
@EntityListeners(AuditableListener.class)
public class CustMacMappping extends Auditable implements IBaseData<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custmacmapid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "macaddress", length = 100)
    private String macAddress;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "custid")
    private Customers customer;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    @Transient
    private  Long custServicemappingId;

    @Override
    public Integer getPrimaryKey() {
        return this.id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

    public CustMacMappping() {
    }

    public CustMacMappping(CustMacMappping custMacMappping) {
        this.id = custMacMappping.getId();
        this.macAddress = custMacMappping.getMacAddress();
        this.customer = custMacMappping.getCustomer();
        this.isDeleted = custMacMappping.getIsDeleted();
    }
}
