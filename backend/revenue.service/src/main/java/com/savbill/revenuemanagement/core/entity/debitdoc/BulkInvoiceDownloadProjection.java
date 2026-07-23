package com.savbill.revenuemanagement.core.entity.debitdoc;

public interface BulkInvoiceDownloadProjection {

    Integer getDebitDocId();

    String getDocNumber();

    Integer getCustomerId();
}
