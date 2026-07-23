package com.savbill.integrationsystem.isp;

import lombok.Data;

@Data
public class ProratedPayload {
    private Integer quantity;
    private Double unitPrice;
}
