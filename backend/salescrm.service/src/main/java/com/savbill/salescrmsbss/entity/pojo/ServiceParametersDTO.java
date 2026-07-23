package com.savbill.salescrmsbss.entity.pojo;

import lombok.Data;

@Data
public class ServiceParametersDTO {

    private Long id;
    private String name;
    private Boolean isdelete;
    private String value;
    private Boolean isMandatory;
    private String fieldName;
    private String dataType;
    private Integer mvnoId;
    private Integer buId;
}
