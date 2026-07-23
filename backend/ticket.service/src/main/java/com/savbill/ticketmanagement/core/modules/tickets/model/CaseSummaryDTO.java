package com.savbill.ticketmanagement.core.modules.tickets.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class CaseSummaryDTO {
    private Long caseId;
    private String caseStatus;
    private LocalDateTime createDate;
    private LocalTime nextFollowupTime;

    public CaseSummaryDTO(Long caseId, String caseStatus, LocalDateTime createDate, LocalTime nextFollowupTime) {
        this.caseId = caseId;
        this.caseStatus = caseStatus;
        this.createDate = createDate;
        this.nextFollowupTime = nextFollowupTime;
    }
}
