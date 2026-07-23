package com.savbill.integrationsystem.core.dto;

import lombok.Data;

@Data
public class GenericSearchModel {

    private String filterColumn;
    private String filterOperator;
    private String filterValue;
    private String filterDataType;
    private String filterCondition;
    private Long serviceArea;
    private Long serviceNetwork;
    private Long port;
    private Long slot;
    private Long salesRepresentative;
    private String fromDate;
    private String toDate;

}
