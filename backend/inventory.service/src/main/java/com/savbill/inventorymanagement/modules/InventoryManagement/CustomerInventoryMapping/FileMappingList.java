package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import lombok.Data;

import java.util.List;

@Data
public class FileMappingList {

    private String sectionName;

    private List<FileDetails> fileDetails;
}
