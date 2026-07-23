package com.savbill.ticketmanagement.core.dto;

import lombok.Data;

import java.util.List;

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
    private List<Object> filterListValues;

}
