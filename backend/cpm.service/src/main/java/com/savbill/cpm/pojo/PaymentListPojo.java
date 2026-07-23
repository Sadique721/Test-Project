package com.savbill.cpm.pojo;

import lombok.Data;

@Data
public class PaymentListPojo {

    private Double tdsAmountAgainstInvoice;
    private Double abbsAmountAgainstInvoice;

    private Integer invoiceId;

    private Double amountAgainstInvoice;




}
