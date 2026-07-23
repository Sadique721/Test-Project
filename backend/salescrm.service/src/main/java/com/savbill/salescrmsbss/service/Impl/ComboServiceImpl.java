package com.savbill.salescrmsbss.service.Impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.entity.Branch;
import com.savbill.salescrmsbss.entity.ClientService;
import com.savbill.salescrmsbss.entity.Customers;
import com.savbill.salescrmsbss.entity.LeadSource;
import com.savbill.salescrmsbss.entity.LeadSubSource;
import com.savbill.salescrmsbss.entity.Partner;
import com.savbill.salescrmsbss.entity.RejectReason;
import com.savbill.salescrmsbss.entity.RejectSubReason;
import com.savbill.salescrmsbss.entity.ServiceArea;
import com.savbill.salescrmsbss.entity.StaffUser;
import com.savbill.salescrmsbss.entity.pojo.CommonListDTO;
import com.savbill.salescrmsbss.repository.BranchRepository;
import com.savbill.salescrmsbss.repository.CustomersRepository;
import com.savbill.salescrmsbss.repository.LeadSourceRepository;
import com.savbill.salescrmsbss.repository.PartnerRepository;
import com.savbill.salescrmsbss.repository.RejectReasonRepository;
import com.savbill.salescrmsbss.repository.ServiceAreaRepository;
import com.savbill.salescrmsbss.repository.StaffUserRepository;
import com.savbill.salescrmsbss.service.ClientServiceSrv;
import com.savbill.salescrmsbss.service.ComboService;
import com.savbill.salescrmsbss.service.LeadMasterService;
import com.savbill.salescrmsbss.utils.CommonConstants;

@Service
public class ComboServiceImpl implements ComboService {

	@Autowired
	private ClientServiceSrv clientServiceSrv;

	@Autowired
	private LeadSourceRepository leadSourceRepo;

	@Autowired
	private RejectReasonRepository rejectReasonRepo;

	@Autowired
	private StaffUserRepository staffUserRepository;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private ServiceAreaRepository serviceAreaRepository;

	@Autowired
	private PartnerRepository partnerRepository;

	@Autowired
	private CustomersRepository customersRepository;

	@Autowired
	private LeadMasterService leadMasterService;

