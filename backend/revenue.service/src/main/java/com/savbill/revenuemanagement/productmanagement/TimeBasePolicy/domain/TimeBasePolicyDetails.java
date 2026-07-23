package com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltimebasepolicydetails")
public class TimeBasePolicyDetails  {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "details_id", nullable = false, length = 40)
    private Long detailsid;

//    @Column(name = "policy_id", nullable = false, length = 40)
//    private Long policyid;

    @Column(name = "from_day", nullable = false)
    private String fromDay;

    @Column(name = "to_day", nullable = false)
    private String toDay;

    @Column(name = "from_time", nullable = false)
    private String fromTime;

    @Column(name = "to_time", nullable = false)
    private String toTime;

    @Column(name = "qqsid", nullable = false)
    private Long qqsid;

    @Column(name = "access")
    private Boolean access;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "policy_id")
    private TimeBasePolicy timeBasePolicy;

}
