package com.savbill.taskmanagement.core.modules.TicketTatMatrix.Model;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrixMapping;
import lombok.Data;

import java.util.List;

@Data
public class TicketTatMatrixDTO extends Auditable implements IBaseDto {

    Long id;
    String name;
    String status;
    Boolean isDeleted = false;
    Integer mvnoId;
    Long buId;
    Long slaTimep1;
    Long slaTimep2;
    Long slaTime3;
    String sunitp1;
    String sunitp2;
    String sunitp3;
    Long rtime;
    String runit;

    List<TicketTatMatrixMapping> tatMatrixMappings;
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
