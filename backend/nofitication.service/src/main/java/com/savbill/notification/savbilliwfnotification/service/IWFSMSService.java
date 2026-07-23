package com.savbill.notification.savbilliwfnotification.service;

import com.savbill.notification.savbilliwfnotification.dto.CommonNotificationDto;
import com.savbill.notification.entity.*;
import com.savbill.notification.entity.*;
import org.springframework.data.domain.Page;

public interface IWFSMSService {
    void sendIWFSMSNotification(SmsReceiverEventTempBinding smsReceiverEventTempBinding, SmsConfigEventTempBinding smsConfigEventTempBinding, SmsConfig smsConfig, Template template, Event event, CommonNotificationDto dto);
    Page<Sms> getSmsAudits(int page, int pageSize, Long mvnoId);
}
