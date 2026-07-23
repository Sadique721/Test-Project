package com.savbill.revenuemanagement.core.dto.ChangePlanDto;

import com.savbill.revenuemanagement.core.entity.customers.CustPlanMappping;
import com.savbill.revenuemanagement.core.entity.customers.CustomerChargeHistory;
import com.savbill.revenuemanagement.core.entity.customers.CustomerServiceMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChildInvoiceDetails {

    List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
    List<CustomerServiceMapping> customerServiceMappings = new ArrayList<>();
    List<CustomerChargeHistory> customerChargeHistories = new ArrayList<>();
    List<Long> custServiceIdList  = new ArrayList<>();


}
