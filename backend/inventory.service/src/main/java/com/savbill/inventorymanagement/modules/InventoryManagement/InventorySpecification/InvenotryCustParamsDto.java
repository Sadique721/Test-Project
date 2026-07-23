package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification;

import com.savbill.inventorymanagement.modules.acl.model.ProductParameterDefaultValueMappingDTO;
import lombok.Data;

import java.util.List;

@Data
public class InvenotryCustParamsDto {

    List<Long> serializedItemIds;

    List<ProductParameterDefaultValueMappingDTO> parameters;
}
