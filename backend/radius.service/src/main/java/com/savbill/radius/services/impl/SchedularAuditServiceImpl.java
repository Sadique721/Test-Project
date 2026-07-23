package com.savbill.radius.services.impl;

import com.savbill.radius.entity.SchedularAudit;
import com.savbill.radius.repository.SchedularAuditRepository;
import com.savbill.radius.services.SchedularAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchedularAuditServiceImpl implements SchedularAuditService {

    @Autowired
    private SchedularAuditRepository schedularAuditRepository;

    @Override
    public void saveEntity(SchedularAudit schedularAudit) {
        schedularAuditRepository.save(schedularAudit);
    }
}
