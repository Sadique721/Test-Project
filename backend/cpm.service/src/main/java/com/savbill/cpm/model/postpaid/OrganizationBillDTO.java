package com.savbill.cpm.model.postpaid;

import com.savbill.cpm.model.common.Customers;
import lombok.Data;

@Data
public class OrganizationBillDTO {
    DebitDocument debitDocument;
    Customers actualCustomers;
}
