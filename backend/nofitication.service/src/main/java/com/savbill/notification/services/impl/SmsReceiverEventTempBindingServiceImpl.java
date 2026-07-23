package com.savbill.notification.services.impl;

import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.SmsReceiverEventTempBinding;
import com.savbill.notification.helper.StaffCustomDTO;
import com.savbill.notification.repository.EventRepository;
import com.savbill.notification.repository.SmsReceiverEventTempBindingRepository;
import com.savbill.notification.services.SmsReceiverEventTempBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class SmsReceiverEventTempBindingServiceImpl implements SmsReceiverEventTempBindingService {

    @Autowired
    SmsReceiverEventTempBindingRepository smsReceiverEventTempBindingRepository;

    @Autowired
    EventRepository eventRepository;

    @Override
    @Transactional
    public List<SmsReceiverEventTempBinding> saveSmsReceiverEventTempBinding(List<StaffCustomDTO> staffCustomDTOList, Long eventId) {
        List<SmsReceiverEventTempBinding> smsReceiverEventTempBindingList = new ArrayList<>();
        if(staffCustomDTOList != null && staffCustomDTOList.size() > 0){
            Event event = eventRepository.findById(eventId).get();
            deleteAllSmsReceiverEventTempBindingByEvent(event);
            for (StaffCustomDTO staffCustomDTO : staffCustomDTOList) {
                SmsReceiverEventTempBinding smsReceiverEventTempBinding = new SmsReceiverEventTempBinding();
                smsReceiverEventTempBinding.setStaffId(staffCustomDTO.getId());
                smsReceiverEventTempBinding.setMobileNumber(staffCustomDTO.getMobileNumber());
                smsReceiverEventTempBinding.setStaffFullName(staffCustomDTO.getFullName());
                smsReceiverEventTempBinding.setStaffUsername(staffCustomDTO.getUsername());
                smsReceiverEventTempBinding.setEvent(event);
                smsReceiverEventTempBinding = smsReceiverEventTempBindingRepository.save(smsReceiverEventTempBinding);
                smsReceiverEventTempBindingList.add(smsReceiverEventTempBinding);
            }
        }
        return smsReceiverEventTempBindingList;
    }

    @Override
    @Transactional
    public List<SmsReceiverEventTempBinding> updateSmsReceiverEventTempBinding(List<StaffCustomDTO> staffCustomDTOList, Long eventId) {
        List<SmsReceiverEventTempBinding> smsReceiverEventTempBindingList = new ArrayList<>();
        if(staffCustomDTOList != null && staffCustomDTOList.size() > 0){
            Event event = eventRepository.findById(eventId).get();
            deleteAllSmsReceiverEventTempBindingByEvent(event);
            for (StaffCustomDTO staffCustomDTO : staffCustomDTOList) {
                SmsReceiverEventTempBinding smsReceiverEventTempBinding = new SmsReceiverEventTempBinding();
                smsReceiverEventTempBinding.setStaffId(staffCustomDTO.getId());
                smsReceiverEventTempBinding.setMobileNumber(staffCustomDTO.getMobileNumber());
                smsReceiverEventTempBinding.setStaffFullName(staffCustomDTO.getFullName());
                smsReceiverEventTempBinding.setStaffUsername(staffCustomDTO.getUsername());
                smsReceiverEventTempBinding.setEvent(event);
                smsReceiverEventTempBinding = smsReceiverEventTempBindingRepository.save(smsReceiverEventTempBinding);
                smsReceiverEventTempBindingList.add(smsReceiverEventTempBinding);
            }
        }
        return smsReceiverEventTempBindingList;
    }

    @Override
    public List<SmsReceiverEventTempBinding> findAllSmsReceiverEventTempBindingByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        return smsReceiverEventTempBindingRepository.findAllByEvent(event);
    }

    @Override
    @Transactional
    public void deleteAllSmsReceiverEventTempBindingByEvent(Event event) {
        smsReceiverEventTempBindingRepository.deleteAllByEvent(event);
    }
}
