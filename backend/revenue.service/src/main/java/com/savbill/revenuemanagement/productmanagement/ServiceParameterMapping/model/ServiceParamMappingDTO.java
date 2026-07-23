package com.savbill.revenuemanagement.productmanagement.ServiceParameterMapping.model;


import lombok.Data;

@Data
public class ServiceParamMappingDTO  {

    private Long id;
    private Long serviceid;
    private Long serviceParamId;
//    private ServiceParameter serviceParameter;
    private String value;
    private Boolean isMandatory;
    private  String serviceParamName;

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
