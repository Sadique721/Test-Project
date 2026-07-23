package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateServicesSharedDataMessage {

    private Integer id;
    private String name;
    private Integer mvnoId;
    private Long buId;
    private Boolean is_dtv;
    private List<ProductCategory> productCategories = new ArrayList<>();
    private Boolean isDeleted;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
