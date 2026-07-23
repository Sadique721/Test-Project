package com.savbill.integrationsystem.rms.model;

import com.savbill.integrationsystem.core.dto.IBaseDto;
import lombok.Data;

@Data
public class VendorDto implements IBaseDto {

    private Long id;
    private String name;
    private String status;

    private Long mvnoId;

    private boolean isDeleted;


    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Long getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Long mvnoId) {
        this.mvnoId = mvnoId;
    }

}
