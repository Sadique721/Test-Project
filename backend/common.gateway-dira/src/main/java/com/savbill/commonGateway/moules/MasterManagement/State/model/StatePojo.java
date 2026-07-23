package com.savbill.commonGateway.moules.MasterManagement.State.model;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.moules.MasterManagement.Country.model.CountryPojo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class StatePojo extends Auditable {

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
    public StatePojo(Integer id, String name, String status, Boolean isDeleted, Integer mvnoId, Integer displayId, String displayName) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.isDeleted = isDeleted;
        this.mvnoId = mvnoId;
        this.displayId = displayId;
        this.displayName = displayName;
    }

}
