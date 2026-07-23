package com.savbill.integrationsystem.NewNMSIntegration.entity;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltnmsintegrationparameters")
public class IntegrationParameters {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "integration_id", nullable = false)
    private Long integration_id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "param_name", nullable = false)
    private String paramName;

    @Column(name = "param_value", nullable = false)
    private String paramValue;
}
