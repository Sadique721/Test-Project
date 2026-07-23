package com.savbill.revenuemanagement.productmanagement.Plan.dto;

import com.savbill.revenuemanagement.productmanagement.Charge.dto.ChargePojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class PostpaidPlanChargePojo {

    private Integer id;

    private ChargePojo charge;

    private Integer billingCycle;

    @CreationTimestamp
    private LocalDateTime createdate;

    @JsonBackReference
    @ToString.Exclude
    private PostpaidPlanPojo plan;

    private Double chargeprice;

//    private String chargeName;

}
