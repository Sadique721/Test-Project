package com.savbill.integrationsystem.billgen.entity;

import com.savbill.integrationsystem.rabbitmq.TaxMessage;
import lombok.Data;
import lombok.ToString;
import javax.persistence.*;


@Entity
@Data
@ToString
@Table(name = "TBLMTAX")
public class TaxData {

    @Id
    @Column(name = "TAXID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 150)
    private String desc;

    @Column(name = "TAXTYPE", nullable = false, length = 40)
    private String taxtype;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;



    public TaxData(TaxMessage message) {
        this.id = message.getId();
        this.name = message.getName();
        this.desc = message.getDesc();
        this.taxtype = message.getTaxtype();
        this.status = message.getStatus();
        this.mvnoId = message.getMvnoId();
        this.buId = message.getBuId();
        this.isDelete = message.getIsDelete();
    }

    public TaxData() {
    }
}
