package com.savbill.revenuemanagement.core.dto.invoice;

import java.time.LocalDateTime;

public interface CustPlanMappingPartial {
    LocalDateTime getStartDate();
    Integer getId();
    Long getDebitDocId();
    Long getCustPackRelId();
}
