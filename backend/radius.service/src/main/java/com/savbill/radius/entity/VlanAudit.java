package com.savbill.radius.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@NoArgsConstructor
@Table(name = "tbltvlanaudit")
@Data
public class VlanAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @DiffIgnore
    private Long id;

    @DiffIgnore
    @Column(name = "event_name")
    private String eventName;

    @Column(name = "entity_name")
    private String entitytName;

    @DiffIgnore
    @Column(name = "entity_id")
    private Long entityId;

    @DiffIgnore
    @Column(name = "action_by_id")
    private Integer actionById;

//    @DiffIgnore
//    @Column(name = "entity_name")
//    private String entityName;

    @Column(name = "action_by_name")
    private String actionByName;

    @Column(name = "action")
    private String action;

    @DiffIgnore
    @Column(name = "action_time")
    private LocalDateTime actionDateTime;

    @DiffIgnore
    @Column(name = "remark")
    private String remark;

    @Column(name = "details")
    private String details;

    @Column(name = "filename")
    private String fileName;

    public VlanAudit(VLANManagement vlan, String username, String action, Integer staffId,String details, String fileName) {
        this.action = action;
        this.entitytName=vlan.getVlanName();
        this.actionByName = username;
        this.actionDateTime = LocalDateTime.now();
        this.entityId = vlan.getVlanId();
        this.eventName = new StringBuilder("VLAN "+action).toString();
        this.remark=new StringBuilder("VLAN "+action+"d Succesfully").toString();
        this.actionById = staffId;
        this.details=details;
        this.fileName = fileName;

    }

    public VlanAudit(String entitytName ,String username, String action, Integer staffId, String fileName,String details, String remark) {
        this.actionByName = username;
        this.action = action;
        this.actionById = staffId;
        this.fileName = fileName;
        this.eventName = new StringBuilder("VLAN "+action).toString();
        this.actionDateTime = LocalDateTime.now();
        this.remark=remark;
        this.details=details;
        this.entitytName=entitytName;
    }
}
