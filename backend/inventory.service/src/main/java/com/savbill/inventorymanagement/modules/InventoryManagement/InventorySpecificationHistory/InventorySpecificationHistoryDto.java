package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecificationHistory;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import lombok.Data;

@Data
public class InventorySpecificationHistoryDto implements IBaseDto {

    private Long id;
    private Long itemId;
    private Long paramId;
    private String paramValue;
    private Boolean isMandatory;
    private Long invenId;
    private String status;

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
