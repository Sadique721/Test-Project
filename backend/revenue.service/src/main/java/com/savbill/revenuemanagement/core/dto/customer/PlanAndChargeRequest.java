package com.savbill.revenuemanagement.core.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanAndChargeRequest {
    private Integer custId;
    private Integer mvnoId;
    private Boolean isTaxCalculate;
    private List<PlanMappingDto> planMapping;
    private List<ChargeDetailDto> custChargeDetailsPojoList;
}
