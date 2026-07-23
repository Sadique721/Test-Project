package com.savbill.integrationsystem.rms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRmsDto {

    private Long productId;
    private String productName;
    private String productCategory;
    private String manufacturer;
    private String model;
    private String specification;
    private String casName;
}
