package com.savbill.revenuemanagement.productmanagement.childcustomer;

import lombok.Data;

@Data
public class UpdateChildCustometMessesge {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String email;
    private Long parentCustId;
    private String status;
    private String mobileNumber;
    private Boolean isParent;

}
