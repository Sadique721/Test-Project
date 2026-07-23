package com.savbill.revenuemanagement.productmanagement.ServiceParameters.model;

import lombok.Data;

@Data
public class ServiceParametersDTO  {

    private Long id;
    private String name;
    private Boolean isdelete;
    private String value;
    private Boolean isMandatory;
    private String fieldName;
    private String dataType;

//    @Override
//    @JsonIgnore
//    public Long getIdentityKey() {
//        return id;
//    }
//
//    @Override
//    @JsonIgnore
//    public Integer getMvnoId() {
//        return null;
//    }
//
//    @Override
//    @JsonIgnore
//    public void setMvnoId(Integer mvnoId) {
//
//    }
//
//    @Override
//    @JsonIgnore
//    public Long getBuId() {
//        return null;
//    }
}
