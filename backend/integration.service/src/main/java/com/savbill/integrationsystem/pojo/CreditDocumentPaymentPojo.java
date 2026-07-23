package com.savbill.integrationsystem.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreditDocumentPaymentPojo {

    private Integer id;
    private Double remainingAmount = 0.0;
}
