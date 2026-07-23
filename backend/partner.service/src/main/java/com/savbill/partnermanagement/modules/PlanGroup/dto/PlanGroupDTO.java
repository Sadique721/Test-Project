package com.savbill.partnermanagement.modules.PlanGroup.dto;


import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.partnermanagement.modules.Product_Plan_Group_Mapping.ProductPlanGroupMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlanGroupDTO extends Auditable {
	private Integer planGroupId;
	private String planGroupName;
	private String status;
	private String planType;
	private String planMode;
//	private Long serviceAreaId;
	private Integer mvnoId;
	private List<PlanGroupMappingDTO> planMappingList = new ArrayList<>();
	private Long buId;
	private String planGroupType;
	private String category;
	private String accessibility;
	private Boolean allowdiscount;
	private Double offerprice;

	private List<Long> serviceAreaId = new ArrayList<>();

//	private  List<ServiceAreaPlanGroupMappingDTO> serviceAreaPlanGroupMappingDTOList = new ArrayList<>();

	private List<ProductPlanGroupMapping> productPlanGroupMappingList;

	private ServiceArea serviceArea;

	private Integer nextStaff;
	private Integer nextTeamHierarchyMappingId;


	@Override
	public String toString() {
		return "PlanGroupDTO [planGroupId=" + planGroupId + ", planGroupName=" + planGroupName + ", status=" + status
				 + ", serviceAreaId=" + serviceArea + ", mvnoId=" + mvnoId
				+", productPlanGroupMappingList=" + productPlanGroupMappingList + ", planMappingList=" + planMappingList + ", productPlanGroupMappingList=" + productPlanGroupMappingList +"]";
	}

	private Boolean invoiceToOrg;

	private Boolean requiredApproval;

	private Long servicearea_id;
}
