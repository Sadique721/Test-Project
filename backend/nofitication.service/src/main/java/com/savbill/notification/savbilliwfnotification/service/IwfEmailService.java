package com.savbill.notification.savbilliwfnotification.service;

import com.savbill.notification.savbilliwfnotification.dto.DataMaster;
import com.savbill.notification.entity.Email;
import org.springframework.data.domain.Page;


public interface IwfEmailService {

//    public void sendEmailNotification(DataMaster dataMaster);

    Page<Email> getEmailAudits(int page, int pageSize, Long mvnoId);

    public void sendEmailTempBindNotification(DataMaster dataMaster);
}
