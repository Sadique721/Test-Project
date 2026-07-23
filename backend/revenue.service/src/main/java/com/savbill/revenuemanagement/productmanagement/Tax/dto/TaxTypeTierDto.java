package com.savbill.revenuemanagement.productmanagement.Tax.dto;

import lombok.Data;

@Data
public class TaxTypeTierDto {

    private Integer id;

    private String name;

    private String taxGroup;

    private Double rate;

    private Boolean beforeDiscount = false;

    private String taxLedgerId;

}


