package com.savbill.notification.repository;

import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.SmsReceiverEventTempBinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmsReceiverEventTempBindingRepository extends JpaRepository<SmsReceiverEventTempBinding, Long> {
    void deleteAllByEvent(Event event);

    List<SmsReceiverEventTempBinding> findAllByEvent(Event event);

    List<SmsReceiverEventTempBinding> findAllByEventIn(List<Event> events);
}