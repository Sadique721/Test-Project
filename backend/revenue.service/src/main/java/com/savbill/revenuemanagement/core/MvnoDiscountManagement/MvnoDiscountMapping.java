package com.savbill.revenuemanagement.core.MvnoDiscountManagement;

import com.savbill.revenuemanagement.core.Mvno.domain.Mvno;
import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@ToString
@Table(name = "tbltmvnodiscountmapping")
@EntityListeners(AuditableListener.class)
public class MvnoDiscountMapping extends Auditable {

    @Id
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @Column(name = "discount", nullable = false)
    private double discount;

    @ManyToOne
    @JoinColumn(name = "mvnoid")
    private Mvno mvno;

    @Column(name = "count_from", nullable = false)
    private Long countFrom;

    @Column(name = "count_to", nullable = false)
    private Long countTo;

    @Column(name = "charge_id", nullable = false)
    private Long chargeId;

    @Column(name = "charge_name", nullable = false)
    private String chargeName;
}
