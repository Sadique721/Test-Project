package com.diameter.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
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
