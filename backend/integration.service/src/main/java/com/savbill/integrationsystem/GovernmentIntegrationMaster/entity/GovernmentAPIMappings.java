package com.savbill.integrationsystem.GovernmentIntegrationMaster.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "tbltgovernmentapimapping")
public class GovernmentAPIMappings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "api_name")
    private String apiName;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "government_master_id")
    private Long governmentMasterId;


}
