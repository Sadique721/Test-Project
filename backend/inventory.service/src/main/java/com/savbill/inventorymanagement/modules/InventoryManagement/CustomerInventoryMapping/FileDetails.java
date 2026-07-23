package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import lombok.Data;

@Data
public class FileDetails {
    private String fileName;
    private String uniqueName;
    private String latitude;
    private String longitude;
    private Long customerInventoryId;
    private String opticalRange;
}
