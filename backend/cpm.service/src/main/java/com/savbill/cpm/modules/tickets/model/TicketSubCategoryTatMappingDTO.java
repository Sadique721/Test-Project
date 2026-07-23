package com.savbill.cpm.modules.tickets.model;

import com.savbill.cpm.modules.tickets.domain.TatQueryFieldMapping;

import java.util.List;

public class TicketSubCategoryTatMappingDTO {

    Long id;

    private Long ticketReasonSubCategoryId;

    private Long ticketTatMatrixId;

    private Boolean isDeleted = false;

    private Long orderid;

    private List<TatQueryFieldMapping> tatQueryFieldMappingList;

}
