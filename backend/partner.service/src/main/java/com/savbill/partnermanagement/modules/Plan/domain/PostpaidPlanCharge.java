package com.savbill.partnermanagement.modules.Plan.domain;

import com.savbill.partnermanagement.modules.Charge.domain.Charge;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "TBLMPOSTPAIDPLANCHARGEREL")
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