	@Override
	public List<CommonListDTO> getLeadTypes() {
		// TODO Auto-generated method stub
		List<String> leadtypes = getList(CommonConstants.LEAD_TYPE);
		List<CommonListDTO> commonListDto = new ArrayList<>();

		for (String value : leadtypes) {
			CommonListDTO dto = new CommonListDTO();
			dto.setDisplayName(value);
			dto.setText(value);
			dto.setValue(value);
			commonListDto.add(dto);
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getPreviousVendors() {
		// TODO Auto-generated method stub

		List<String> list = getList(CommonConstants.PREVIOUS_VENDOR_TYPE);
		List<CommonListDTO> commonListDto = new ArrayList<>();

		for (String value : list) {
			CommonListDTO dto = new CommonListDTO();
			dto.setValue(value);
			dto.setDisplayName(value);
			dto.setText(value);
			commonListDto.add(dto);
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getSeriverTypes() {
		// TODO Auto-generated method stub

		List<String> list = getList(CommonConstants.SERVICER_TYPE);
		List<CommonListDTO> commonListDto = new ArrayList<>();

		for (String value : list) {
			CommonListDTO dto = new CommonListDTO();
			dto.setValue(value);
			dto.setDisplayName(value);
			dto.setText(value);
			commonListDto.add(dto);
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getLeadSourceAndItsSubSource() {
		// TODO Auto-generated method stub

		List<LeadSource> leadSourceList = leadSourceRepo.findAll().stream()
				.filter(item -> item.getIsDelete() == false)
				.collect(Collectors.toList());

		List<CommonListDTO> commonListDto = new ArrayList<>();
		List<CommonListDTO> commonListSubDto = new ArrayList<>();

		// iterate lead sub sources
		if (leadSourceList != null && leadSourceList.size() > 0) {
			for (LeadSource obj : leadSourceList) {
				if (obj.getLeadSubSourceList() != null && obj.getLeadSubSourceList().size() > 0) {
					for (LeadSubSource item : obj.getLeadSubSourceList()) {
						CommonListDTO dto = new CommonListDTO();
						dto.setDisplayId(item.getId().intValue());
						dto.setDisplayName(item.getLeadSubSourceName());
						commonListSubDto.add(dto);
					}
				}
			}
			// iterate lead sources
			for (LeadSource obj : leadSourceList) {
				CommonListDTO dto = new CommonListDTO();
				dto.setDisplayId(obj.getId().intValue());
				dto.setDisplayName(obj.getLeadSourceName());
				dto.setSubTypeList(commonListSubDto != null && commonListSubDto.size() > 0 ? commonListSubDto
						: new ArrayList<CommonListDTO>());
				commonListDto.add(dto);
			}
		}
		return commonListDto != null ? commonListDto : new ArrayList<CommonListDTO>();
	}

	@Override
	public List<CommonListDTO> getRejectedReasonsAndItsSubReasons(Long buId, Long mvnoId) {
		// TODO Auto-generated method stub

		List<RejectReason> rejectReasonList = rejectReasonRepo.findAll().stream()
				.filter(item -> item.getIsDelete() == false && item.getBuId() == buId && item.getMvnoId() == mvnoId)
				.collect(Collectors.toList());

		List<CommonListDTO> commonListDto = new ArrayList<>();
		List<CommonListDTO> commonListSubDto = new ArrayList<>();

		// iterate reject sub reasons
		if (rejectReasonList != null && rejectReasonList.size() > 0) {
			for (RejectReason obj : rejectReasonList) {
				if (obj.getRejectSubReasonList() != null && obj.getRejectSubReasonList().size() > 0) {
					for (RejectSubReason item : obj.getRejectSubReasonList()) {
						CommonListDTO dto = new CommonListDTO();
						dto.setDisplayId(item.getId().intValue());
						dto.setDisplayName(item.getName());
						commonListSubDto.add(dto);
					}
				}
			}
			// iterate reject reasons
			for (RejectReason obj : rejectReasonList) {
				CommonListDTO dto = new CommonListDTO();
				dto.setDisplayId(obj.getId().intValue());
				dto.setDisplayName(obj.getName());
				dto.setSubTypeList(commonListSubDto != null && commonListSubDto.size() > 0 ? commonListSubDto
						: new ArrayList<CommonListDTO>());
				commonListDto.add(dto);
			}
		}
		return commonListDto != null ? commonListDto : new ArrayList<CommonListDTO>();
	}

	@Override
	public List<CommonListDTO> getLeadBranchList() {
		// TODO Auto-generated method stub

		List<Branch> branchList = this.branchRepository.findAll().stream().filter(data -> data.getIsDeleted() == false)
				.collect(Collectors.toList());
		List<CommonListDTO> commonListDto = new ArrayList<>();

		if (branchList != null && branchList.size() > 0) {
			for (Branch branchObj : branchList) {
				CommonListDTO dto = new CommonListDTO();
				dto.setDisplayId(branchObj.getId().intValue());
				dto.setDisplayName(branchObj.getName());
				commonListDto.add(dto);
			}
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getLeadCustomers() {
		// TODO Auto-generated method stub
		List<Customers> customerList = this.customersRepository.findAll().stream()
				.filter(data -> data.getIsDeleted() == false).collect(Collectors.toList());
		List<CommonListDTO> commonListDto = new ArrayList<>();

		if (customerList != null && customerList.size() > 0) {
			for (Customers custObj : customerList) {
				CommonListDTO dto = new CommonListDTO();
				dto.setDisplayId(custObj.getId().intValue());
				String customerName = custObj.getFirstname() + " " + custObj.getLastname();
				dto.setDisplayName(customerName);
				commonListDto.add(dto);
			}
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getLeadStaffs() {
		// TODO Auto-generated method stub

		List<StaffUser> staffUser = this.staffUserRepository.findAll().stream()
				.filter(data -> data.getIsDelete() == false).collect(Collectors.toList());
		List<CommonListDTO> commonListDto = new ArrayList<>();

		if (staffUser != null && staffUser.size() > 0) {
			for (StaffUser staffUSerObj : staffUser) {
				CommonListDTO dto = new CommonListDTO();
				dto.setDisplayId(staffUSerObj.getId().intValue());
				String staffUserName = staffUSerObj.getFirstname() + " " + staffUSerObj.getLastname();
				dto.setDisplayName(staffUserName);
				commonListDto.add(dto);
			}
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getLeadPartners() {
		// TODO Auto-generated method stub
		List<Partner> partnerList = this.partnerRepository.findAll().stream()
				.filter(data -> data.getIsDelete() == false).collect(Collectors.toList());
		List<CommonListDTO> commonListDto = new ArrayList<>();

		if (partnerList != null && partnerList.size() > 0) {
			for (Partner partnerObj : partnerList) {
				CommonListDTO dto = new CommonListDTO();
				dto.setDisplayId(partnerObj.getId().intValue());
				dto.setDisplayName(partnerObj.getName());
				commonListDto.add(dto);
			}
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getLeadServiceAreaList() {
		// TODO Auto-generated method stub
		List<ServiceArea> serviceAreaList = this.serviceAreaRepository.findAll().stream()
				.filter(data -> data.getIsDeleted() == false).collect(Collectors.toList());
		List<CommonListDTO> commonListDto = new ArrayList<>();

		if (serviceAreaList != null && serviceAreaList.size() > 0) {
			for (ServiceArea saObj : serviceAreaList) {
				CommonListDTO dto = new CommonListDTO();
				dto.setDisplayId(saObj.getId().intValue());
				dto.setDisplayName(saObj.getName());
				commonListDto.add(dto);
			}
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getPlanTypes() {
		// TODO Auto-generated method stub
		List<String> planTypes = getList(CommonConstants.PLAN_TYPE);
		List<CommonListDTO> commonListDto = new ArrayList<>();

		for (String value : planTypes) {
			CommonListDTO dto = new CommonListDTO();
			dto.setDisplayName(value);
			dto.setText(value);
			dto.setValue(value);
			commonListDto.add(dto);
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getLeadOriginTypes() {
		// TODO Auto-generated method stub
		List<String> leadOriginTypes = getList(CommonConstants.LEAD_ORIGIN_TYPES);
		List<CommonListDTO> commonListDto = new ArrayList<>();

		for (String value : leadOriginTypes) {
			CommonListDTO dto = new CommonListDTO();
			dto.setDisplayName(value);
			dto.setText(value);
			dto.setValue(value);
			commonListDto.add(dto);
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getRequireServiceTypes() {
		// TODO Auto-generated method stub
		List<String> requireServiceTypes = getList(CommonConstants.REQUIRE_SERVICE_TYPES);
		List<CommonListDTO> commonListDto = new ArrayList<>();

		for (String value : requireServiceTypes) {
			CommonListDTO dto = new CommonListDTO();
			dto.setDisplayName(value);
			dto.setText(value);
			dto.setValue(value);
			commonListDto.add(dto);
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getLeadCategories() {
		// TODO Auto-generated method stub
		List<String> leadCategories = getList(CommonConstants.LEAD_CATEGORY);
		List<CommonListDTO> commonListDto = new ArrayList<>();

		for (String value : leadCategories) {
			CommonListDTO dto = new CommonListDTO();
			dto.setDisplayName(value);
			dto.setText(value);
			dto.setValue(value);
			commonListDto.add(dto);
		}
		return commonListDto;
	}

	@Override
	public List<CommonListDTO> getFeasibilities() {
		// TODO Auto-generated method stub
		List<String> leadFeasibilities = getList(CommonConstants.FEASIBILITY);
		List<CommonListDTO> commonListDto = new ArrayList<>();

		for (String value : leadFeasibilities) {
			CommonListDTO dto = new CommonListDTO();
			dto.setDisplayName(value);
			dto.setText(value);
			dto.setValue(value);
			commonListDto.add(dto);
		}
		return commonListDto;
	}

	@Override
	public CommonListDTO getLeadNo() {
		// TODO Auto-generated method stub

		String leadNo = this.leadMasterService.generateLeadNo();
		CommonListDTO obj = new CommonListDTO();
		if (leadNo != null && !"".equalsIgnoreCase(leadNo)) {
			obj.setDisplayName(leadNo);
			obj.setValue(leadNo);
		}
		return obj;
	}

	@Override
	public List<CommonListDTO> getCustomerGenderValues() {
		// TODO Auto-generated method stub
		List<String> leadCategories = getList(CommonConstants.LEAD_CUSTOMER_GENDER_TYPE);
		List<CommonListDTO> commonListDto = new ArrayList<>();

		for (String value : leadCategories) {
			CommonListDTO dto = new CommonListDTO();
			dto.setDisplayName(value);
			dto.setText(value);
			dto.setValue(value);
			commonListDto.add(dto);
		}
		return commonListDto;
	}

	List<String> getList(String constant) {

		ClientService obj = clientServiceSrv.getByNameAndMvnoId(constant,getLoggedInMvnoId().longValue());
		if (obj != null) {
			String[] myArray = obj.getValue().split(",");
			List<String> myList = Arrays.asList(myArray);
			return myList != null && myList.size() > 0 ? myList : new ArrayList<String>();
		}
		return new ArrayList<String>();
	}

//	@Override
//	public List<CommonListDTO> getCommonCustomerCategories() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<CommonListDTO> getCommonCustomerTypes() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<CommonListDTO> getCommonCustomerSubTypes() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<CommonListDTO> getCommonCustomerSectors() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public List<CommonListDTO> getCommonValleyTypes() {
//		// TODO Auto-generated method stub
//		List<String> list = new ArrayList<>(Arrays.asList("insideValley", "outsideValley"));
//		CommonListDTO item = new CommonListDTO();
//		List<CommonListDTO> commonList = new ArrayList<>();
//		for (String obj : list) {
//			item.setDisplayName(obj);
//			commonList.add(item);
//		}
//		return commonList != null || commonList.size() == 0 ? commonList : new ArrayList<CommonListDTO>();
//	}

//	@Override
//	public List<CommonListDTO> getCommonInsideValleyTypes() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<CommonListDTO> getCommonOutsieValleyTypes() {
//		// TODO Auto-generated method stub
//		return null;
//	}
public Integer getLoggedInMvnoId() {
	int loggedInMvnoId = -1;
	try {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		if (null != securityContext.getAuthentication()) {
			loggedInMvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
		}
	} catch (Exception e) {
		loggedInMvnoId = -1;
	}
	return loggedInMvnoId;
}
}
