package com.savbill.cpm.model.postpaid;

import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.pojo.api.CustomersPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
public class CustMacMapppingPojo extends Auditable {

    private Integer custid;

    private List<CustMacMappping> custMacMapppingList;

    private Integer id;

    private String macAddress;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private CustomersPojo customer;
    private Boolean isDeleted = false;
}
