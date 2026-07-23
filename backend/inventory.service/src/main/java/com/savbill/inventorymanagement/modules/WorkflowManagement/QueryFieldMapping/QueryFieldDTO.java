package com.savbill.inventorymanagement.modules.WorkflowManagement.QueryFieldMapping;

import lombok.Data;

@Data
public class QueryFieldDTO {

    private Integer teamId;
    private Integer id;
    private String queryField;
    private String queryOperator;
    private String queryValue;
    private String queryCondition;
    private Boolean isDeleted;


}
