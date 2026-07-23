package com.savbill.salescrmsbss.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;

import com.savbill.salescrmsbss.entity.FollowUpRemark;
import com.savbill.salescrmsbss.entity.StaffUser;
import com.savbill.salescrmsbss.helper.FollowUpRemarkDto;
import com.savbill.salescrmsbss.helper.LeadFollowUpDto;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;

public interface LeadFollowUpService {

	void validateRequest(LeadFollowUpDto leadFollowUpDto,Integer operation);

	LeadFollowUpDto save(LeadFollowUpDto leadFollowUpDto,Integer staffId);
	
	LeadFollowUpDto update(LeadFollowUpDto leadFollowUpDto,HttpServletRequest req);
	
	LeadFollowUpDto findById(Long followUpId);
	
	List<LeadFollowUpDto> findAll();
	
	List<LeadFollowUpDto> findAllByLeadId(Long leadId);
	
	List<StaffUser> findStaffUserByLeadId(Long leadId);
	
	void closeFollowUp(Long followUpId,String remarks,Integer staffId);
	
	void closeAndReScheduleFollowUp(Long followUpId,String remarks,LeadFollowUpDto leadFollowUpDto,Integer staffId);

	FollowUpRemarkDto saveFollowUpRemark(FollowUpRemarkDto followUpRemarkDto);
	
	List<FollowUpRemark> findAllFollowUpRemarkByFollowUpId(Long followUpId);

	String generateNameOfTheFollowUp(Long leadId);
	
	Page<LeadFollowUpDto> findAllByAssignId(Long assignId,PaginationRequestDTO paginationRequestDTO);

	Page<LeadFollowUpDto> findAllByAssignIdAndTeam(Long assignId,PaginationRequestDTO paginationRequestDTO);

	String getFollowUpNameById(Long followUpId);

}
