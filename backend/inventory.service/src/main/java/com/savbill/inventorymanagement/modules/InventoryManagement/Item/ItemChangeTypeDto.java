package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import lombok.Data;

@Data
public class ItemChangeTypeDto {
    private Long id;
    private Long itemId;
    private String condition;
    private String remarks;
    private String filename;
    private String uniquename;
    private String otherreason;
  //  private MultipartFile file;

}
