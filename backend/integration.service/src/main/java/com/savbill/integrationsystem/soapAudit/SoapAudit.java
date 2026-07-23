package com.savbill.integrationsystem.soapAudit;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tblmsoapaudit")
public class SoapAudit {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_time", updatable = false)
    private LocalDateTime actionTime;

    @Column(name = "requestbody", updatable = false)
    private String requestBody;

    @Column(name = "responsebody", updatable = false)
    private String responseBody;

    @Column(name = "event_name", updatable = false)
    private String eventName;
    @Column(name = "status", updatable = false)
    private Integer status ;

    @Column(name = "parameter", updatable = false)
    private String parameter;

    @Column(name = "actionitem", updatable = false)
    private String actionitem;

    @Column(name = "request_ip_address", updatable = false)
    private String requestIpAddress;
}
