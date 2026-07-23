package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import lombok.Data;

import java.util.List;

@Data
public class InventoryFileUploadRequest {
    private Long customerInventoryId;

    private String opticalPowerRange;
    private List<SectionUploadRequest> sections;

    // Getters and Setters
}

