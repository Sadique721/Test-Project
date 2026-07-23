package com.savbill.integrationsystem.PaymentIntegration.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoMoPePayment {

    private String amount;
    private String currency;
    private String externalId;
    private Payer payer;
    /*
    This is message which we want to send to customer
     */
    private String payerMessage;

    /*
    This is message which we get to callback
     */
    private String payeeNote;

    private String status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payer {
        private String partyIdType;
        private String partyId;
    }
}


