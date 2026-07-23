package com.savbill.integrationsystem.billgen.entity;

import com.savbill.integrationsystem.rabbitmq.PlanServiceMessage;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "TBLMSERVICES")
public class PlanServiceData {

    @Id
    @Column(name = "serviceid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "servicename", nullable = false, length = 40)
    private String name;

    @Column(name = "icname", nullable = false, length = 40)
    private String icname;

    @Column(name = "iccode", nullable = false, length = 40)
    private String iccode;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "is_qosv", nullable = false, columnDefinition = "Boolean default true")
    private Boolean isQoSV;

    @Column(name = "expiry",nullable = false,length = 100)
    private String expiry;

    private String ledgerId;

    @Column(name = "is_dtv")
    private Boolean is_dtv;

    @Column(name = "investmentcode_id")
    private Long investmentid;


    public PlanServiceData(PlanServiceMessage message) {
        this.id = message.getId();
        this.name = message.getName();
        this.icname = message.getIcname();
        this.iccode = message.getIccode();
        this.mvnoId = message.getMvnoId();
        this.buId = message.getBuId();
        this.isQoSV = message.getIsQoSV();
        this.expiry = message.getExpiry();
        this.ledgerId = message.getLedgerId();
        this.is_dtv = message.getIs_dtv();

    }

    public PlanServiceData() {

    }
}
