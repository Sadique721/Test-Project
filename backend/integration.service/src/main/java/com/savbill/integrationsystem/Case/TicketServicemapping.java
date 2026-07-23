package com.savbill.integrationsystem.Case;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tbltticketservicemapping")
public class TicketServicemapping
{
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "ticket_id")
    private Long ticketid;

    @Column(name = "service_id")
    private Long serviceid;

    public TicketServicemapping() {
    }

    public TicketServicemapping(List<TicketServicemapping> ticketServicemappingList) {
        if (ticketServicemappingList != null) {
            for (TicketServicemapping mapping : ticketServicemappingList) {
                this.id = mapping.getId();
                this.ticketid = mapping.getTicketid();
                this.serviceid = mapping.getServiceid();
            }
        }

    }
}
