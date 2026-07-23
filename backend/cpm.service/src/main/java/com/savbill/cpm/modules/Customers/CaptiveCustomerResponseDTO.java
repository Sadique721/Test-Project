package com.savbill.cpm.modules.Customers;

import com.savbill.cpm.pojo.api.CustomersPojo;
import lombok.Data;

@Data
public class CaptiveCustomerResponseDTO {

    private Boolean isParentCustAvailable;

    private Boolean isBranchBindWithServiceArea;

    private Boolean isPartnerBindWithServiceArea;

    private CustomersPojo parentCustomers;

}
