package com.savbill.taskmanagement.core.modules.tasks.model;

public interface TaskApprovalProjection {

    Long getCaseId();
    String getCurrentAssigneeName();
    String getCaseSubCategoryName();
    Integer getTeamId();
    String getTeamName();
    Long getCaseSubCategoryId();
    Integer getCurrentAssigneeId();
}