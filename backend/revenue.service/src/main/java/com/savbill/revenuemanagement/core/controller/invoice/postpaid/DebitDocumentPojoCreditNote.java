package com.savbill.revenuemanagement.core.controller.invoice.postpaid;

import lombok.Data;


@Data
public class DebitDocumentPojoCreditNote {
    private Integer id;

    private String createdByName;

    private String docnumber;

    private Double tax;

    private Double totalamount;

    private Double adjustedAmount;

    private  String refundAbleAmount;

    private String status;
}
