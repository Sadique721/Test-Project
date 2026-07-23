package com.savbill.radius.services;

import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.FaultyMAC;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface FaultyMACKService {
    public FaultyMAC saveMack(FaultyMAC mack);
    public FaultyMAC updateMack(FaultyMAC mack);
    public void deleteMack(String mack,Integer mvnoId);
    public FaultyMAC findByMacId(String mackId,Integer mvnoId);
    public FaultyMAC findById(Long macId);

    Page<FaultyMAC> getAll(Integer mvnoId, PaginationDTO paginationDTO, HttpServletRequest request);

    void uploadXl(MultipartFile file, Integer mvnoId);
}
