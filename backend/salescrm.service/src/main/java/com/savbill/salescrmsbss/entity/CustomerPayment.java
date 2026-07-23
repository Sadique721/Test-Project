package com.savbill.salescrmsbss.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.helper.CustomerPaymentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbltpayment")
public class CustomerPayment {

	@Id
    @Column(name = "orderid", nullable = false, length = 40)
    private Long orderId;

    @Column (name="leadid", nullable =false)
    private Integer leadId;

    @Column(name = "payment", nullable = false, length = 40)
    private Double payment;

    @Column(name = "status")
    private String status;

    @Column(name = "pgtransactionid")
    private String pgTransactionId;

    @Column(name = "linkid")
    private String linkId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "plan_id")
    private Integer planId;

    public CustomerPayment(CustomerPaymentDto paymentDto) {
        this.leadId = paymentDto.getLeadId();
        this.payment = paymentDto.getPayment();
        this.status = paymentDto.getStatus();
    }
}
