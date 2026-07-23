package com.savbill.salescrmsbss.AuditLog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditSearchPojo {

    String moduleName;
    String entityName;
    Integer pageIndex;
    Integer pageSize;
    LocalDate startDate ;
    LocalDate endDate;

}
