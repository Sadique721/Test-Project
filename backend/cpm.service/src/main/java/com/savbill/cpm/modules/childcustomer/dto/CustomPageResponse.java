package com.savbill.cpm.modules.childcustomer.dto;

import lombok.Data;

import java.util.List;
@Data
public class CustomPageResponse<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
}
