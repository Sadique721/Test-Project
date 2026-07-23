package com.savbill.cpm.model.postpaid;

import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.pojo.api.CustomersPojo;
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
