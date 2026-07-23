package com.savbill.cpm.modules.InventoryManagement.RequestInventory;

import com.savbill.cpm.core.dto.IBaseDto;
import lombok.Data;

@Data
public class CommonResponceDto implements IBaseDto {

    private Long id;
    private String name;

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
