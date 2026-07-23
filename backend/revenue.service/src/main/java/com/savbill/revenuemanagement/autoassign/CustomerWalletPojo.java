package com.savbill.revenuemanagement.autoassign;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CustomerWalletPojo {
    private Double walletAmount;
    private List<CreditDocumentPaymentPojo> creditDocumentPaymentPojos;
}
