package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage;

import com.savbill.revenuemanagement.productmanagement.PlanService.domain.Services;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.Tax;
import lombok.Data;

import java.util.List;

@Data
public class UpdateChargeSharedDataMessage {

    private Integer id;
    private String name;
    private String desc;
    private String chargetype;
    private double price;
    private double actualprice;
    private Long taxId;
    private Tax tax;
    private Integer discountid;
    private double dbr;
    private Boolean isDelete;
    private String saccode;
    private List<Services> serviceList;
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
