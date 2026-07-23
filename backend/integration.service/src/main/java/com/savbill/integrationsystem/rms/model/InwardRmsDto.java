package com.savbill.integrationsystem.rms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InwardRmsDto {

    private Long inwardId;
    private String productName;
    private String warehouseName;
    private Long quantity;
    private String type;
    private List<ProductDetailDto> productDetails;
}
