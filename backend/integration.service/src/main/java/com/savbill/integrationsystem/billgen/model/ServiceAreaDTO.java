package com.savbill.integrationsystem.billgen.model;

import com.savbill.integrationsystem.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class ServiceAreaDTO implements IBaseDto {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted = false;
    private String latitude;
    private String longitude;
    private Long areaid;
    private Long mvnoId;
    private List<Integer> pincodes;
    private Long cityid;
    private Long displayId;
    private String displayName;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Long getMvnoId() {
        // TODO Auto-generated method stub
        return mvnoId;
    }

}
