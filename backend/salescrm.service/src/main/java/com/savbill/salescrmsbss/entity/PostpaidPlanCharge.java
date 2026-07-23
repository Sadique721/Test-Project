package com.savbill.salescrmsbss.entity;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.PostpaidPlanChargePojo;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLMPOSTPAIDPLANCHARGEREL")
public class PostpaidPlanCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POSTPAIDPLANCHARGERELID", nullable = false, length = 40)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "CHARGEID")
    private Charge charge;

    @Column(name = "BILLINGCYCLE", nullable = false, length = 40)
    private Integer billingCycle;

    @CreationTimestamp
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "POSTPAIDPLANID")
    @ToString.Exclude
    private PostpaidPlan plan;

    @Column(name = "chargeprice")
    private Double chargeprice;

    @Column(name="apig_plancharge_id")
    private Long apiGatewayPlanChargeId;

    public PostpaidPlanCharge(PostpaidPlanChargePojo postpaidPlanChargePojo){
        setApiGatewayPlanChargeId(postpaidPlanChargePojo.getId().longValue());
        setId(postpaidPlanChargePojo.getId());
        setBillingCycle(postpaidPlanChargePojo.getBillingCycle());
        setCreatedate(postpaidPlanChargePojo.getCreatedate());
        setChargeprice(postpaidPlanChargePojo.getChargeprice());
    }


}
