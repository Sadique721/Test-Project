package com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.model;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductWarehouseMappingDTO implements IBaseDto {
    private Long id;
    private Long productId;
    private Long warehouseId;
    private Long thresholdQty;
    private Long mvnoId;
    private String unit;

    @Override
    public Long getIdentityKey() {
        return 0L;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId.intValue();
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId=mvnoId.longValue();
    }
}
