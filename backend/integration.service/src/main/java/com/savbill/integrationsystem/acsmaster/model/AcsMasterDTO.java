package com.savbill.integrationsystem.acsmaster.model;

import com.savbill.integrationsystem.acsmaster.entity.AcsMasterAPIMapping;
import com.savbill.integrationsystem.acsmaster.entity.AcsMasterUrlParamMapping;
import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AcsMasterDTO extends Auditable<Long> implements IBaseDto {
    private Long id;
    private String name;
    private String url;
    private String username;
    private String password;
    private Long mvnoId;
    List<AcsMasterUrlParamMapping> acsMasterUrlParamMappingList;
    List<AcsMasterAPIMapping> acsMasterAPIMappings;
    private Long vendorId;

    private Boolean isdelete =false;


    @Override
    public Long getIdentityKey() {
        return id;
    }


}
