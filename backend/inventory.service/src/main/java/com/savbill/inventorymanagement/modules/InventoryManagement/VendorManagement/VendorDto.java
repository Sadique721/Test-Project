package com.savbill.inventorymanagement.modules.InventoryManagement.VendorManagement;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import lombok.Data;

@Data
public class VendorDto implements IBaseDto {

    private Long id;
    private String name;
    private String status;

    private Integer mvnoId;

    private boolean isDeleted;


    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

//    @Override
//    public Long getBuId() {
//        return null;
//    }

}
