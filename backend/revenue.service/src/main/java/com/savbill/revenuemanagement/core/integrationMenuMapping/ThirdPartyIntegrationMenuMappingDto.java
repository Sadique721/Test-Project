package com.savbill.revenuemanagement.core.integrationMenuMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdPartyIntegrationMenuMappingDto {
    private Long id;
    private Long thirdPartyMenuId;
    private String thirdPartyParameterName;
    private String thirdPartyParameterValue;
    private String thirdPartyParamDesc;

}
