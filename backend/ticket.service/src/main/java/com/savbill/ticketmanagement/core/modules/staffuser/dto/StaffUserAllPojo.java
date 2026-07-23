package com.savbill.ticketmanagement.core.modules.staffuser.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StaffUserAllPojo {
    private Integer id;
    @NotNull
    private String username;
}
