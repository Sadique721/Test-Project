package com.savbill.cpm.modules.Matrix.model;

import com.savbill.cpm.core.dto.IBaseDto2;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.modules.Matrix.domain.MatrixDetails;
import lombok.Data;

import java.util.List;

@Data
public class MatrixDTO extends Auditable implements IBaseDto2 {

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
     Long rtime;
     String runit;

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
