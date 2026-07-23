package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;

import com.savbill.ticketmanagement.core.modules.PlanService.domain.CustPlanMappingRevenue;
import com.savbill.ticketmanagement.core.modules.PlanService.domain.CustomerServiceMappingRevenue;
import lombok.Data;

import java.util.List;

@Data
public class ChangePlanMessage {

    String type;

    Integer renewalId;

    List<CustPlanMappingRevenue> newCustPlanMappingRevenues;

    List<CustomerServiceMappingRevenue> customerServiceMappingRevenues;

    List<CustPlanMappingRevenue> oldCustPlanMappingRevenues;

    List<Integer> custChargeIds;
    private Integer createdById;

    private Integer parentId;

    private List<Integer> childIds;

    private String paySource;

    private List<Long> buId;

    private Integer mvnoId;

    private Integer lcoId;

    private Boolean isLco;

    private Integer getCreatedById;

    private String getCreatedByName;
}
