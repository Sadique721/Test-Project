package com.savbill.revenuemanagement.core.repository.customer;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDBRResponse {
    public List<CustomerDBRPojo> customerDBRPojos;
    public Double outstandingPending;
    public Double outstandingDbr;
    public Double outstandingRevenue;
}
