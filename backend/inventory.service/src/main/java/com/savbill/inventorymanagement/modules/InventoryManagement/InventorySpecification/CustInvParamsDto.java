package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustInvParamsDto {

     private Long id;

     private String paramName;

     private String paramValue;

     private Long custId;

     private Long custSerMapId;

     private Long custInvId;
}
