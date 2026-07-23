package com.savbill.inventorymanagement.modules.InventoryManagement.Inward;

import lombok.Data;

@Data
public class MacSerialListDTO {
    private Long itemId;
    private String macAddress;
    private String serialNumber;
}
