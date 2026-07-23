package com.savbill.revenuemanagement.productmanagement.TimeBasePolicy.domain;


import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblmtimebasepolicy")
@EntityListeners(AuditableListener.class)
public class TimeBasePolicy extends Auditable  {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id", nullable = false, length = 40)
    private Long id;

    @Column(name = "policy_name", nullable = false, length = 40)
    private String name;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "timeBasePolicy",orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TimeBasePolicyDetails> timeBasePolicyDetailsList = new ArrayList<>();


}
