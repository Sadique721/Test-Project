package com.savbill.radius.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearch 
{
    private String username;
    private String fullname;
    private String name;
    private String customerStatus;
    private String subscriptionMode;
    private String mobile;
    private String email;
    private String plan;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private Boolean param5;
    private Boolean param6;
    private String locationName;
    private String macaddress;
    private String framedIpAddress;
}
