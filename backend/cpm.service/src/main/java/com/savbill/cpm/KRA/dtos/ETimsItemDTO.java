package com.savbill.cpm.KRA.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ETimsItemDTO {

    private String itemCode;
    private String itemClassifiCode;
    private String itemTypeCode;
    private String itemName;
    private String itemStrdName;
    private String countryCode;
    private String pkgUnitCode;
    private String qtyUnitCode;
    private String taxTypeCode;
    private String batchNo;
    private String barcode;
    private Double unitPrice;
    private Double group1UnitPrice;
    private Double group2UnitPrice;
    private Double group3UnitPrice;
    private Double group4UnitPrice;
    private Double group5UnitPrice;
    private String additionalInfo;
    private Double saftyQuantity;
    private Boolean isInrcApplicable;
    private Boolean isUsed;
    private Integer packageQuantity;
    private Integer mvnoId;
}
