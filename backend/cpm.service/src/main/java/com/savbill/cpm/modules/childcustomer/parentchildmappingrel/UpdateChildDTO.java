package com.savbill.cpm.modules.childcustomer.parentchildmappingrel;

import lombok.Data;

@Data
public class UpdateChildDTO {

    private Long childId;

    private String firstName;

    private String lastName;

    private String status;

    private String email;


}
