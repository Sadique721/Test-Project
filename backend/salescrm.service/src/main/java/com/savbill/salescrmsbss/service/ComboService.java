package com.savbill.salescrmsbss.service;

import java.util.List;

import com.savbill.salescrmsbss.entity.pojo.CommonListDTO;

public interface ComboService {

	public List<CommonListDTO> getLeadTypes();

	public List<CommonListDTO> getPreviousVendors();

	public List<CommonListDTO> getSeriverTypes();

	public List<CommonListDTO> getLeadSourceAndItsSubSource();

//	public List<CommonListDTO> getLeadSubSources(Long leadSourceId);
	public List<CommonListDTO> getRejectedReasonsAndItsSubReasons(Long buId, Long mvnoId);

//	public List<CommonListDTO> getRejectedSubReasons(Long rejectReasonId);
	public List<CommonListDTO> getLeadBranchList();

	public List<CommonListDTO> getLeadCustomers();

	public List<CommonListDTO> getLeadStaffs();

	public List<CommonListDTO> getLeadPartners();

	public List<CommonListDTO> getLeadServiceAreaList();

	public List<CommonListDTO> getPlanTypes();

	public List<CommonListDTO> getLeadOriginTypes();

	public List<CommonListDTO> getRequireServiceTypes();

	public List<CommonListDTO> getLeadCategories();

	public List<CommonListDTO> getFeasibilities();
	
	public CommonListDTO getLeadNo();
	
	public List<CommonListDTO> getCustomerGenderValues();

//	public List<CommonListDTO> getCommonCustomerCategories();
//
//	public List<CommonListDTO> getCommonCustomerTypes();
//
//	public List<CommonListDTO> getCommonCustomerSubTypes();
//
//	public List<CommonListDTO> getCommonCustomerSectors();
//
//	public List<CommonListDTO> getCommonValleyTypes();
//
//	public List<CommonListDTO> getCommonInsideValleyTypes();
//
//	public List<CommonListDTO> getCommonOutsieValleyTypes();

}
