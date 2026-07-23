package com.savbill.cpm.model.radius;

import lombok.Data;

import java.sql.Date;

import com.savbill.cpm.core.dto.PaginationRequestDTO;

@Data
public class DbCDRRequestDTO extends PaginationRequestDTO  {

    private Date startDate;
    private Date endDate;
    private String username;
    private String requestType;
}
