package com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.domain;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name="tbltproductwarehousemapping", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "warehouse_id"})
})
@EntityListeners(AuditableListener.class)
public class ProductWarehouseMapping extends Auditable implements IBaseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "threshold_qty", nullable = false)
    private Long thresholdQty;

    @Column(name = "mvno_id", nullable = false)
    private Long mvnoId;

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
