package com.savbill.cpm.pojo.NewCustPojos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NewCustPlanMappingPojo {
    @NotNull
    private Integer planId;
    private String service;
    private String billTo = "CUSTOMER";
    private String custPlanStatus = "Active";
    private Double discount;
    private Boolean isInvoiceToOrg = false;
    private Integer billableCustomerId=null;
    private Integer custServiceMappingId;
    private String planName;
    private String billableAddress;

    public NewCustPlanMappingPojo(Integer planId, String service, String billTo, String custPlanStatus, Double discount, Boolean isInvoiceToOrg, Integer billableCustomerId, Integer custServiceMappingId) {
        this.planId = planId;
        this.service = service;
        this.billTo = billTo;
        this.custPlanStatus = custPlanStatus;
        this.discount = discount;
        this.isInvoiceToOrg = isInvoiceToOrg;
        this.billableCustomerId = billableCustomerId;
        this.custServiceMappingId = custServiceMappingId;
    }
}
