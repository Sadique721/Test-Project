package com.savbill.integrationsystem.core.dto;


import lombok.Data;

@Data
public class CustomerResponseDTO {

    public String username;

    public String password;

    public String firstname;

    public String lastname;

    public String walletBalance;

    public String accountNo;

    public String status;

    public Integer custId;

    public Integer mvnoId;

    public String mobileNumber;

    public String  custtype;
}
