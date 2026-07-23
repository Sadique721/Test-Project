package com.savbill.cpm.pojo.api;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StaffUserAllPojo {
    private Integer id;
    @NotNull
    private String username;
}
