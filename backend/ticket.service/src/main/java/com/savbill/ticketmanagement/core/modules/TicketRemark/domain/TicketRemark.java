package com.savbill.ticketmanagement.core.modules.TicketRemark.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblmticketremark")
public class TicketRemark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "custid")
    private Integer custId;

    @Column(name = "ticket_no")
    private String ticketNo;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "staff_id")
    private Integer staffId;

    @Column(name = "internal_remarks")
    private String internalRemarks;

    @Column(name = "external_remarks")
    private String externalRemarks;

    @Column(name = "is_from_customer")
    private Boolean isFromCustomer;

    @Column(name = "CREATEDATE")
    private LocalDateTime createdate;

    @Column(name = "common_domain")
    private String commonDomain;
}
