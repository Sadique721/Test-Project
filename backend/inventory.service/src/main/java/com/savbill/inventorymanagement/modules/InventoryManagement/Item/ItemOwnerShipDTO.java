package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import lombok.Data;

@Data
public class ItemOwnerShipDTO {
    private Long id;
    private String ownershipType;
    private String remarks;
}
