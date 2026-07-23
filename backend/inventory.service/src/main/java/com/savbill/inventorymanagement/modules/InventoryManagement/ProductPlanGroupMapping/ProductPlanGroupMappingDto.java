package com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import lombok.Data;

@Data
public class ProductPlanGroupMappingDto implements IBaseDto {
    private Long id;
    private Long planGroupId;
    private Long productCategoryId;
    private String product_type;
    private Long productId;
    private String revisedCharge;
    private String ownershipType;
    private String name;
    private String productName;
    private String productCategoryName;
    private String planName;
    private Long planId;
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }
}
