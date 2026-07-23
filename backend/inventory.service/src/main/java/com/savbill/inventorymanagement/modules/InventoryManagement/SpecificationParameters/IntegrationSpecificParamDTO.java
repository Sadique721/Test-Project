package com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationSpecificParamDTO {
    private String paramName;
    private String paramValue;
}
