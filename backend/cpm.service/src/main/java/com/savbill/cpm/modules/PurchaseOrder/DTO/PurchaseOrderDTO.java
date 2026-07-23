package com.savbill.cpm.modules.PurchaseOrder.DTO;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.model.common.Customers;
import lombok.Data;

@Data
public class PurchaseOrderDTO  extends Auditable implements IBaseDto {

    private Long id;
    private String ponumber;
    private Customers custid;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Long buid;
    private String filename;
    private String uniquename;



    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
