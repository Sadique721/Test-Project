package com.savbill.inventorymanagement.rabbitmq;

import com.savbill.inventorymanagement.modules.ChargeManagement.Charge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargeMessage {
//    private Integer id;
    private String name;
    private String desc;
    private String chargetype;
    private double price;
    private double actualprice;
    private Integer taxId;
    private Boolean isDelete;
    private String chargecategory;
    private Integer mvnoId;
    private Long buId;
    private String service;
    private String status;
    private Double taxamount;
    private Boolean isinventorycharge;
    private Integer createdById;
    private Integer lastModifiedById;
    private Long productId;

    public ChargeMessage(Charge charge) {
//        this.id = charge.getId();
        this.name = charge.getName();
        this.desc = charge.getDesc();
        this.chargetype = charge.getChargetype();
        this.price = charge.getPrice();
        this.actualprice = charge.getActualprice();
        this.taxId = charge.getTaxId();
        this.isDelete = charge.getIsDelete();
        this.chargecategory = charge.getChargecategory();
        this.mvnoId = charge.getMvnoId();
//        this.buId = charge.getBuId();
        this.service = charge.getService();
        this.isinventorycharge = charge.getIsinventorycharge();
        this.status = charge.getStatus();
        this.taxamount = charge.getTaxamount();
        this.createdById = charge.getCreatedById();
        this.lastModifiedById = charge.getLastModifiedById();
        this.productId = charge.getProductId();
    }
}
