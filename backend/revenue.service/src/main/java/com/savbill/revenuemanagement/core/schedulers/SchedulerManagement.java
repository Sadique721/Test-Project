package com.savbill.revenuemanagement.core.schedulers;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="tblmschedulers")
public class SchedulerManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "scheduler_name", nullable = false)
    private SchedulerName schedulerName;

    @Column(name = "scheduler_time", nullable = false)
    private String schedulerTime;

    @Column(name = "schedule_type",nullable = false)
    private String scheduleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Weekly weekly;

    @Column(name = "day_of_month")
    private Long dayOfMonth;

    @Column(name = "status")
    private String status;

    @Column(name = "mvno_id", nullable = false)
    private Long mvnoId;

}
