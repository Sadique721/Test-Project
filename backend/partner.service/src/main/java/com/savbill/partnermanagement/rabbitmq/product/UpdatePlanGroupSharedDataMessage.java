package com.savbill.partnermanagement.rabbitmq.product;

import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.partnermanagement.modules.Plan.domain.PostpaidPlanCharge;
import com.savbill.partnermanagement.modules.PlanGroup.domain.PlanGroupMapping;
import com.savbill.partnermanagement.modules.PlanGroup.domain.PlanGroupMappingChargeRel;
import com.savbill.partnermanagement.modules.PlanGroup.domain.ServiceAreaPlanGroupMapping;
import com.savbill.partnermanagement.modules.Product_Plan_Group_Mapping.ProductPlanGroupMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatePlanGroupSharedDataMessage {
    private Integer planGroupId;
    private String planGroupName;
    private String status;
    private Integer mvnoId;
    private String plantype;
    private String planMode;
    private Boolean isDelete;
    private List<PlanGroupMapping> planMappingList;
    private List<PlanGroupMappingChargeRel> planGroupMappingChargeRelsList;
    private List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappingList;
    private Double dbr;
    private Long buId;
    private String planGroupType;
    private String category;
    private Integer nextTeamHierarchyMappingId;
    private Integer nextStaff;
    private String accessibility;
    private Boolean allowDiscount;
    private Double offerprice;
    @JsonIgnore
    private List<ServiceArea> servicearea;
    private List<ProductPlanGroupMapping> productPlanGroupMappingList;
    private Long templateId;
    private Boolean invoiceToOrg;
    private Boolean requiredApproval;
    private Integer createdById;
    private List<PostpaidPlanCharge> chargeList = new ArrayList<>();
    private Integer lastModifiedById;
}
