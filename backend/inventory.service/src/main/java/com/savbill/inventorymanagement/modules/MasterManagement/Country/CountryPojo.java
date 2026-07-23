package com.savbill.inventorymanagement.modules.MasterManagement.Country;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CountryPojo extends Auditable implements IBaseDto {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String status;

    private Boolean isDelete = false;
    
//    private Integer mvnoId;

    private Integer displayId;
    private String displayName;

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    @Override
    public Long getIdentityKey() {
        return Long.valueOf(id);
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }
}
