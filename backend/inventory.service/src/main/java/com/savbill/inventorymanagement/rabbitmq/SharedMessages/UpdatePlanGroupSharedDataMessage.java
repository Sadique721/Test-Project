package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.ProductPlanGroupMapping;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.PlanGroupMapping.PlanGroupMapping;
import lombok.Data;

import java.util.List;

@Data
public class UpdatePlanGroupSharedDataMessage {
    private Integer planGroupId;
    private String planGroupName;
    private String status;
    private Integer mvnoId;
    private String plantype;
    private String planMode;
    private Boolean isDelete;
    private List<PlanGroupMapping> planMappingList;
    private Long buId;
    private String planGroupType;
    private String category;
    private List<ServiceArea> servicearea;
    private List<ProductPlanGroupMapping> productPlanGroupMappingList;
    private Integer createdById;
    private Integer lastModifiedById;
}
