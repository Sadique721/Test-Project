package com.savbill.revenuemanagement.core.entity.ladger;


import lombok.Data;

@Data
public class CreditDocumentDTOForAdjustment {

    Integer id;

    Double amount;

    Double adjustedAmount;

    Integer custId;

    String status;


    public CreditDocumentDTOForAdjustment(Integer id, Double amount, Double adjustedAmount,Integer custId) {
        this.id = id;
        this.amount = amount;
        this.adjustedAmount = adjustedAmount;
        this.custId = custId;
    }

}
