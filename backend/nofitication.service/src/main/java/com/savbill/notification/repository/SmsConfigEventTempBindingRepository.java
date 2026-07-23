package com.savbill.notification.repository;

import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.SmsConfig;
import com.savbill.notification.entity.SmsConfigEventTempBinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmsConfigEventTempBindingRepository extends JpaRepository<SmsConfigEventTempBinding, Long> {
    List<SmsConfigEventTempBinding> findAllByEvent(Event event);

    void deleteAllByEvent(Event event);

    void deleteAllByEventAndSmsConfig(Event event, SmsConfig smsConfig);

    void deleteAllBySmsConfig(SmsConfig smsConfig);

    List<SmsConfigEventTempBinding> findAllBySmsConfig(SmsConfig smsConfig);

    List<SmsConfigEventTempBinding> findAllByEventAndSmsConfig(Event event, SmsConfig smsConfig);

    List<SmsConfigEventTempBinding> findAllByEventIn(List<Event> events);
}
