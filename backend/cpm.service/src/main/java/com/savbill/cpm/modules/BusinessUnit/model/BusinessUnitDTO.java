package com.savbill.cpm.modules.BusinessUnit.model;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BusinessUnitDTO extends Auditable implements IBaseDto {

    private Long id;
    private String buname;
    private String bucode;
    private String status;
    private Boolean isDeleted = false;
    private String planBindingType;
    private Integer mvnoId;

    private List<Long> investmentCodeid;

    private List<String> icnames=new ArrayList<>();

    private Long displayId;
    private String displayName;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId(){
        return mvnoId;
    }
}
