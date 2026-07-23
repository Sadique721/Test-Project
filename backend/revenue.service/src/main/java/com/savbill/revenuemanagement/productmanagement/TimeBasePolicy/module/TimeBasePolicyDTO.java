package com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.module;


import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TimeBasePolicyDTO extends Auditable {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Long buId;

    @JsonManagedReference
    private List<TimeBasePolicyDetailsDTO> timeBasePolicyDetailsList = new ArrayList<>();




}
