package com.savbill.ticketmanagement.core.modules.tickets.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TicketTatAuditPojo {


    private Integer id;


    private Integer caseId;


    private String caseStatus;



    private String tatAction;


    private Integer tatTime;


    private String tatUnit;


    private Integer slaTime;


    private String slaUnit;


    private LocalDateTime tatStartTime;



    private String tatMessage;


    private Integer assignStaffId;


    private Integer assignStaffParentId;


    private String caseLevel;


    private String notificationFor;


    private String isTatBreached;


    private String isSlaBreached;

}
