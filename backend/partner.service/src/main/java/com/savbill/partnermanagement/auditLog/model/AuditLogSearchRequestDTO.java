package com.savbill.partnermanagement.auditLog.model;


import com.savbill.partnermanagement.core.dto.PaginationRequestDTO;
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
