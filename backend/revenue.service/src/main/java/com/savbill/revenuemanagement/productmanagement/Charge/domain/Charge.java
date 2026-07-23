package com.savbill.revenuemanagement.productmanagement.Charge.domain;

import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;

import com.savbill.revenuemanagement.productmanagement.PlanService.domain.Services;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.Tax;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "tblmcharges")
@EntityListeners(AuditableListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Charge extends Auditable {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHARGEID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "CHARGENAME", nullable = false, length = 40)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 40)
    private String desc;

    @Column(name = "CHARGETYPE", nullable = false, length = 40)
    private String chargetype;

    @Column(name = "PRICE", nullable = false, length = 40)
    private double price;

    @Column(name = "actual_price", length = 40)
    private double actualprice;

    @JoinColumn(name = "TAXID")
    @OneToOne(cascade = CascadeType.ALL)
    private Tax tax;

    @Column(name = "DISCOUNTID", nullable = false, length = 40)
    private Integer discountid;

    @Column(name = "dbr", nullable = false, length = 40)
    private double dbr;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    private String chargecategory;
    private String saccode;

    //private Double taxamount;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "service", nullable = false, length = 40)
    private String service;

    @Column(name = "status",nullable = false,length = 40)
    private String status;

    @Column(name = "LEDGER_ID",length = 40)
    private String ledgerId;

    @Column(name = "royalty_payable")
    private Boolean royalty_payable=false;


    @Column(name = "taxamount")
    private Double taxamount;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "kra_sync_id")
    private String kraSyncId;

    @Column(name = "is_kra_synced", columnDefinition = "BOOLEAN DEFAULT FALSE", nullable = false)
    private Boolean isKraSynced = false;


    @Column(name = "pushable_ledger_id")
    private String pushableLedgerId;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tblmservicechargemapping", joinColumns = {@JoinColumn(name = "chargeid")}, inverseJoinColumns = {@JoinColumn(name = "servicesid")})
    private List<Services> serviceList = new ArrayList<>();
}
