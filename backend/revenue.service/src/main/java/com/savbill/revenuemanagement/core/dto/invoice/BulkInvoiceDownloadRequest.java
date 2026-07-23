package com.savbill.revenuemanagement.core.dto.invoice;

import java.util.List;

public class BulkInvoiceDownloadRequest {

    private List<Integer> debitDocIds;

    public List<Integer> getDebitDocIds() {
        return debitDocIds;
    }

    public void setDebitDocIds(List<Integer> debitDocIds) {
        this.debitDocIds = debitDocIds;
    }
}
