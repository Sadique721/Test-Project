package com.savbill.revenuemanagement.core.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentListPojo {

    private Double tdsAmountAgainstInvoice;
    private Double abbsAmountAgainstInvoice;

    private Integer invoiceId;

    private Double amountAgainstInvoice;




}
