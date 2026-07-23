package com.savbill.cpm.pojo.api;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

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
