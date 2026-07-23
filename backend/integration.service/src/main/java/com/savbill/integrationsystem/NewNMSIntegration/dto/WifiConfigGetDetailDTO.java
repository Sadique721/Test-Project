package com.savbill.integrationsystem.NewNMSIntegration.dto;

import lombok.Data;

@Data
public class WifiConfigGetDetailDTO {
    private Long itemId;
    private Long customerId;
    private Long custInvenId;
    private String serialNumber;
    private String ssidUsername;
    private String ssidPassword;
    private String workingFrequency;
}
