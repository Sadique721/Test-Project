package com.savbill.taskmanagement.core.modules.tasks.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseDocDetails;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseFeedbackRel;
import com.savbill.taskmanagement.core.modules.tasks.domain.TicketAssignStaffMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseListDTO extends Auditable implements IBaseDto {

    private Long caseId;
    private String caseTitle;
    private String caseNumber;
    private String caseType;
    private String caseStatus;
    private LocalDateTime createdate;
    private LocalDateTime updatedate;
    private LocalDate nextFollowupDate;
    private LocalTime nextFollowupTime;
    private Integer currentAssigneeId;
    private String currentAssigneeName;
    private String mvnoName;
    private Integer mvnoId;
    private Long parentId;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return caseId;
    }

    public Integer getCurrentAssigneeId() {
        return currentAssigneeId;
    }

    public void setCurrentAssigneeId(Integer currentAssigneeId) {
        this.currentAssigneeId = currentAssigneeId;
    }

    @Override
    public Long getBuId() {
        return null;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    public CaseListDTO(
            Long caseId,
            String caseTitle,
            String caseNumber,
            String caseType,
            String caseStatus,
            LocalDateTime createdate,
            LocalDateTime updatedate,
            LocalDate nextFollowupDate,
            LocalTime nextFollowupTime,
            Integer currentAssigneeId,
            String currentAssigneeName,
            String mvnoName,
            Integer mvnoId,
            Integer parentStaffId
    ) {
        this.caseId = caseId;
        this.caseTitle = caseTitle;
        this.caseNumber = caseNumber;
        this.caseType = caseType;
        this.caseStatus = caseStatus;
        this.createdate = createdate;
        this.updatedate = updatedate;
        this.nextFollowupDate = nextFollowupDate;
        this.nextFollowupTime = nextFollowupTime;
        this.currentAssigneeId = currentAssigneeId;
        this.currentAssigneeName = currentAssigneeName;
        this.mvnoName = mvnoName;
        this.mvnoId = mvnoId;
        this.parentId = parentStaffId != null ? parentStaffId.longValue() : null;
    }
}
