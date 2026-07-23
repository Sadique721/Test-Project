package com.savbill.revenuemanagement.core.integrationMenu;



import com.savbill.revenuemanagement.core.integrationMenuMapping.ThirdPartyIntegrationMenuMapping;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThirdPartyIntegrationMenuDto{
    private Long id;
    private String name;
    private String eventName;
    private String clientName;
    private List<ThirdPartyIntegrationMenuMapping> thirdPartyIntegrationMenuMappings;
    private String status;
    private boolean isDelete = false;
    private Long mvnoId;

}
