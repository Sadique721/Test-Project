package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceMappingMessage {
    private Long serviceId;
    private Integer customerId;
}
