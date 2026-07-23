package com.savbill.cpm.modules.subscriber.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.savbill.cpm.model.postpaid.CreditDocument;

@Data
public class RecordpaymentResponseDTO {
    private CustomersBasicDetailsPojo customersBasicDetails;
    private RecordPaymentRequestDTO recordPaymentRequestDTO;
    private List<CreditDocument> creditDocument = new ArrayList<>();
}
