package com.savbill.revenuemanagement.core.dto.invoice;

import java.time.LocalDateTime;

public interface DebitDocumentCreditNoteView {
    Integer getId();
    String getCreatedByName();
    String getDocnumber();
    Double getTax();
    Double getTotalamount();
    Double getAdjustedAmount();
    String getStatus();
    LocalDateTime getEndate();
    Integer getCustpackrelid();
    LocalDateTime getStartdate();
    Double getSubtotal();
    Double getDiscount();
    Boolean getIsDirectChargeInvoice();
}
