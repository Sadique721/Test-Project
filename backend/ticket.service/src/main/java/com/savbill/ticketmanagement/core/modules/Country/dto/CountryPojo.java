package com.savbill.ticketmanagement.core.modules.Country.dto;


import com.savbill.ticketmanagement.core.data.Auditable;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CountryPojo extends Auditable {

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
