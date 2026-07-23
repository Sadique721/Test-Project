package com.savbill.revenuemanagement.core.entity.debitdoc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetails {
    DebitDocument debitDocument;
    List<DebitDocDetails> debitDocDetails;
    List<DebitDocumentTAXRel> debitDocumentTAXRels;
}
