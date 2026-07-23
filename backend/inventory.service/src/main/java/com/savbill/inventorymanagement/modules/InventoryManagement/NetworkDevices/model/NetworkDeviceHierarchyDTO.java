package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model;

import lombok.Data;

@Data
public class NetworkDeviceHierarchyDTO {
    private Long parentDeviceId;
    private Long childDeviceId;
    private String parentDeviceName;
    private String childDeviceName;
    private String parentDevicePortNumber;
    private String childDevicePortNumber;
    private String parentDeviceType;
    private String childDeviceType;
    private String parentDeviceOwnerType;
    private String childDeviceOwnerType;
    private String parentDeviceMacAddress;
    private String childDeviceMacAddress;
    private String parentDeviceSerialNumber;
    private String childDeviceSerialNumber;
    private String parentOwnerName;
    private String childOwnerName;
    private String parentDevicePortType;
    private String childDevicePortType;
}