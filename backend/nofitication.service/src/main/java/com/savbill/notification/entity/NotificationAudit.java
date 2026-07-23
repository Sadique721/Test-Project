package com.savbill.notification.entity;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblnotificationaudit")
@NoArgsConstructor
public class NotificationAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="notificationauditid")
    private Long id;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "action")
    private String action;

    @Column(name = "staffid")
    private Long staffid;

    @Column(name = "custid")
    private Integer custid;

    @Column(name = "partnerid")
    private Long partnerid;

    @Column(name = "message_date")
    private LocalDateTime MessageDate;

    @Column(name = "message")
    private String Message;

    @Column(name = "username")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getStaffid() {
        return staffid;
    }

    public void setStaffid(Long staffid) {
        this.staffid = staffid;
    }

    public Integer getCustid() {
        return custid;
    }

    public void setCustid(Integer custid) {
        this.custid = custid;
    }

    public Long getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(Long partnerid) {
        this.partnerid = partnerid;
    }

    public LocalDateTime getMessageDate() {
        return MessageDate;
    }

    public void setMessageDate(LocalDateTime messageDate) {
        MessageDate = messageDate;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
