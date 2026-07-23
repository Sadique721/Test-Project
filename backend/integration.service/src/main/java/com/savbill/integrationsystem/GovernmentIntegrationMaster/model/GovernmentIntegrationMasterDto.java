package com.savbill.integrationsystem.GovernmentIntegrationMaster.model;

import com.savbill.integrationsystem.GovernmentIntegrationMaster.entity.GovernmentAPIMappings;
import com.savbill.integrationsystem.core.dto.IBaseDto;
import lombok.Data;

import java.util.List;

@Data
public class GovernmentIntegrationMasterDto implements IBaseDto {

    private Long id;
    private String username;
    private String password;
    private Long mvnoId;
    //private Boolean isdelete;
    private Boolean isdelete = false;
    List<GovernmentAPIMappings> governmentAPIMappings;
    private String status;

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
