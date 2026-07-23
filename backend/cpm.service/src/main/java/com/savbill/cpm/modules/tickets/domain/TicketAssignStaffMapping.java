package com.savbill.cpm.modules.tickets.domain;

import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.spring.security.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmticketassignstaffmapping")
@EntityListeners(AuditableListener.class)
public class TicketAssignStaffMapping extends Auditable<TicketAssignStaffMapping> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id")
    private Long ticketId;

    private Integer staffId;
}
