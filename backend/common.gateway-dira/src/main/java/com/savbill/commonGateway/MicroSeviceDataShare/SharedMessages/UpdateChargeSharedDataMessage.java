package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;


import lombok.Data;

@Data
public class UpdateChargeSharedDataMessage {

    private Integer id;
    private String name;
    private String desc;
    private String chargetype;
    private double price;
    private double actualprice;
    private Integer taxId;
    private Integer discountid;
    private double dbr;
    private Boolean isDelete;
    private String saccode;
//    private List<Services> serviceList;
    private Integer mvnoId;
    private Long buId;
    private String service;
    private String status;
    private String ledgerId;
    private Boolean royalty_payable;
    private Double taxamount;
    private String businessType;
    private String pushableLedgerId;
    private Integer createdById;
    private Integer lastModifiedById;
}
