package com.savbill.integrationsystem.integrationMenu;


import com.savbill.integrationsystem.core.dto.IBaseDto;
import com.savbill.integrationsystem.integrationMenuMapping.ThirdPartyIntegrationMenuMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdPartyIntegrationMenuDto implements IBaseDto {
    private Long id;
    private String name;
    private String eventName;
    private String clientName;
    private List<ThirdPartyIntegrationMenuMapping> thirdPartyIntegrationMenuMappings;
    private String status;
    private boolean isDelete = false;
    private Long mvnoId;

    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public void setMvnoId(Long mvnoId) {
        this.mvnoId = mvnoId;
    }
}
