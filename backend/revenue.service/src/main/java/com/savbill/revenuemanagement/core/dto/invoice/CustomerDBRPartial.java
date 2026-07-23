package com.savbill.revenuemanagement.core.dto.invoice;

import java.time.LocalDate;

public interface CustomerDBRPartial {
    Long getDebitDocId();
    LocalDate getStartdate();
    Double getPendingamt();
    Double getDbr();
}

