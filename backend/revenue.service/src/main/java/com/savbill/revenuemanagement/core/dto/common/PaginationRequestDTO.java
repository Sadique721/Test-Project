package com.savbill.revenuemanagement.core.dto.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PaginationRequestDTO {
    private Integer page;
    private Integer pageSize;
    private Integer sortOrder;
    private String sortBy;
    private List<GenericSearchModel> filters = new ArrayList<>();
    private String status;
    private String filterBy;
    private Date fromDate;
    private Date toDate;
}
