package com.savbill.partnermanagement.rabbitmq.product;
import com.savbill.partnermanagement.customers.CustChargeDetailsRevenue;
import com.savbill.partnermanagement.customers.CustPlanMappingRevenue;
import com.savbill.partnermanagement.customers.CustomerChargeHistoryRevenue;
import com.savbill.partnermanagement.customers.CustomerServiceMappingRevenue;
import lombok.Data;

import java.util.List;

@Data
public class ChangePlanMessage {

    String type;

    Integer renewalId;

    List<CustPlanMappingRevenue> newCustPlanMappingRevenues;

    List<CustChargeDetailsRevenue> custChargeDetailsRevenues;

    List<CustomerChargeHistoryRevenue> customerChargeHistoryRevenues;

    List<CustomerServiceMappingRevenue> customerServiceMappingRevenues;

    List<CustPlanMappingRevenue> oldCustPlanMappingRevenues;

    List<Integer> custChargeIds;
    private Integer createdById;

}

