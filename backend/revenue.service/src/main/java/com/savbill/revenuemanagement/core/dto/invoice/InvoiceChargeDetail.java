package com.savbill.revenuemanagement.core.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceChargeDetail {

    private Integer chargeId;

    private String chargeName;

    private Double price;

    private List<InvoiceTaxDetail> invoiceTaxDetails;
}
