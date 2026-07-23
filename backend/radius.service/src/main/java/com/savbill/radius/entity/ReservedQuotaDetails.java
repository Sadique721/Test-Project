package com.savbill.radius.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tblreservedquotadtls")
public class ReservedQuotaDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "cust_id")
    private Integer custId;

    @Column(name = "usedquota")
    private Double usedQuota = 0.0;

    @Column(name = "unused_quota")
    private Double unusedQuota = 0.0;

    @Column(name = "reserved_quota")
    private Double reservedQuota = 0.0;

    @Column(name = "parent_cust_id")
    private Integer parentCustId;

    @Column(name = "custquotadtlsid")
    private Integer custquotadtlsid;

}
