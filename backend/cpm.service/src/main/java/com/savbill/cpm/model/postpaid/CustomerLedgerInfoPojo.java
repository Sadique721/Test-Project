package com.savbill.cpm.model.postpaid;

import lombok.Data;

import java.util.List;

@Data
public class CustomerLedgerInfoPojo {
    private Double openingAmount;
    private List<CustomerLedgerDtlsPojo> debitCreditDetail;
    private Double closingBalance;
}
