package com.savbill.inventorymanagement.modules.StaffUser;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ActiveStaffPojo {
    private Integer id;
    private String username;
    private String firstname;
    private String lastname;
}
