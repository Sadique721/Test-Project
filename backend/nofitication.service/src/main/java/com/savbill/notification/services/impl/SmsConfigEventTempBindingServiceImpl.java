package com.savbill.notification.services.impl;

import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.SmsConfig;
import com.savbill.notification.entity.SmsConfigEventTempBinding;
import com.savbill.notification.repository.EventRepository;
import com.savbill.notification.repository.SmsConfigEventTempBindingRepository;
import com.savbill.notification.repository.SmsConfigRepository;
import com.savbill.notification.services.SmsConfigEventTempBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SmsConfigEventTempBindingServiceImpl implements SmsConfigEventTempBindingService {
    @Autowired
    SmsConfigEventTempBindingRepository smsConfigEventTempBindingRepository;

    @Autowired
    SmsConfigRepository smsConfigRepository;

    @Autowired
    EventRepository eventRepository;

    @Override
    @Transactional
    public List<SmsConfigEventTempBinding> saveSmsConfigEventTempBinding(List<Long> smsConfigIdsList, Long eventId){
        List<SmsConfigEventTempBinding> smsConfigEventTempBindingList = new ArrayList<>();
        if(smsConfigIdsList != null && smsConfigIdsList.size() > 0 && eventId != null){
            Event event = eventRepository.findById(eventId).get();
            deleteAllSmsConfigEventTempBindingByEvent(event);
            for (Long smsConfigId: smsConfigIdsList) {
                Optional<SmsConfig> smsConfigOp = smsConfigRepository.findById(smsConfigId);
                if(smsConfigOp.isPresent()){
                    SmsConfigEventTempBinding smsConfigEventTempBinding = new SmsConfigEventTempBinding();
                    smsConfigEventTempBinding.setSmsConfig(smsConfigOp.get());
                    smsConfigEventTempBinding.setEvent(event);
                    smsConfigEventTempBinding = smsConfigEventTempBindingRepository.save(smsConfigEventTempBinding);
                    smsConfigEventTempBindingList.add(smsConfigEventTempBinding);
                }
            }
        }
        return smsConfigEventTempBindingList;
    }

    @Override
    @Transactional
    public List<SmsConfigEventTempBinding> updateSmsConfigEventTempBinding(List<Long> smsConfigIdsList, Long eventId){
        List<SmsConfigEventTempBinding> smsConfigEventTempBindingList = new ArrayList<>();
        if(smsConfigIdsList != null && smsConfigIdsList.size() > 0 && eventId != null){
            Event event = eventRepository.findById(eventId).get();
            deleteAllSmsConfigEventTempBindingByEvent(event);
            for (Long smsConfigId: smsConfigIdsList) {
                Optional<SmsConfig> smsConfigOp = smsConfigRepository.findById(smsConfigId);
                if(smsConfigOp.isPresent()){
                    SmsConfigEventTempBinding smsConfigEventTempBinding = new SmsConfigEventTempBinding();
                    smsConfigEventTempBinding.setSmsConfig(smsConfigOp.get());
                    smsConfigEventTempBinding.setEvent(event);
                    smsConfigEventTempBinding = smsConfigEventTempBindingRepository.save(smsConfigEventTempBinding);
                    smsConfigEventTempBindingList.add(smsConfigEventTempBinding);
                }
            }
        }
        return smsConfigEventTempBindingList;
    }

    @Override
    public List<SmsConfigEventTempBinding> findAllSmsConfigEventTempBindingByEvent(Long eventId){
        Event event = eventRepository.findById(eventId).get();
        return smsConfigEventTempBindingRepository.findAllByEvent(event);
    }

    @Override
    public List<SmsConfigEventTempBinding> findAllSmsConfigEventTempBindingBySmsConfig(Long smsConfigId){
        SmsConfig smsConfig = smsConfigRepository.findById(smsConfigId).get();
        return smsConfigEventTempBindingRepository.findAllBySmsConfig(smsConfig);
    }

    @Override
    public List<SmsConfigEventTempBinding> findAllSmsConfigEventTempBindingByEventAndSmsConfig(Long eventId, Long smsConfigId){
        Event event = eventRepository.findById(eventId).get();
        SmsConfig smsConfig = smsConfigRepository.findById(smsConfigId).get();
        return smsConfigEventTempBindingRepository.findAllByEventAndSmsConfig(event,smsConfig);
    }

    @Override
    @Transactional
    public void deleteAllSmsConfigEventTempBindingByEvent(Event event){
        smsConfigEventTempBindingRepository.deleteAllByEvent(event);
    }
    @Override
    @Transactional
    public void deleteAllSmsConfigEventTempBindingBySmsConfig(Long smsConfigId){
        SmsConfig smsConfig = smsConfigRepository.findById(smsConfigId).get();
        smsConfigEventTempBindingRepository.deleteAllBySmsConfig(smsConfig);
    }

    @Override
    @Transactional
    public void deleteAllSmsConfigEventTempBindingByEventAndSmsConfig(Long eventId, Long smsConfigId){
        Event event = eventRepository.findById(eventId).get();
        SmsConfig smsConfig = smsConfigRepository.findById(smsConfigId).get();
        smsConfigEventTempBindingRepository.deleteAllByEventAndSmsConfig(event,smsConfig);
    }
}
