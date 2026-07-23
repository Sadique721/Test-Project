package com.savbill.taskmanagement.core.modules.tasks.model;

import lombok.Data;

@Data
public class TasksAssignDTO {

    private  Long caseId;

    private Integer staffId;

    private Integer teamId;

    private Boolean isReassignTask;


}
