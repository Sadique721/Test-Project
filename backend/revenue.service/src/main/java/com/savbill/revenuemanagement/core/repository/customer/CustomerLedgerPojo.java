package com.savbill.revenuemanagement.core.repository.customer;

import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class CustomerLedgerPojo extends Auditable {

    private Integer id;

    private Double totaldue;

    private Double totalpaid;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private CustomersPojo customer;

}
