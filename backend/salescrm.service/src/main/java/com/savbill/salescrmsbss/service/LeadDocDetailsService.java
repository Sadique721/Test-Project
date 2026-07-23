package com.savbill.salescrmsbss.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.savbill.salescrmsbss.entity.LeadDocDetails;
import com.savbill.salescrmsbss.entity.pojo.LeadDocDetailsDTO;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;

public interface LeadDocDetailsService {
	
	void validateRequest(LeadDocDetailsDTO leadMasterPojo,Integer operation);
	
	LeadDocDetails save(LeadDocDetailsDTO leadMasterPojo,MultipartFile files) throws IOException;
		
	LeadDocDetails findById(Long docId);
			
	Page<LeadDocDetails> findAll(PaginationRequestDTO paginationRequestDTO,Long id);
	
	void deleteLeadDocDetails(Long leadDocId);

	LeadDocDetails approveLeadDocDetails(Long docId, String status);
	
	LeadDocDetails uploadDocumentOnline(LeadDocDetailsDTO customerDocDetails, Boolean isUpdate) throws Exception;
	
	List<LeadDocDetails> findDocsByLeadId(Long leadId);
}
