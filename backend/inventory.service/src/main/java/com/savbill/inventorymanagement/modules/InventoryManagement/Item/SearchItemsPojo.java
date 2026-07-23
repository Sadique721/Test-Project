package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SearchItemsPojo {

    private String ownerType;
    private Long ownerId;
    private Long productId;
    private Long inwardId;
    private String itemStatus;
    private String itemType;
    private String warrantyStatus;
    private String ownership;

    private String serialNumber;
    private String macAddress;

}
