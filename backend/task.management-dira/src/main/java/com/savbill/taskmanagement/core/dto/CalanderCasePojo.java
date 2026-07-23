package com.savbill.taskmanagement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalanderCasePojo {

    Integer teamId;
    Integer currentAssigneeId;
    String caseTitle;
    String caseStatus;
    String casePriority;
    String caseType;
    Integer customerId;
}
