package com.savbill.radius.services.impl;

import com.savbill.radius.entity.TimeBasePolicyDetails;
import com.savbill.radius.kafka.message.TimeBasePolicyDetailsMessage;
import com.savbill.radius.repository.TimeBasePolicyDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TimeBasePolicyDetailServiceImpl {
    @Autowired
    TimeBasePolicyDetailsRepository timeBasePolicyDetailsRepository;

    public TimeBasePolicyDetails save(TimeBasePolicyDetailsMessage message){
        try {
            if (message.getData() != null) {
                TimeBasePolicyDetails  timeBasePolicyDetails = new TimeBasePolicyDetails(message);
                TimeBasePolicyDetails timeBasePolicyDetailsSave = timeBasePolicyDetailsRepository.save(timeBasePolicyDetails);
                return timeBasePolicyDetailsSave;
            } else {
                throw new RuntimeException("INVALID_DATA");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void saveAll(List<TimeBasePolicyDetailsMessage> timeBasePolicyDetailsMessageList){
        try {
            for(TimeBasePolicyDetailsMessage timeBasePolicyDetailsMessage : timeBasePolicyDetailsMessageList){
                save(timeBasePolicyDetailsMessage);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
