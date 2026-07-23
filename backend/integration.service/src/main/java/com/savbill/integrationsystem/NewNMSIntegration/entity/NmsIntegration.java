package com.savbill.integrationsystem.NewNMSIntegration.entity;


import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tblmnmsintegration")
public class NmsIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "config_name", nullable = false)
    private String configName;

    @Column(name = "cust_inven_id", nullable = false)
    private Long custInvenId;

    @Column(name = "created_by_staff", nullable = false)
    private Long createdByStaff;

    @Column(name = "mvno_id", nullable = false)
    private Long mvnoId;

    @Column(name = "operation", nullable = false)
    private String operation;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "serial_number")
    private String serialNumber;
}

