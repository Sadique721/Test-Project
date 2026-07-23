package com.savbill.revenuemanagement.rabbitmq.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AutoRenewalBoosterPlanMessage {

    private Boolean renewalForBooster;
    private Integer custPlanMappingId;

}
