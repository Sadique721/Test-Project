package com.savbill.radius.services.impl;

import com.savbill.radius.entity.TimeBasePolicy;
import com.savbill.radius.kafka.message.TimeBasePolicyMessage;
import com.savbill.radius.repository.TimeBasePolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeBasePolicyServiceImp {

    @Autowired
    private TimeBasePolicyRepository timeBasePolicyRepository;

    public TimeBasePolicy save(TimeBasePolicyMessage message){
        try {
            if (message.getData() != null) {
                TimeBasePolicy timeBasePolicy = new TimeBasePolicy(message);
                TimeBasePolicy timeBasePolicySave = timeBasePolicyRepository.save(timeBasePolicy);
                return timeBasePolicySave;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
