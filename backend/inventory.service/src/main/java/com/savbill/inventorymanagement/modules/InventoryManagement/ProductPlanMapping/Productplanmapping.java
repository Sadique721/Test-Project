package com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltproductplanmapping")
@EntityListeners(AuditableListener.class)

public class Productplanmapping extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @Column(name = "plan_id",length = 40)
    private Long planId;

    @Column(name = "product_category_id",length = 40)
    private Long productCategoryId;

    @Column(name = "product_type",length = 40)
    private String product_type;

    @Column(name = "product_id",length = 40)
    private Long productId;

    @Column(name = "revised_charge",length = 40)
    private String revisedCharge;

    @Column(name = "ownershipType",length = 40)
    private String ownershipType;

    @Column(name="name")
    private String name;

    @Transient
    private String productCategoryName;
    @Transient
    private String productName;
    @Transient
    private String planName;
    
    @Column(name = "product_quantity")
    private Integer productQuantity;

    @Override
    public Long getPrimaryKey() {
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
