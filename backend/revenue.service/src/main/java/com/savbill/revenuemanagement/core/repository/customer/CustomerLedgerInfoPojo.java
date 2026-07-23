package com.savbill.revenuemanagement.core.repository.customer;

import lombok.Data;

import java.util.List;

@Data
public class CustomerLedgerInfoPojo {
    private Double openingAmount;
    private List<CustomerLedgerDtlsPojo> debitCreditDetail;
    private Double closingBalance;
}
