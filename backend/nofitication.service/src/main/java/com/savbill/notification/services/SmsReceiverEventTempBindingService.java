package com.savbill.notification.services;

import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.SmsReceiverEventTempBinding;
import com.savbill.notification.helper.StaffCustomDTO;

import java.util.List;

public interface SmsReceiverEventTempBindingService {

    List<SmsReceiverEventTempBinding> saveSmsReceiverEventTempBinding(List<StaffCustomDTO> staffCustomDTOList, Long eventId);

    List<SmsReceiverEventTempBinding> updateSmsReceiverEventTempBinding(List<StaffCustomDTO> staffCustomDTOList, Long eventId);

    List<SmsReceiverEventTempBinding> findAllSmsReceiverEventTempBindingByEvent(Long eventId);

    void deleteAllSmsReceiverEventTempBindingByEvent(Event event);

}
