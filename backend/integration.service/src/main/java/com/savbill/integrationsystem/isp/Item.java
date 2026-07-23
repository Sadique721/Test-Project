package com.savbill.integrationsystem.isp;

import lombok.Data;

import java.util.List;

@Data
public class Item {
    private String bandwidth;
    private Integer quantity;
    private Double unitPrice;
    private List<ProratedPayload> prorated;
}
