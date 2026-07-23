package com.savbill.ticketmanagement.core.modules.tickets.domain;


import com.savbill.ticketmanagement.core.data.Auditable;
import com.savbill.ticketmanagement.core.modules.common.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmticketassignstaffmapping")
@EntityListeners(AuditableListener.class)
public class TicketAssignStaffMapping extends Auditable<TicketAssignStaffMapping> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   //@DiffIgnore
    private Long id;

    //@DiffIgnore
    @Column(name = "ticket_id")
    private Long ticketId;

    //@DiffIgnore
    private Integer staffId;
}
