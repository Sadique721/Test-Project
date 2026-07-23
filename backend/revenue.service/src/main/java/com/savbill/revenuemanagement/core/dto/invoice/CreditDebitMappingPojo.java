package com.savbill.revenuemanagement.core.dto.invoice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreditDebitMappingPojo {

    private Integer invoiceId;

    //@ToString.Exclude
    //@EqualsAndHashCode.Exclude
    @JsonBackReference
    private List<CreditDebitDataPojo> creditDocumentList;
}
