package com.savbill.radius.services;

import com.savbill.radius.helper.RequestDto;
import org.springframework.data.domain.Page;

import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.AuthResponse;

import javax.servlet.http.HttpServletRequest;

public interface AuthResponseService {
	
	Page<AuthResponse> findAllAuthResponse(Integer mvnoId, PaginationDTO paginationDTO, RequestDto requestDto, HttpServletRequest request);
	void deleteAuthResponseById(Long id, Integer mvnoId);
	Page<AuthResponse> findAuthResponseByUserName(PaginationDTO paginationDTO ,String userName, Integer mvnoId);
}
