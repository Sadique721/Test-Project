package com.savbill.radius.helper;

import lombok.Data;

@Data
public class DynamicAttributeMappingDto {
    
    private Long id;

    private Long clientGroupId;

    private String customerAttribute;

    private String radiusAttribute;

    private Boolean isAbsenceAccepted;

}
