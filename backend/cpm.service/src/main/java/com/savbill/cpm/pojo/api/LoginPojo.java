package com.savbill.cpm.pojo.api;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginPojo {

    @NotNull
    private String username;

    @NotNull
    private String password;

}
