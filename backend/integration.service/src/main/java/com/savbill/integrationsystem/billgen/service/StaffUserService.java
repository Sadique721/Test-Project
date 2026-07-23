package com.savbill.integrationsystem.billgen.service;

import com.savbill.integrationsystem.billgen.entity.StaffUser;
import com.savbill.integrationsystem.billgen.repository.StaffUserRepository;
import com.savbill.integrationsystem.rabbitmq.SaveStaffUserSharedDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaffUserService {

    @Autowired
    StaffUserRepository staffUserRepository;

    public void save(SaveStaffUserSharedDataMessage message) {
        StaffUser staffUser = new StaffUser(message);
        staffUserRepository.save(staffUser);
    }
}
