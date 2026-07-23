package com.savbill.inventorymanagement.modules.MasterManagement.State;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.MasterManagement.Country.CountryPojo;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StatePojo extends Auditable implements IBaseDto {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String status;

    @NotNull
    private CountryPojo countryPojo;
    private String countryName;
    private Boolean isDeleted = false;
    
    private Integer mvnoId;

    private Integer displayId;
    private String displayName;

    @Override
    public Long getIdentityKey() {
        return Long.valueOf(id);
    }
}
