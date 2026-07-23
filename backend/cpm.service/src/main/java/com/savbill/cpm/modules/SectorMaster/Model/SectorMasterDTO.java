package com.savbill.cpm.modules.SectorMaster.Model;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.model.common.Auditable;
import lombok.Data;

@Data
public class SectorMasterDTO extends Auditable implements IBaseDto {

    Long id;
    String sname;
    String status;
    Integer count;
    Integer ezyBillSectorId = 1;
    Boolean isDeleted = false;
    Integer mvnoId;
    Long buId;

    @Override
    public Long getIdentityKey() {
        return  id;
    }

    @Override
    public Integer getMvnoId() {
        // TODO Auto-generated method stub
        return mvnoId;
    }

    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }




}
