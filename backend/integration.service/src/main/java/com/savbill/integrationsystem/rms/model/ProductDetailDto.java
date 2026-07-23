package com.savbill.integrationsystem.rms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDto {
    private Long productDetailId;
    private String serialNumber;
}
