package com.savbill.notification.services;

import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.SmsConfigEventTempBinding;

import java.util.List;

public interface SmsConfigEventTempBindingService {
    List<SmsConfigEventTempBinding> saveSmsConfigEventTempBinding(List<Long> smsConfigIdsList, Long eventId);

    List<SmsConfigEventTempBinding> updateSmsConfigEventTempBinding(List<Long> smsConfigIdsList, Long eventId);

    List<SmsConfigEventTempBinding> findAllSmsConfigEventTempBindingByEvent(Long eventId);

    List<SmsConfigEventTempBinding> findAllSmsConfigEventTempBindingBySmsConfig(Long smsConfigId);

    List<SmsConfigEventTempBinding> findAllSmsConfigEventTempBindingByEventAndSmsConfig(Long eventId, Long smsConfigId);

    void deleteAllSmsConfigEventTempBindingByEvent(Event event);

    void deleteAllSmsConfigEventTempBindingBySmsConfig(Long smsConfigId);

    void deleteAllSmsConfigEventTempBindingByEventAndSmsConfig(Long eventId, Long smsConfigId);
}
