package com.savbill.cpm.modules.TimeBasePolicy.module;

import com.savbill.cpm.core.dto.IBaseDto2;
import com.savbill.cpm.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TimeBasePolicyDTO extends Auditable implements IBaseDto2 {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Long buId;
    private String mvnoName;

    @JsonManagedReference
    private List<TimeBasePolicyDetailsDTO> timeBasePolicyDetailsList = new ArrayList<>();



    @Override
    public Long getIdentityKey() {
        return id;
    }
}
