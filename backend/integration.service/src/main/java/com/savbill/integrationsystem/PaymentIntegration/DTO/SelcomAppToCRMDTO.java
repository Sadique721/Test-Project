package com.savbill.integrationsystem.PaymentIntegration.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelcomAppToCRMDTO {
    private String operator;
    private String transid;
    /**
     * Reference : OrderId
     * */
    private String reference;
    /**
     * Utility Reference : Customer Account Number
     * */
    private String utilityReference;
    private String amount;
    private String msisdn;
}
