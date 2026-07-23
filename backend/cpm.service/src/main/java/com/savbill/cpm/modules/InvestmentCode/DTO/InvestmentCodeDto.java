package com.savbill.cpm.modules.InvestmentCode.DTO;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InvestmentCodeDto extends Auditable implements IBaseDto {

    private Long id;

    private String iccode;

    private String icname;

    private Boolean isDeleted = false;

    private Integer mvnoId;
    @NotNull
    private String status;

    @JsonIgnore
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
