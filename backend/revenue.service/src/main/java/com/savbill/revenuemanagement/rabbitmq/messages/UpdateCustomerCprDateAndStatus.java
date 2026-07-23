package com.savbill.revenuemanagement.rabbitmq.messages;

import com.savbill.revenuemanagement.productmanagement.Plan.domain.CustPlanMapppingPojo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UpdateCustomerCprDateAndStatus {
    private Integer id;
    private List<CustPlanMapppingPojo> custPlanMapppingList = new ArrayList<>();
    private String startDateString;
    private String endDateString;
    private String expirayDateString;
    private String status;
    private boolean isTrailPlan;

    public UpdateCustomerCprDateAndStatus(UpdateCustomerCprDateAndStatus updateCustomerCprDateAndStatus) {
        this.id = updateCustomerCprDateAndStatus.getId();
        this.custPlanMapppingList = updateCustomerCprDateAndStatus.getCustPlanMapppingList();
    }
}
