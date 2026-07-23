package com.savbill.inventorymanagement.modules.PostpaidPlanCharge;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.modules.ChargeManagement.Charge;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "tbltpostpaidplanchargerel")
public class PostpaidPlanCharge extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chargeid")
    private Charge charge;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "postpaidplanid")
    @ToString.Exclude
    private PostpaidPlan plan;

    @Column(name = "chargeprice")
    private Double chargeprice;

    @Column(name = "chargename")
    private String chargeName;
    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

//    @Column(name = "chargeid")
//    private Long chargeId;

}
