package com.savbill.integrationsystem.billgen.entity;

import com.savbill.integrationsystem.rabbitmq.ChargeMessage;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "TBLCHARGES")
public class ChargeData {
    @Id
    @Column(name = "CHARGEID", length = 40)
    private Integer id;

    @Column(name = "CHARGENAME", length = 40)
    private String name;

    @Column(name = "DESCRIPTION", length = 40)
    private String desc;

    @Column(name = "CHARGETYPE", length = 40)
    private String chargetype;

    @Column(name = "PRICE", length = 40)
    private double price;

    @Column(name = "actual_price", length = 40)
    private double actualprice;

//    @JoinColumn(name = "TAXID")
//    @OneToOne(cascade = CascadeType.ALL)
    //private Tax tax;

    @Column(name = "DISCOUNTID", length = 40)
    private Integer discountid;

    @Column(name = "dbr", length = 40)
    private double dbr;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    private String chargecategory;
    private String saccode;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "tblmservicechargemapping", joinColumns = {@JoinColumn(name = "chargeid")}, inverseJoinColumns = {@JoinColumn(name = "servicesid")})
//    private List<Services> serviceList = new ArrayList<>();

    //private Double taxamount;

    @Column(name = "MVNOID", length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", length = 40, updatable = false)
    private Long buId;

    @Column(name = "service", length = 40)
    private String service;

    @Column(name = "status", length = 40)
    private String status;

    @Column(name = "LEDGER_ID", length = 40)
    private String ledgerId;

    @Column(name = "royalty_payable")
    private Boolean royalty_payable = false;

    @Transient
    private Double taxamount;

    @Column(name = "pushable_ledger_id")
    private String pushableLedgerId;

    public ChargeData(ChargeMessage message) {
        this.id = message.getId();
        this.name = message.getName();
        this.desc = message.getDesc();
        this.chargetype = message.getChargetype();
        this.price = message.getPrice();
        this.actualprice = message.getActualprice();
        this.discountid = message.getDiscountid();
        this.dbr = message.getDbr();
        this.isDelete = message.getIsDelete();
        this.chargecategory = message.getChargecategory();
        this.saccode = message.getSaccode();
        this.mvnoId = message.getMvnoId();
        this.buId = message.getBuId();
        //this.service = message.getServiceid();
        this.status = message.getStatus();
        this.ledgerId = message.getLedgerId();
        this.royalty_payable = message.getRoyalty_payable();
        this.taxamount = message.getTaxamount();
        this.pushableLedgerId = message.getPushableLedgerId();
    }

    public ChargeData() {

    }
}
