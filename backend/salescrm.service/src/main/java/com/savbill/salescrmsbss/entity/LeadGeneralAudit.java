package com.savbill.salescrmsbss.entity;

import lombok.Data;


import javax.persistence.*;

@Data
@Entity
@Table(name = "tblleadgeneraledit")
public class LeadGeneralAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gen_audit_id", nullable = false)
    private Long id;

    @Column(name="lead_id")
    private Long leadId;

    @Column(name = "old_value", nullable = false)
    private String oldValue;

    @Column(name = "new_value", nullable = false)
    private String newValue;

    @Column(name = "remark", nullable = true)
    private String remark;

    @Column(name = "remark_type", nullable = true)
    private String remarkType;

    @Column(name="status")
    private String status;

    @Column(name="entity_type")
    private String EntityType;

    @Column(name="operation")
    private String operation;

    @Column(name = "create_date_string")
    private String createDateString;

    @Column(name = "update_date_string")
    private String updateDateString;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;






}
