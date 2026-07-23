package com.savbill.taskmanagement.core.modules.tasks.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tbltexternalticketlink")
public class ExternalTicketLinkMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="task_id")
    private Integer taskId;

    @Column(name="linked_ticket_id")
    private Integer linkedTicketId;

}
