package com.savbill.ticketmanagement.core.modules.TicketRemark.model;

import lombok.Data;

@Data
public class TicketRemarkDTO {

    private Integer custId;

    private String ticketNo;

    private Long ticketId;

    private Integer staffId;

    private String internalRemarks;

    private String externalRemarks;

    private Boolean isFromCustomer;
}
