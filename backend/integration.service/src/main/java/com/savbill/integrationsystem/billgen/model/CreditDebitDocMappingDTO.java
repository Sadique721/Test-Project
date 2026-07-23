package com.savbill.integrationsystem.billgen.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreditDebitDocMappingDTO {
    private Integer id;
    private Integer creditDocId;
    private Integer debtDocId;
    private Boolean isDeleted = false;
    private Double adjustedAmount;
    private Integer withdrawId;
}
