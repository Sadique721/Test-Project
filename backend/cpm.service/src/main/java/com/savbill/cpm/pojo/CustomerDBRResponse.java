package com.savbill.cpm.pojo;

import com.savbill.cpm.modules.CustomerDBR.pojo.CustomerDBRPojo;
import lombok.Data;

import java.util.List;

@Data
public class CustomerDBRResponse {
    public List<CustomerDBRPojo> customerDBRPojos;
    public Double outstandingPending;
    public Double outstandingDbr;
    public Double outstandingRevenue;
}
