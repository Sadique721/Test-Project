package com.savbill.ticketmanagement.core.modules.tickets.model;

import lombok.Data;

import java.util.List;

@Data
public class TicketFileUploadDTO {

    private List<SectionUploadDTO> sections;

}
