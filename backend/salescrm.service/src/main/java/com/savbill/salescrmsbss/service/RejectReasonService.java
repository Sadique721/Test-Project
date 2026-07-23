package com.savbill.salescrmsbss.service;

import javax.servlet.http.HttpServletRequest;

import com.savbill.salescrmsbss.entity.RejectReason;
import org.springframework.data.domain.Page;

import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.helper.RejectReasonDto;

import java.util.List;

public interface RejectReasonService {

    RejectReasonDto saveRejectReason(RejectReasonDto rejectReasonDto,Long mvnoId,Long buId);
	
    RejectReasonDto updateRejectReason(RejectReasonDto rejectReasonDto,HttpServletRequest req);
		
	RejectReasonDto findById(Long id);

	RejectReason getByID(Long id);
	
	void validateRequest(RejectReasonDto rejectReasonDto,Long mvnoId,Integer operation);
	
	void deleteRejectReason(Long rejectReasonId);
		
	Page<RejectReasonDto> search(PaginationRequestDTO paginationRequestDTO);
	
	Page<RejectReasonDto> search(Long mvnoId,List<Long> buId,PaginationRequestDTO paginationRequestDTO);
	
	Page<RejectReasonDto> findAll(PaginationRequestDTO paginationRequestDTO);
	
	Page<RejectReasonDto> findAll(Long mvnoId, List<Long> buId, PaginationRequestDTO paginationRequestDTO);

	boolean isDuplicate(String name);
}
