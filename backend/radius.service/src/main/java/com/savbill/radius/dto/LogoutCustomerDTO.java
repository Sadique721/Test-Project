package com.savbill.radius.dto;

import lombok.Data;

@Data
public class LogoutCustomerDTO {

    private Integer custId;

    private String username;

    private Integer mvnoId;

    private String framedIP;
}
