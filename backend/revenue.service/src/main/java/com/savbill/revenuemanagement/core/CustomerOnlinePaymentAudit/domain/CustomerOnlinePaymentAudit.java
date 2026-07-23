package com.savbill.revenuemanagement.core.CustomerOnlinePaymentAudit.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tbltonlinepaymentaudit")
public class CustomerOnlinePaymentAudit {
    @Id
    @Column(name = "id",nullable = false)
    private Long id;

    @Column(name = "orderid", nullable = false, length = 40)
    private Long orderId;

    @Column (name="custid")
    private Integer custId;

    @Column(name = "payment", nullable = false, length = 40)
    private Double payment;

    @Column(name = "status")
    private String status;

    @Column(name = "pgtransactionid")
    private String pgTransactionId;


    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "plan_id")
    private Integer planId;


    @Column(name="transaction_date")
    private LocalDateTime transactionDate;

    @Column(name="customer_user_name")
    private String customerUsername;

    @Column(name="mvnoid")
    private Integer mvnoid;

    @Column(name="buid")
    private Integer buid;

    @Column(name="creditdocid")
    private Integer creditDocumentId;


    @Column (name="partnerid")
    private Integer partnerId;


    @Column(name = "created_by_id")
    private Integer createdById;


    @Column(name = "created_by_name")
    private String createdByName;

    @Column(name = "account_number")
    private String accountNumber;

}
