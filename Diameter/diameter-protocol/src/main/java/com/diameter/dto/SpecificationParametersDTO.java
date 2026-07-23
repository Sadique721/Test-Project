package com.diameter.dto;

import com.diameter.service.IBaseDto;
import lombok.Data;

import java.util.List;

@Data
public class SpecificationParametersDTO implements IBaseDto {
    private Long id;
    private Long pcid;
    private String paramName;
    private String paramValue;
    private Boolean isAccessories; // new flag to recognise accessories
    private Boolean isConfiguration; // new flag to recognise configuration
    private Boolean isMainAccessories; // new flag to recognise configuration
    private Boolean isMandatory;
    private Boolean isMultiValueParam;
    private List<String> paramMultiValues;
    private String paramValues;
    private String defaultValue;
    private Integer mvnoId;
    private Integer custId;
    private List<Long> custInvMap;
    private String connectionNo;
    private String newParamDefaultValue;
    private String stage;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
       this.mvnoId=mvnoId;
    }
}
