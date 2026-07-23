package com.savbill.cpm.modules.ServiceParameters.model;

import com.savbill.cpm.core.dto.IBaseDto2;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ServiceParametersDTO implements IBaseDto2 {

    private Long id;
    private String name;
    private Boolean isdelete;
    private String value;
    private Boolean isMandatory;
    private String fieldName;
    private String dataType;

    @Override
    @JsonIgnore
    public Long getIdentityKey() {
        return id;
    }

    @Override
    @JsonIgnore
    public Integer getMvnoId() {
        return null;
    }

    @Override
    @JsonIgnore
    public void setMvnoId(Integer mvnoId) {

    }

    @Override
    @JsonIgnore
    public Long getBuId() {
        return null;
    }
}
