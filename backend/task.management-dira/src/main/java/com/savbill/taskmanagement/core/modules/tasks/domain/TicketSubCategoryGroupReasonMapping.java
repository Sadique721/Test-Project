package com.savbill.taskmanagement.core.modules.tasks.domain;

import lombok.Getter;
import lombok.Setter;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tbltticketsubcategorygroupreasonmapping")
public class TicketSubCategoryGroupReasonMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DiffIgnore
    private Long id;

    String reason;
    @DiffIgnore
    @Column(name = "ticket_reason_sub_category_id", nullable = false)
    private Long ticketReasonSubCategoryId;
}
