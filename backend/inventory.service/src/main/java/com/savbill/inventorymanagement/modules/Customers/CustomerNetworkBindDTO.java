package com.savbill.inventorymanagement.modules.Customers;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import lombok.Data;

@Data
public class CustomerNetworkBindDTO extends Auditable implements IBaseDto {
    private Long id;
    private Long customerid;
    private Long popid;
    private Long oltid;
    private Long dnsplitterid;
    private Long snsplitterid;
    private Long masterdbid;

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
