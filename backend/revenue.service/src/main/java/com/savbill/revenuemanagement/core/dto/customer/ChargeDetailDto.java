package com.savbill.revenuemanagement.core.dto.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeDetailDto {
    private String type;
    private Integer chargeid;
    private Integer validity;
    private Double price;
    private Double actualprice;
    private String charge_date;
    private Integer planid; // If this is actually an integer, change to Integer
    private String planName;
    private String unitsOfValidity;
    private Integer billingCycle;
    private Integer paymentOwnerId;
    private Double discount;
    private String staticIPAdrress;
    private String expiry;
    private String expiryDate;
    private String connection_no;
}
