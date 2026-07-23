package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification;

import lombok.Data;

@Data
public class UpdateInvenSpecValueDTO {
    private Long itemId;
    private Long invenId;
    private String newParamValue;
}
