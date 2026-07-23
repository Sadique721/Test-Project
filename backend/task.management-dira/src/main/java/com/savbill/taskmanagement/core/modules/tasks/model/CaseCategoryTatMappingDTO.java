package com.savbill.taskmanagement.core.modules.tasks.model;


import com.savbill.taskmanagement.core.modules.tasks.domain.TatQueryFieldMapping;

import java.util.List;

public class CaseCategoryTatMappingDTO {

    Long id;

    private Long caseCategoryId;

    private Long ticketTatMatrixId;

    private Boolean isDeleted = false;

    private Long orderid;

    private List<TatQueryFieldMapping> tatQueryFieldMappingList;

}
