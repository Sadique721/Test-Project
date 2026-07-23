package com.savbill.ticketmanagement.core.modules.PlanService.domain;

import com.savbill.ticketmanagement.core.data.Auditable;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CustServiceMapppingPojo extends Auditable {

    private Integer id;

    @NotNull
    private Integer custId;

    @NotNull
    private Long serviceId;


    public CustServiceMapppingPojo() {
    }

    @Override
    public String toString() {
        return "CustPlanMapppingPojo [id=" + id + ", serviceId=" + serviceId + ", custId=" + custId + "]";
    }




}
