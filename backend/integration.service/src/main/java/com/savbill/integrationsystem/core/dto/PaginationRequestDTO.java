package com.savbill.integrationsystem.core.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationRequestDTO {
    private Integer page;
    private Integer pageSize;
    private Integer sortOrder;
    private String sortBy="createdate";
    private List<GenericSearchModel> filters = new ArrayList<>();
    private String status;
    private String filterBy;
}
