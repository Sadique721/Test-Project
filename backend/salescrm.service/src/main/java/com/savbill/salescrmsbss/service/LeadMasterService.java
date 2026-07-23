package com.savbill.salescrmsbss.service;

import java.util.List;


import com.savbill.salescrmsbss.entity.pojo.CustPlanMapppingPojo;
import com.savbill.salescrmsbss.entity.pojo.SearchLeadByBuidDTO;
import com.savbill.salescrmsbss.rabbitMq.message.LeadMasterPojoMessage;
import com.savbill.salescrmsbss.entity.LeadServiceMapping;
import org.springframework.data.domain.Page;

import com.savbill.salescrmsbss.entity.pojo.LeadMasterPojo;
import com.savbill.salescrmsbss.helper.LeadMgmtWfDTO;
import com.savbill.salescrmsbss.helper.LeadNotesDto;
import com.savbill.salescrmsbss.helper.LeadRejectDto;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;

public interface LeadMasterService {

	void validateRequest(LeadMasterPojo leadMasterPojo,Integer operation);
	
	LeadMasterPojo save(LeadMasterPojo leadMasterPojo,Long mvnoId,Long buId,Long staffId);
	
	LeadMasterPojo update(LeadMasterPojo leadMasterPojo);
	
	LeadMasterPojo findById(Long leadMasterId);
	
	List<LeadMasterPojo> findByMobileNo(Long mvnoId,List<Long> buId,String mobileNo);
	
	Page<LeadMasterPojo> search(Long mvnoid, List<Long> buId,PaginationRequestDTO paginationRequestDTO,String fromConvertedDate,String toConvertedDate);
	
	Page<LeadMasterPojo> findAll(PaginationRequestDTO paginationRequestDTO);
	
//	Page<LeadMasterPojo> findAll(Long mvnoId,PaginationRequestDTO paginationRequestDTO);
	
	//Page<LeadMasterPojo> findAll(Long mvnoId,Long buId,PaginationRequestDTO paginationRequestDTO);
	Page<LeadMasterPojo> findAll(Long mvnoId,List<Long> buId,List<Integer> serviceareaid,PaginationRequestDTO paginationRequestDTO,Integer loggedInUserId);

	void deleteLeadMaster(Long leadMasterId);
	
	LeadNotesDto saveNotes(LeadNotesDto leadNotesDto,Long staffId);

	LeadMgmtWfDTO updateCustomerLeadAssignment(LeadMgmtWfDTO leadMgmtWfDTO);

//	List<TeamHierarchyDTO> getLeadStatus(LeadMasterPojo leadMasterPojo, List<TeamHierarchyDTO> teamHierarchyDTOList);

//	List<TeamHierarchyDTO> getTeamHierarchyDTO(List<TeamHierarchyDTO> teamHierarchyDTOList);


	void rejectLead(LeadRejectDto leadRejectDto);
		
	void reopenLead(Long leadId,Long staffId);
	
	void convertLeadToCustomerCafAndSendToCustomerCafEntry(LeadMasterPojo leadMasterPojo);
	
	LeadMasterPojo saveCampaignManager(LeadMasterPojo leadMasterPojo,Long mvnoId,Long buId,Long staffId);

	String generateLeadNo();

	Page<LeadNotesDto> findAllLeadNoteWithPagination(PaginationRequestDTO paginationRequestDTO, Long id);
	
	LeadMasterPojo assignWorkFlow(Long leadMasterId,Long staffId);
	
	Page<LeadMasterPojo> findByCurrentUser(PaginationRequestDTO paginationRequestDTO,Long staffId,Long mvnoId,List<Long> buId);

	Page<LeadMasterPojo> findByCurrentUserTeamLeadList(PaginationRequestDTO paginationRequestDTO,Long staffId,Long mvnoId,List<Long> buId);

	List<LeadMasterPojo> findByusername(Long mvnoId,Long buId,String username);
	String generateCafNo();

	void updateLeadStatus(LeadMasterPojoMessage message);
    LeadMasterPojo newService(LeadMasterPojo pojo) throws Exception;

    List<LeadServiceMapping> findCircuitDetailsByLeadId(Long leadId);

    List<CustPlanMapppingPojo> findFinalServicesForLeadToCAFConvertion(Long leadId);

	LeadMasterPojo findLeadServiceMappingById(Long leadServiceMappingId);

	LeadMasterPojo updateLeadService(LeadMasterPojo pojo, Long leadMasterServiceId);

	List<LeadMasterPojo> findAllByBuidList(SearchLeadByBuidDTO searchLeadByBuidDTO);
	
	Page<LeadMasterPojo> enterpriseSearch(Long mvnoid,PaginationRequestDTO paginationRequestDTO,String fromConvertedDate,String toConvertedDate);

	List<CustPlanMapppingPojo> verifyPlansWithQuotationApproval(LeadMasterPojo leadMasterPojo);

	String getLeadNameById(Long leadId);

    int getLeadCountForCurrentUser(Integer nextApproveStaffId);
}
