package com.savbill.cpm.model.postpaid;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DebitDocumentSummary {
    private DebitDocument debitDocument; // Example field for the entity
    private Double totalAmount;

    public DebitDocumentSummary(DebitDocument debitDocument, Double totalAmount){
        this.debitDocument=debitDocument;
        this.totalAmount=totalAmount;

    }
}
