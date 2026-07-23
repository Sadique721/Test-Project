package com.savbill.revenuemanagement.core.entity.invoice;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildPartnerAddress {

    private String childPartnerName;

    private String address1;

    private String city;

    private String country;

    private String pinCode;

    private String state;
}
