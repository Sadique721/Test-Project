package com.savbill.radius.services;

import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.AcctCdr;
import com.savbill.radius.entity.LiveUser;
import org.springframework.data.domain.Page;

public interface ResellerService {
    AcctCdr findAcctCdrById(Long cdrId, Integer mvnoId, Long locationId);
    Page<AcctCdr> findAcctCrdByUserName(String userName,String framedIp, Integer mvnoId, PaginationDTO paginationDTO, Long locationId);
    Page<AcctCdr> findAllAcctCdr(Integer mvnoId, PaginationDTO paginationDTO, Long locationId);
    void deleteAcctCdrById(Long id, Integer mvnoId, Long locationId);


    LiveUser findLiveUserById(Long id, Integer mvnoId, Long locationId);
    Page<LiveUser> findByUserName(String userName, String framedIpAddress, Integer mvnoId, PaginationDTO paginationDTO, Long locationId);
    Page<LiveUser> getAll(Integer mvnoId, PaginationDTO paginationDTO, Long locationId);
    void delete(Long id, Integer mvnoId, Long locationId);
}
