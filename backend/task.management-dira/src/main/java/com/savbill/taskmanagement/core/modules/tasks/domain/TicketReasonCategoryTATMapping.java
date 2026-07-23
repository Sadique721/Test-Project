//package com.savbill.taskmanagement.core.modules.tickets.domain;
//
//
//import lombok.Getter;
//import lombok.Setter;
//import org.javers.core.metamodel.annotation.DiffIgnore;
//
//import javax.persistence.*;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "tbltticketreasoncategorytatmapping")
//public class TicketReasonCategoryTATMapping {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long mappingId;
//
//    private String action;
//
//    private String timeUnit;
//
//    //P3 -Low
//    private Long time;
//
//    @DiffIgnore
//    @Column(name = "ticket_reason_category_id", nullable = false)
//    private Long ticketReasonCategoryId;
//
//    @Column(name = "order_number",nullable = false)
//    private Long orderNumber;
//
//    //P1 -High
//    private Long escalatedTime;
//
//    //P2 - Medium
//    @Column(name = "medium_time")
//    private Long mediumTime;
//
//    @Transient
//    String teamName;
//
//    @Column(name = "level")
//    String level;
//
////    @Transient
////    String teamName;
//
//}
