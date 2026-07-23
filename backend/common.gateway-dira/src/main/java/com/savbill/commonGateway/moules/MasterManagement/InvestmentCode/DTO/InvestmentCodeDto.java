package com.savbill.commonGateway.moules.MasterManagement.InvestmentCode.DTO;

import com.savbill.commonGateway.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class InvestmentCodeDto implements IBaseDto {

    private Long id;

    private String iccode;

    private String icname;

    private Boolean isDeleted = false;

    private Integer mvnoId;
    @NotNull
    private String status;
    private String createdByName;

    private String lastModifiedByName;

    private Integer createdById;
    private Integer lastModifiedById;
    private LocalDateTime createdate;

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
