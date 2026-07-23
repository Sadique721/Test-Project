package com.savbill.revenuemanagement.core.controller.invoice.postpaid;

import lombok.Data;

@Data
public class TrialDebitDocShowPojo {
    private Integer id;

    private String docnumber;

    private String createdByName;

    private Double tax;

    private Double totalamount;

    private Double adjustedAmount;

    private Double pendingAmt = 0d;

    private  String refundAbleAmount;

    private Double subtotal;

    private String billrunstatus;

    public TrialDebitDocShowPojo(Integer id, String docnumber, String createdByName, Double tax,
                                 Double totalamount, Double adjustedAmount,
                                 Double subtotal,
                                 String billrunstatus) {
        this.id = id;
        this.docnumber = docnumber;
        this.createdByName = createdByName;
        this.tax = tax;
        this.totalamount = totalamount;
        this.adjustedAmount = adjustedAmount;
        this.subtotal = subtotal;
        this.billrunstatus = billrunstatus;

    }
}
