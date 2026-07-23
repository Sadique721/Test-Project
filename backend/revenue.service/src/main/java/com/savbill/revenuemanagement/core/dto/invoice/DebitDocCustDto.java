package com.savbill.revenuemanagement.core.dto.invoice;

import lombok.Data;

@Data
public class DebitDocCustDto {
    private Integer debitDocumentId;
    private Integer customerId;

    public DebitDocCustDto(Integer debitDocumentId, Integer customerId) {
        this.debitDocumentId = debitDocumentId;
        this.customerId = customerId;
    }
}
