package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model;

import lombok.Data;

@Data
public class IPDetailsDTO {
    private String ipRange;
    private String netMask;
    private String networkIp;
    private String broadcastIp;
    private String firstHost;
    private String lastHost;
    private Integer totalHost;
}
