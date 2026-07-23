package com.savbill.cpm.pojo.api;

import java.util.Set;

import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.model.postpaid.CustSpecialPlanRelMappping;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustSpecialPlanRelMapppingPojo extends Auditable {

	private Long id;
	
	private String name;
	private String mappingName;
	
	private String status;
	
    Set<CustSpecialPlanMapppingPojo> custMapping;
	
	Set<CustSpecialPlanMapppingPojo> planMapping;

	Set<CustSpecialPlanMapppingPojo> planGroupMapping;

	Set<CustSpecialPlanMapppingPojo> leadCustMapping;

	private Integer mvnoId;

	private Integer nextTeamHierarchyMapping;

	private Integer nextStaff;
	private String remarks;
	private String flag;
	private String mvnoName;

	public CustSpecialPlanRelMapppingPojo(CustSpecialPlanRelMappping custSpecialPlanRelMappping,
			Set<CustSpecialPlanMapppingPojo> custMapping, Set<CustSpecialPlanMapppingPojo> planMapping, Set<CustSpecialPlanMapppingPojo> planGroupMapping,Set<CustSpecialPlanMapppingPojo> leadCustMapping) {
		this.id = custSpecialPlanRelMappping.getId();
		this.name = custSpecialPlanRelMappping.getMappingName();
		this.status = custSpecialPlanRelMappping.getStatus();
		this.custMapping = custMapping;
		this.planMapping = planMapping;
		this.planGroupMapping = planGroupMapping;
		this.mvnoId = custSpecialPlanRelMappping.getMvnoId();
		this.nextStaff=custSpecialPlanRelMappping.getNextStaff();
		this.nextTeamHierarchyMapping=custSpecialPlanRelMappping.getNextTeamHierarchyMapping();
		this.remarks=custSpecialPlanRelMappping.getRemarks();
		this.flag=custSpecialPlanRelMappping.getFlag();
	 	this.leadCustMapping = leadCustMapping;
		 this.mvnoName = custSpecialPlanRelMappping.getMvnoName();
	}

	
}
