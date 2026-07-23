package com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductWarehouseMapViewDTO {
    private Long id;
    private String productName;
    private Long thresholdQty;
    private String unit;
}
