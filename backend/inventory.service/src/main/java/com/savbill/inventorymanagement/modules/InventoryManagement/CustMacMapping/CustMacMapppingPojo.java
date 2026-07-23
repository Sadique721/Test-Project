package com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.Customers.CustomersPojo;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
public class CustMacMapppingPojo extends Auditable implements IBaseDto {

    private Integer custid;

    private List<CustMacMappping> custMacMapppingList;

    private Integer id;

    private String macAddress;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private CustomersPojo customer;
    private Boolean isDeleted = false;

    @Override
    public Long getIdentityKey() {
        return null;
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
}
