package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model;

import lombok.Data;

@Data
public class NetworkDevicePortsBinding {

    private Long parentDeviceId;
    private String inBind;
    private String outBind;

}
