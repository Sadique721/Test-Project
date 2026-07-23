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
public class TaskApprovalDTO extends Auditable implements IBaseDto {

    private Long caseId;
    private String currentAssigneeName;
    private String caseSubCategoryName;
    private Integer teamId;
    private String teamName;
    private Long caseSubCategoryId;
    private Integer currentAssigneeId;
    private Integer mvnoId;
    private Long buId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return caseId;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    public TaskApprovalDTO(Long caseId, String currentAssigneeName, String caseSubCategoryName, Integer teamId, String teamName, Long caseSubCategoryId, Integer currentAssigneeId) {
        this.caseId = caseId;
        this.currentAssigneeName = currentAssigneeName;
        this.caseSubCategoryName = caseSubCategoryName;
        this.teamId = teamId;
        this.teamName = teamName;
        this.caseSubCategoryId = caseSubCategoryId;
        this.currentAssigneeId = currentAssigneeId;
    }
}
