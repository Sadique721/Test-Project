package com.savbill.integrationsystem.NewNMSIntegration.dto;

import lombok.Data;

@Data
public class WifiConfigRequestDTO {
    private Long itemId;
    private Long customerId;
    private Long custInvenId;
    private String serialNumber;
}
