package com.savbill.cpm.modules.BankManagement.model;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class BankManagementDTO  extends Auditable implements IBaseDto {

    private Long id;
    private String bankname;
    private String accountnum;
    private String ifsccode;
    private String bankholdername;
    private String status;
    private Boolean isDeleted = false;
    private String bankcode;
    private Integer mvnoId;
    private String banktype;

    private Long displayId;
    private String displayName;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }




}
