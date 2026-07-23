package com.savbill.integrationsystem.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
public class ProductParameterDefaultValueMappingDTO {

    private Long id;

    private Long custSerId;

    private Long parameterId;

    private String paramName;

    private String defaultValue;

    public ProductParameterDefaultValueMappingDTO(Long id, Long custSerId, String paramName, String defaultValue) {
        this.id = id;
        this.custSerId = custSerId;
        this.paramName = paramName;
        this.defaultValue = defaultValue;
    }
}
