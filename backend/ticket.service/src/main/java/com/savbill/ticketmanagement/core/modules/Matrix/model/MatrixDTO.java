package com.savbill.ticketmanagement.core.modules.Matrix.model;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.dto.IBaseDto;
import com.savbill.ticketmanagement.core.modules.Matrix.domain.MatrixDetails;
import lombok.Data;

import java.util.List;

@Data
public class MatrixDTO extends Auditable implements IBaseDto {

     Long id;
     String name;
     String status;
     Boolean isDeleted = false;
     Integer mvnoId;
     Long buId;
     Long slaTime;
     String slaUnit;
     List<MatrixDetails> matrixDetailsList;
     Integer lcoId;

    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Integer getMvnoId() {
        return this.mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
