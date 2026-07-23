package com.savbill.integrationsystem.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CustomerWalletPojo {
    private Double walletAmount;
    private List<CreditDocumentPaymentPojo> creditDocumentPaymentPojos;
}

