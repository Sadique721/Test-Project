package com.savbill.revenuemanagement.core.controller.invoice.postpaid;

import lombok.Data;

@Data
public class TrialDebitDocumentTAXRelPojo {
    String taxname;
    Double percentage;
    Double amount;
    int chargeId;

    public TrialDebitDocumentTAXRelPojo(String taxname, Double percentage, Double amount, int chargeId) {
        this.taxname = taxname;
        this.percentage = percentage;
        this.amount = amount;
        this.chargeId = chargeId;
    }
}
