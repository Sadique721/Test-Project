package com.savbill.inventorymanagement.modules.acl.model;

import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParameters;
import lombok.Data;

@Data
public class ProductParameterDefaultValueMappingDTO {

    private Long id;

    private Long productId;

    private Long parameterId;

    private String paramName;

    private String defaultValue;

    public ProductParameterDefaultValueMappingDTO(SpecificationParameters specificationParameters, String value) {
        this.parameterId = specificationParameters.getId();
        this.paramName = specificationParameters.getParamName();
        this.defaultValue = value;
    }
}
