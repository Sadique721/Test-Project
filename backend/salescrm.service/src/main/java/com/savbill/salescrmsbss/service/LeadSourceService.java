package com.savbill.salescrmsbss.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;

import com.savbill.salescrmsbss.entity.LeadSource;
import com.savbill.salescrmsbss.helper.LeadSourceDto;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;

public interface LeadSourceService {

	LeadSourceDto saveLeadSource(LeadSourceDto leadSourceDto,Long mvnoId,Long buId);
	
	LeadSourceDto updateLeadSource(LeadSourceDto leadSourceDto,HttpServletRequest req);
	
	Page<LeadSourceDto> findAll(Long mvnoId,List<Long> buId,PaginationRequestDTO paginationRequestDTO);
	
	List<LeadSourceDto> findAll(Long mvnoId,Long buId);
	
	List<LeadSourceDto> findAll();
	
	LeadSource findById(Long id);
	
	void validateRequest(LeadSourceDto leadSourceDto,Long mvnoId,Integer operation);
	
	void deleteLeadSource(Long leadSourceId);
	
	List<LeadSource> findByName(String name);
	
	Page<LeadSource> search(Long mvnoId,List<Long> buId,PaginationRequestDTO paginationRequestDTO);
}
