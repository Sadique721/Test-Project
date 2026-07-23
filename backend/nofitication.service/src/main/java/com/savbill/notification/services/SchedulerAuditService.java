package com.savbill.notification.services;

import com.savbill.notification.entity.SchedulerAudit;
import com.savbill.notification.repository.SchedulerAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchedulerAuditService {

    @Autowired
    private SchedulerAuditRepository schedulerAuditRepository;

    public void saveEntity(SchedulerAudit schedulerAudit) {

        schedulerAuditRepository.save(schedulerAudit);
    }
}