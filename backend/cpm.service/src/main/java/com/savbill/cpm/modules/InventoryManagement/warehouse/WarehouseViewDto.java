package com.savbill.cpm.modules.InventoryManagement.warehouse;

import lombok.Data;

@Data
public class WarehouseViewDto {
    Long id;
    String name;
    String description;
    String status;
}
