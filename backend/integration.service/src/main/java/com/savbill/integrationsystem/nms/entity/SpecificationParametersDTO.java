package com.savbill.integrationsystem.nms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecificationParametersDTO {
    private Long id;
    private Long pcid;
    private String paramName;
    private String paramValue;
    private Boolean isMandatory;
    private Integer mvnoId;

}
