package com.savbill.salescrmsbss.helper;

import lombok.Data;

@Data
public class GenericSearchModel {

	private String filterColumn;
    private String filterOperator;
    private String filterValue;
    private String filterDataType;
    private String filterCondition;
}
