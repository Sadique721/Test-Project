package com.savbill.ticketmanagement.core.modules.tickets.domain;

import lombok.Getter;
import lombok.Setter;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tbltticketsubcategoryreasoncategorymapping")
public class TicketSubCategoryReasonCategoryMapping {

    @DiffIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @DiffIgnore
    @Column(name = "ticket_reason_category_id", nullable = false)
    private Long ticketReasonCategoryId;

    @DiffIgnore
    @Column(name = "ticket_reason_sub_category_id", nullable = false)
    private Long ticketReasonSubCategoryId;
}
