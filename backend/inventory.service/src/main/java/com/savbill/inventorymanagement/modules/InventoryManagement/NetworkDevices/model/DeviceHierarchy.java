package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceHierarchy {
    String type;
    Boolean expanded;
    String styleClass;
    GraphData data;
    List<DeviceHierarchy> children;
}

