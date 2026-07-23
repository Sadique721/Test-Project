package com.savbill.notification.BusinessUnit.domain;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmbusinessunit")
//@EntityListeners(AuditableListener.class)
public class BusinessUnit {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "businessunitid")
    private Long id;

    private String buname;

    private String bucode;

    private String status;

    @Column(name = "plan_binding_type",length = 50)
    private String planBindingType;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;




}
