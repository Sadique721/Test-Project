package com.savbill.revenuemanagement.core.entity.partner;


import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "TBLMPOSTPAIDPLANCHARGEREL")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostpaidPlanCharge {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POSTPAIDPLANCHARGERELID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "BILLINGCYCLE", nullable = false, length = 40)
    private Integer billingCycle;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "CHARGEID")
    private Charge charge;

    @CreationTimestamp
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @Column(name = "chargeprice")
    private Double chargeprice;

    @Column(name = "chargename")
    private String chargeName;
    @ManyToOne
    @JoinColumn(name = "POSTPAIDPLANID")
    @ToString.Exclude
    @JsonBackReference
    private PostpaidPlan plan;
    @Transient
    private Integer planId;

    @Transient
    private Integer chargeId;
}

