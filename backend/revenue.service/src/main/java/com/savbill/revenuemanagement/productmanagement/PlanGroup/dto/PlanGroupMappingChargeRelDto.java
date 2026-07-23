package com.savbill.revenuemanagement.productmanagement.PlanGroup.dto;


import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroupMappingChargeRel;
import lombok.Data;

import java.util.List;

@Data
public class PlanGroupMappingChargeRelDto {
    private Integer id;
    private Double chargeprice;
    private String chargeName;

    private List<PlanGroupMappingChargeRel> planGroupMappingChargeRelList;
    private Double totalPrice;
}
