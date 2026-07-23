package com.savbill.revenuemanagement.KRA.Dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ETimsSaleItemDTO {
    private String itemCode;
    private String itemName;
    private String taxTypeCode;

    private Double unitPrice;
    private Integer pkgQuantity;
    private Integer quantity;
    private Double discountRate;
}