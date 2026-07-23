package com.diameter.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tbltplanqosmapping")
public class PlanQosMappingEntity {

    @Id
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @JoinColumn(name = "planid", referencedColumnName = "POSTPAIDPLANID")
    @ManyToOne
    @JsonBackReference
    private PostpaidPlan postpaidPlan;

    @JoinColumn(name = "qosid", referencedColumnName = "id")
    @OneToOne
    private QOSPolicy qosPolicy;

    @Column(name = "from_percentage", length = 40)
    private Double frompercentage;

    @Column(name = "to_percentage", length = 40)
    private Double topercentage;

    @Column(name = "isdelete")
    private Boolean isdelete;

}
