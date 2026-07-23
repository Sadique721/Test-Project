package com.savbill.ticketmanagement.core.auditLog.model;


import com.savbill.ticketmanagement.core.dto.PaginationRequestDTO;
import lombok.Data;

import java.sql.Date;

@Data
public class AuditLogSearchRequestDTO extends PaginationRequestDTO {

    private Date fromDate;
    private Date toDate;
    private String module;
    private String auditFor;
    private String operation;
    private Integer partnerId;
    private Integer customerId;

}
