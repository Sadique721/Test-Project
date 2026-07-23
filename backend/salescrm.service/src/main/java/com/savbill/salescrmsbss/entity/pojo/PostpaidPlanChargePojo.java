package com.savbill.salescrmsbss.entity.pojo;

import com.savbill.salescrmsbss.entity.PostpaidPlanCharge;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    public PostpaidPlanChargePojo(PostpaidPlanCharge postpaidPlanCharge){
        setId(postpaidPlanCharge.getApiGatewayPlanChargeId().intValue());
        setChargeprice(postpaidPlanCharge.getChargeprice());
        setBillingCycle(postpaidPlanCharge.getBillingCycle());
        setCreatedate(postpaidPlanCharge.getCreatedate());
    }

}
