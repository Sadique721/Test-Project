package com.savbill.commonGateway.moules.MasterManagement.Country.model;


import com.savbill.commonGateway.common.domain.Auditable2;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CountryPojo extends Auditable2 {

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

}
