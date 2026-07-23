package com.savbill.notification.services.impl;

import com.savbill.notification.entity.NotificationAudit;
import com.savbill.notification.repository.NotificationAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class NotificationAuditServiceImpl {

    @Autowired
    private NotificationAuditRepository notificationAuditRepository;

    public void saveNotificationAudit(String username , String event , LocalDateTime eventTime,String message){

        try {
            NotificationAudit notificationAudit = new NotificationAudit();
            notificationAudit.setUsername(username);
            notificationAudit.setEventName(event);
            notificationAudit.setMessage(message);
            notificationAudit.setMessageDate(eventTime.truncatedTo(ChronoUnit.SECONDS));
            notificationAuditRepository.save(notificationAudit);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
