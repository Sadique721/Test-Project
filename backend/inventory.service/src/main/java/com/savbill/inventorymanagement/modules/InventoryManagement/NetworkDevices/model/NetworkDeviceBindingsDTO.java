package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model;

import lombok.Data;

@Data
public class NetworkDeviceBindingsDTO {

    private Long id;
    private Long deviceId;
    private String deviceName;
    private Long parentDeviceId;
    private String parentDeviceName;
    private String portType;
    private String inBind;
    private String outBind;

}
