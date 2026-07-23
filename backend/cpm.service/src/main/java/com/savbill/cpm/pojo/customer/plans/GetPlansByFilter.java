package com.savbill.cpm.pojo.customer.plans;

import lombok.Data;

@Data
public class GetPlansByFilter {

    Integer custId;
    Integer customerServiceMappingID;
    Integer planGroupId;
    String changePlanType;
    String plantype;
    Integer currPlanId;
}
