package com.savbill.integrationsystem.nms.entity;


import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "tblmnmscustdetails")
public class NMSCustDetails {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @Column(name = "cust_id", nullable = false, length = 40)
    private Long custId;

    @Column(name = "cust_serv_map_id", nullable = false, length = 40)
    private Long custServMapId;

    @Column(name = "stage", nullable = false, length = 60)
    private String stage;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Column(name="LASTMODIFIEDDATE")
    private String modifyDate;

    @Column(name = "username_for_audit")
    private String usernameForAudit;
}
