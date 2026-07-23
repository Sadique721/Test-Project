package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import lombok.Data;

@Data
public class SaveChargeSharedDataMessage {

    private Integer id;
    private String name;
    private String desc;
    private String chargetype;
    private double price;
    private double actualprice;
    private Integer taxId;
    private Boolean isDelete;
    private Integer mvnoId;
    private Long buId;
    private String service;
    private String status;
    private Double taxamount;
    private Integer createdById;
    private Integer lastModifiedById;
    private Boolean isinventorycharge;
    private String chargecategory;
    private Long productId;
    private String inventoryChargeType;
}
