package com.savbill.revenuemanagement.mastermanagement.State.dto;


import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.mastermanagement.Country.dto.CountryPojo;
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
