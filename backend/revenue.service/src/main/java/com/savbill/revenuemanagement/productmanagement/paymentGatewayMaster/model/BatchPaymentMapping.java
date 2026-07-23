package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model;


import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;


@Entity
@Data
@Table(name = "tbltbatchpaymentmapping")
public class BatchPaymentMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "credit_doc_id")
    private CreditDocument creditDocument;

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "batch_id")
    private BatchPayment batchPayment;

    @Column(name = "is_deleted")
    private Boolean is_deleted=false;
}
