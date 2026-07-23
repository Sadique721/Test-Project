package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model;

import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Data
@Table(name = "tbltbatchpaymentassignment")
public class BatchPaymentAssignment {
    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @DiffIgnore
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "batch_id",nullable = false)
    private BatchPayment batchPayment;

    @DiffIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "staff_id")
    private StaffUser staffUser;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "next_staff_id",nullable = true)
    private StaffUser nextStaffUser;

    @Column(name = "status", nullable = false, length = 15)
    private String status;

    @Column(name = "remark", nullable = false, length = 250)
    private String remark;

    @Column(name = "assigneddate", nullable = false, length = 250)
    private LocalDate assignedDate;

    @Column(name = "assignedstatus", nullable = false, length = 250)
    private String assignedStatus;
}
