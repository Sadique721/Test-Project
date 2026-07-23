package com.savbill.revenuemanagement.productmanagement.parentchildmapping;

import lombok.Data;

@Data
public class CafChildCustomerApproveMessege {
    private Integer customerId;

    private Integer loggedInUser;

    private String status;

    private String cafApproveStatus;
}
