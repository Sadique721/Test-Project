package com.savbill.salescrmsbss.service.Impl;

import com.savbill.salescrmsbss.entity.PlanGroup;
import com.savbill.salescrmsbss.rabbitMq.message.PlanGroupMsg;
import com.savbill.salescrmsbss.repository.PlanGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanGroupService {
    @Autowired
    PlanGroupRepository planGroupRepository;
    public void save(PlanGroupMsg message) {
        PlanGroup planGroup = new PlanGroup(message);
        planGroupRepository.save(planGroup);
        }


    public void update(PlanGroupMsg message){

        PlanGroup planGroup = new PlanGroup(message);

        //remove old entry from the plangroup table
        planGroupRepository.deleteById(message.getPlanGroupId());

        //add new entry in to the plangroup table
        planGroupRepository.save(planGroup);
    }

}
