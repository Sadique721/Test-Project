package com.savbill.revenuemanagement.core.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanMappingDto {
    private Long planId;
    private String service;
    private Integer validity;
    private Double discount;
    private String billTo;
    private Long billableCustomerId;
    private Double newAmount;
    private Double offerPrice;
    private Boolean isInvoiceToOrg;
    private Boolean istrialplan;
    private String discountType;
    private String discountExpiryDate;
    private String invoiceType;
    private String currency;
    private Integer serviceId;
    private String serialNumber;
    private Boolean skipQuotaUpdate;
}
