package com.savbill.taskmanagement.core.modules.MailConfigration.service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;

public interface ReceiveMailService {

    void handleReceivedMail(MimeMessage message);

    public List<String> mailScrapping(String mail) throws IOException;

    void SendExternalRemarkMailInThread(Long mvnoId, Long buId , String caseNumber, String remark);

}
