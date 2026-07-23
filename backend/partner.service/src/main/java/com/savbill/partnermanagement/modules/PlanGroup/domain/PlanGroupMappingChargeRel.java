package com.savbill.partnermanagement.modules.PlanGroup.domain;

import com.savbill.partnermanagement.modules.Charge.domain.Charge;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tbltplangroupmappingchargerel")
@NoArgsConstructor
public class PlanGroupMappingChargeRel {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @JoinColumn(name = "plan_group_mappingid" , referencedColumnName = "plangroupmappingid")
    @ManyToOne(targetEntity = PlanGroupMapping.class,cascade = CascadeType.ALL)
    private PlanGroupMapping planGroupMapping;

    @JoinColumn(name="chargeid" , referencedColumnName = "CHARGEID")
    @OneToOne
    private Charge charge;

    @Column(name = "price", nullable = false, length = 40)
    private double price;

    @Column(name = "chargename")
    private String chargeName;

    @Column(name ="planid")
    private Integer planId;

    @Column(name = "isdelete")
    private Boolean isdelete = false;

    @Transient
    private Integer planGroupMappingId;

    @Transient
    private Integer chargeid;

    public PlanGroupMappingChargeRel(PlanGroupMappingChargeRel planGroupMappingChargeRel,Charge charge,PlanGroupMapping planGroupMapping){
        this.id = planGroupMappingChargeRel.getId();
        this.charge = charge;
        this.price = planGroupMappingChargeRel.getPrice();
        this.chargeName = planGroupMappingChargeRel.getChargeName();
        this.planId = planGroupMappingChargeRel.getPlanId();
        this.isdelete = planGroupMappingChargeRel.getIsdelete();
        this.planGroupMapping = planGroupMapping;
    }
}
