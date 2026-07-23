package com.savbill.radius.services;


import com.savbill.radius.dto.CDRSearchDTO;
import com.savbill.radius.helper.AcctCdrSearchDTO;
import com.savbill.radius.helper.AcctShowDTO;
import org.springframework.data.domain.Page;

import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.AcctCdr;

import javax.servlet.http.HttpServletRequest;

public interface AcctCdrService {

	Page<AcctCdr> findAllAcctCdr(Integer mvnoId, PaginationDTO paginationDTO, HttpServletRequest request);
	void deleteAcctCdrById(Long id, Integer mvnoId);
	Page<AcctCdr> findAcctCrdUsingFilter(CDRSearchDTO paginationDTO, Integer mvnoId);
	AcctCdr findAcctCdrById(Long cdrId, Integer mvnoId);

   Page<AcctShowDTO> findAcctCdrByRequest(AcctCdrSearchDTO acctCdrSearchDTO , Integer mvnoId);
}
