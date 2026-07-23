package com.savbill.ticketmanagement.core.modules.tickets.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TicketServicemappingDTO
{
    private Long id;
    private Long ticketid;
    private Long serviceid;
}
