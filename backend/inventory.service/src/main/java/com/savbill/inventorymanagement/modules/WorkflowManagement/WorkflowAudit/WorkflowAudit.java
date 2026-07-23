package com.savbill.inventorymanagement.modules.WorkflowManagement.WorkflowAudit;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tblmworkflowaudit")
public class WorkflowAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private Integer eventId;
    private String eventName;
    private Integer entityId;
    private String entityName;
    private Integer actionByStaffId;
    private String actionByName;
    private String action;
    private LocalDateTime actionDateTime;
    private String remark;
    @Column(name = "cust_id")
    private Integer custId;
    @Column(name = "approval_status")
    private String approvalStatus;


}
