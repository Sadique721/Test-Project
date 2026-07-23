package com.savbill.radius.services;

import com.savbill.radius.dto.AccessResponseDto;
import com.savbill.radius.entity.AccessResponse;
import com.savbill.radius.utils.PaginationRequestDTO;
import org.springframework.data.domain.Page;

public interface AccessResponseService {

    AccessResponse saveAccessResponse(AccessResponseDto accessResponseDto);

    AccessResponse updateAccessResponse(AccessResponseDto accessResponseDto);

    AccessResponse findAccessResponsebyId(Long id);
    Page<AccessResponse> findAccessResponse(PaginationRequestDTO requestDTO);

    AccessResponse deleteAccessResponsebyId(Long id);


}
