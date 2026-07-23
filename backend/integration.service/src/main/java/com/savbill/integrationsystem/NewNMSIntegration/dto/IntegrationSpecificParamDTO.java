package com.savbill.integrationsystem.NewNMSIntegration.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationSpecificParamDTO {
    private String paramName;
    private String paramValue;
}
