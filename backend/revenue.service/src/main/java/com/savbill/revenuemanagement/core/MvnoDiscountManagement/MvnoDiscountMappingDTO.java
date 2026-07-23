package com.savbill.revenuemanagement.core.MvnoDiscountManagement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MvnoDiscountMappingDTO {

    private Long id;
    
    private double discount;
    
    private Long mvnoId;
    
    private Long countFrom;

    private Long countTo;

    private Long chargeId;

    private String chargeName;

    private String lastModifiedByName;

    private String createdByName;

    private Integer createdById;

    private Integer lastModifiedById;

}
