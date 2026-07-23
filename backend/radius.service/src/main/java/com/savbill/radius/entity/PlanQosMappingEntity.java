package com.savbill.radius.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
@ToString
@Table(name = "tbltplanqosmapping")
public class PlanQosMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 40)
    private Long id;
    @Column(name = "planid")
    private Integer postpaidPlan;

    @Column(name="qosid")
    private Integer qosPolicy;

    @Column(name = "from_percentage",  length = 40)
    private Double frompercentage;

    @Column(name = "to_percentage", length = 40)
    private Double topercentage;

    @Column(name = "isdelete")
    private Boolean isdelete;


    public PlanQosMappingEntity(){}
    public PlanQosMappingEntity(Map message) {


            if (message.get("planid") != null)
                this.postpaidPlan = Integer.parseInt(message.get("planid").toString());
            if (message.get("qosid") != null)
                this.qosPolicy = Integer.valueOf(message.get("qosid").toString());
            if (message.get("frompercentage") != null)
                this.frompercentage = Double.valueOf(message.get("frompercentage").toString());
            if (message.get("topercentage") != null)
                this.topercentage = Double.valueOf(message.get("topercentage").toString());
            if (message.get("isdelete") != null)
                this.isdelete = Boolean.valueOf(message.get("isdelete").toString());
        }



}
