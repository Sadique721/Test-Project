package com.savbill.cpm.pojo.api;

import lombok.Data;

@Data
public class PasswordPojo {
    private Integer custId;
    private String newpassword;
    private String password;
    private String selfcarepwd;
    private String remarks;
}
