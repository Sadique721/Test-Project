package com.savbill.salescrmsbss.entity.pojo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.savbill.salescrmsbss.utils.TemplateConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class FielmappingDto{
    private Long id;

    @NotNull(message = "fieldId"+ TemplateConstants.MANDATORY_NOT_NULL_MSG)
    private Long fieldId;

    @JsonIgnore
    private Long buid;

    private Boolean isMandatory = false;

    @NotBlank(message = "screen" + TemplateConstants.MANDATORY_NOT_NULL_MSG)
    private Long screen;
    
    private String screenName;

    @NotBlank(message = "module" + TemplateConstants.MANDATORY_NOT_NULL_MSG)
    private String module;

    private Boolean isDeleted = false;

    @NotBlank(message = "fieldName" + TemplateConstants.MANDATORY_NOT_NULL_MSG)
    private String fieldName;

    @NotBlank(message = "dataType" + TemplateConstants.MANDATORY_NOT_NULL_MSG)
    private String dataType;

    private Boolean defaultMandatory;
}
