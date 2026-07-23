package com.savbill.revenuemanagement.PaymentTransfer;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblttransfer_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransferAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "from_parent_cust_id")
    private Integer fromParentCustId;

    @Column(name = "from_child_cust_id")
    private Integer fromChildCustId;

    @Column(name = "to_child_cust_id")
    private Integer toChildCustId;

    @Column(name = "to_parent_cust_id")
    private Integer toParentCustId;

    @Column(name = "main_cust_id")
    private Integer mainCustomerId;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

}

