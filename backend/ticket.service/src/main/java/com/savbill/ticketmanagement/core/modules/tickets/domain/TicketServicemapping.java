package com.savbill.ticketmanagement.core.modules.tickets.domain;

import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltticketservicemapping")
public class TicketServicemapping
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ticket_id")
    private Long ticketid;

    @Column(name = "service_id")
    private Long serviceid;
}
