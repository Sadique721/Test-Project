package com.savbill.taskmanagement.core.modules.State.dto;

import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.modules.Country.dto.CountryPojo;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
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

}
