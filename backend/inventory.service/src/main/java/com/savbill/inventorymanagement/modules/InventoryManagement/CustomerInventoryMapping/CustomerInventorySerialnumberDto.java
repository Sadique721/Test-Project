package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import lombok.Data;

@Data
public class CustomerInventorySerialnumberDto implements IBaseDto,Cloneable {

    private Long id;

    private String productName;

    private Integer customerId;

    private String serialNumber;

    private Long itemId;

    private String connectionNo;

    private Long custInventoryMappingId;

    private String dtvCategory;

    private boolean isPrimary = false;


    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }

//    @Override
//    public Long getBuId() {
//        return null;
//    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
