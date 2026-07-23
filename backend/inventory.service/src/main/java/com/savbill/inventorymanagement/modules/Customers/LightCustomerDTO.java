package com.savbill.inventorymanagement.modules.Customers;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LightCustomerDTO {

    private Integer id;

    private String username;

    private Integer mvnoId;

    private String status;

    private String custtype;

    private Integer partnerId;

    public LightCustomerDTO(Integer id, String username, Integer mvnoId , String status, String custtype,Integer partnerId) {
        this.id = id;
        this.username = username;
        this.mvnoId = mvnoId;
        this.status= status;
        this.custtype = custtype;
        this.partnerId = partnerId;
    }

}
