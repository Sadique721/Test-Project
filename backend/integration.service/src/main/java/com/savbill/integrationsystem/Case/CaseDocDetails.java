package com.savbill.integrationsystem.Case;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblcasedocdetails")
public class CaseDocDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long docId;

    @Column(name = "ticket_id")
    private Long ticketId;
    private String docStatus;
    private String filename;
    private String uniquename;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

}
