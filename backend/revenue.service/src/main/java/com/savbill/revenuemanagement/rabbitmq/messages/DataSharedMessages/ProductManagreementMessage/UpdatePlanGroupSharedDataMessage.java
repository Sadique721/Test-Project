package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage;

import com.savbill.revenuemanagement.core.entity.partner.PostpaidPlanCharge;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceArea;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroupMapping;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroupMappingChargeRel;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.ServiceAreaPlanGroupMapping;
import com.savbill.revenuemanagement.productmanagement.Product_Plan_Group_Mapping.ProductPlanGroupMapping;
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
    private List<ServiceArea> servicearea;
    private List<ProductPlanGroupMapping> productPlanGroupMappingList;
    private Long templateId;
    private Boolean invoiceToOrg;
    private Boolean requiredApproval;
    private Integer createdById;
    private List<PostpaidPlanCharge> chargeList = new ArrayList<>();
    private Integer lastModifiedById;
}
