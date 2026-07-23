package com.savbill.radius.services.impl;

import com.savbill.radius.entity.StaffUser;
import com.savbill.radius.kafka.message.SaveStaffUserSharedDataMessage;
import com.savbill.radius.kafka.message.StaffUserMessage;
import com.savbill.radius.repository.StaffUserRepo;
import com.savbill.radius.repository.StaffUserServiceAreaMappingRepo;
import com.savbill.radius.utils.CustomValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class StaffUserServiceImpl {
    @Autowired
    StaffUserServiceAreaMappingRepo staffUserServiceAreaMappingRepo;

    @Autowired
    StaffUserRepo staffUserRepo;


    public StaffUser savestaffUser(StaffUserMessage staffUserMessage) {
            if (staffUserMessage.getCustomerData() != null) {
                StaffUser staffUser = new StaffUser(staffUserMessage);
                StaffUser staffUsers = staffUserRepo.save(staffUser);
                return staffUsers;

            } else {
                throw new RuntimeException("Staff User Data not found");
            }
    }


    @Transactional
    public void saveStaffUserEntity(SaveStaffUserSharedDataMessage message) throws Exception
    {
        try {
            StaffUser staffUser = new StaffUser();
            staffUser.setId(message.getId());
            staffUser.setUsername(message.getUsername());
            staffUser.setPassword(message.getPassword());
            staffUser.setFirstname(message.getFirstname());
            staffUser.setLastname(message.getLastname());
            staffUser.setStatus(message.getStatus());
            staffUser.setEmail(message.getEmail());
            staffUser.setPhone(message.getPhone());
            staffUser.setPartnerid(message.getPartnerid());
            staffUser.setIsDelete(message.getIsDelete());
            staffUser.setCreatedById(message.getCreatedById());
            staffUser.setLastModifiedById(message.getLastModifiedById());

            if(!message.getLast_login_time().equalsIgnoreCase("null")) {
                staffUser.setLast_login_time(LocalDateTime.parse(message.getLast_login_time()));
            } else {
                staffUser.setLast_login_time(null);
            }
            staffUser.setMvnoId(message.getMvnoId());
            staffUser.setServiceAreaNameList(message.getServiceAreaNameList());
            staffUser.setBusinessUnitNameList(message.getBusinessUnitNameList());
            staffUserRepo.save(staffUser);
        } catch (CustomValidationException e) {}
    }
}

