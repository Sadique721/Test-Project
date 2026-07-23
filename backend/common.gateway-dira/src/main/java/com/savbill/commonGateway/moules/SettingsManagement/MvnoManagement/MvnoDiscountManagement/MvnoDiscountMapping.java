package com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoDiscountManagement;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.Mvno;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@ToString
@Table(name = "tbltmvnodiscountmapping")
@EntityListeners(AuditableListener.class)
public class MvnoDiscountMapping extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
