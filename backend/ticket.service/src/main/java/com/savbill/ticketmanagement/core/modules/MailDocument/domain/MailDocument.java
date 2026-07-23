package com.savbill.ticketmanagement.core.modules.MailDocument.domain;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmaildocument")
public class MailDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long docId;

    @Column(name = "mail_id")
    private String mailId;
    @Column(name = "doc_status")
    private String docStatus;
    @Column(name = "filename")
    private String filename;

    @Column(name = "uniquename")
    private String uniquename;

}
