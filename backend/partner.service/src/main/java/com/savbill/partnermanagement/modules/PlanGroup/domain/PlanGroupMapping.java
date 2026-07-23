package com.savbill.partnermanagement.modules.PlanGroup.domain;

import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.modules.Plan.domain.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmplangroupmapping")
@NoArgsConstructor
public class PlanGroupMapping extends Auditable {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plangroupmappingid")
    private Integer planGroupMappingId;

    @ManyToOne
    @JoinColumn(name = "POSTPAIDPLANID", referencedColumnName = "POSTPAIDPLANID")
    @ToString.Exclude
    private PostpaidPlan plan;

    @Column(nullable = false, length = 40)
    private String service;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "plangroupid", referencedColumnName = "plangroupid")
    @ToString.Exclude
    private PlanGroup planGroup;

    @Column(name = "is_deleted")
    private Boolean isDelete = false;;

    @Column(name= "MVNOID")
    private Integer mvnoId;

    @Transient
    private Double validity;

    @Column(name= "newofferprice")
    private Double newofferprice;

    @Transient
    private Long planId;
    @Transient
    private Integer planGroupId;

    @Override
    public String toString() {
        return "PlanGroupMapping{" +
                "planGroupMappingId=" + planGroupMappingId +
                ", service='" + service + '\'' +
                ", isDelete=" + isDelete +
                ", mvnoId=" + mvnoId +
                ", validity=" + validity +
                ", newofferprice=" + newofferprice +
                '}';
    }


    public PlanGroupMapping(PlanGroupMapping planGroupMapping,PostpaidPlan plan,PlanGroup planGroup){
        this.planGroupMappingId = planGroupMapping.getPlanGroupMappingId();
        this.service = planGroupMapping.getService();
        this.isDelete = planGroupMapping.getIsDelete();
        this.mvnoId = planGroupMapping.getMvnoId();
        this.validity = planGroupMapping.getValidity();
        this.newofferprice = planGroupMapping.getNewofferprice();
        this.plan = plan;
        this.planGroup = planGroup;
    }
}
