package com.savbill.revenuemanagement.core.entity.debitdoc;

import lombok.Data;

@Data
public class TrailDebitDocumentDTOForAdjustment {

    Integer id;

    Double totalAmount;

    Double adjustedAmount;

    Integer custId;

    String status;

    public TrailDebitDocumentDTOForAdjustment(Integer id, Double totalAmount, Double adjustedAmount, Integer custId) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.adjustedAmount = adjustedAmount;
        this.custId = custId;
    }
}
