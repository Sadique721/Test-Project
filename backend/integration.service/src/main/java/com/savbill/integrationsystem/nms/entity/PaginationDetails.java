package com.savbill.integrationsystem.nms.entity;

import lombok.Data;

@Data
public class PaginationDetails {

    private Integer totalPages;
    private Long totalRecords;
    private Integer totalRecordsPerPage;
    private Integer currentPageNumber;
}
