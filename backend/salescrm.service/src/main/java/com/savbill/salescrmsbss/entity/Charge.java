package com.savbill.salescrmsbss.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.savbill.salescrmsbss.entity.pojo.ChargePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLCHARGES")
public class Charge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHARGEID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "CHARGENAME", nullable = false, length = 40)
    private String name;

    @Column(name = "CHARGETYPE", nullable = false, length = 40)
    private String chargetype;

    @Column(name = "PRICE", nullable = false, length = 40)
    private double price;

    @Column(name = "actual_price", length = 40)
    private double actualprice;

    @JoinColumn(name = "tax_id")
    private Integer taxId;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    private String chargecategory;

    @Column(name="apig_charge_id")
    private Long apiGatewayChargeId;

    private String saccode;

    @Column(name = "LEDGER_ID",length = 40)
    private String ledgerId;

    @Column(name="service_id")
    private Long serviceId;

    public Charge(ChargePojo chargePojo){
        setApiGatewayChargeId(chargePojo.getId().longValue());
        setId(chargePojo.getId());
        setName(chargePojo.getName());
        setChargetype(chargePojo.getChargetype());
        setSaccode(chargePojo.getSaccode());
        setLedgerId(chargePojo.getLedgerId());
        setIsDelete(chargePojo.getIsDelete());
        setTaxId(chargePojo.getTaxid());
        setPrice(chargePojo.getPrice());
        setActualprice(chargePojo.getActualprice());
        setBuId(chargePojo.getBuId());
        setMvnoId(chargePojo.getMvnoId());
        setChargecategory(chargePojo.getChargecategory());
    }

}

