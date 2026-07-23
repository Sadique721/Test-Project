package com.savbill.commonGateway.moules.DemoGraphicMapping.model;

import com.savbill.commonGateway.core.dto.IBaseDto;
import lombok.Data;

@Data
public class DemoGraphicMappingDTO implements IBaseDto {

    private Long id;
    private String currentName;
    private String newName;
    private String validationRegex;


    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }
}
