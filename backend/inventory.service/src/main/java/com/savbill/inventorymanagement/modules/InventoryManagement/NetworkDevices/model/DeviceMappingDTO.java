package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model;

import lombok.Data;

import java.util.Set;

@Data
public class DeviceMappingDTO {

    private Long deviceId;
    private Set<Long> inPortDevices;
    private Set<Long> outPortDevices;

}
