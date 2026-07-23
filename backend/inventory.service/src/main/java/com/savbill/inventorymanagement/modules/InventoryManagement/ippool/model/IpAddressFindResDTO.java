package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model;

import lombok.Data;

@Data
public class IpAddressFindResDTO {
    private Long poolDetailsId;
    private String ipAddress;
    private String poolName;
    private String displayName;
    private String poolType;
    private String ipStatus;
    private Long ipAllocated;
    private String customerName;
    private String startDate;
    private String endDate;
    private Boolean isStaticIp;
}
