package com.savbill.cpm.model.postpaid;

import com.savbill.cpm.model.common.Auditable;
import lombok.Data;

import java.util.List;

@Data
public class EndMacMapppingPojo extends Auditable {

    private Integer custid;

    private List<EndMacMappping> custMacMapppingList;

    private Integer id;

    private String macAddress;

//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @JsonBackReference
//    private CustomersPojo customer;

    private String ownerType;
    private Long ownerId;
    private Boolean isDeleted = false;
}
