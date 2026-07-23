package com.savbill.cpm.pojo.customer.plans;

import com.savbill.cpm.pojo.ExtendPlanValidity;
import lombok.Data;

import java.util.List;

@Data
public class ExtendPlanValidityInBulk {

    private List<ExtendPlanValidity> extendPlanValidity;
}
