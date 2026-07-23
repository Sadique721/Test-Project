package com.savbill.inventorymanagement.rabbitmq;

import com.savbill.inventorymanagement.modules.PostpaidPlanServiceAreaMapping.PostPaidPlanServiceAreaMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavePlanAssignmentMessage {
    private List<PostPaidPlanServiceAreaMapping> mappingList;

    private Integer createdById;

    private Integer updatedById;

    private String createdByName;

    private String lastModifiedByName;

    private Long areaId;

    private Long mvnoId;

    private Boolean staffSAMap = true;
}

