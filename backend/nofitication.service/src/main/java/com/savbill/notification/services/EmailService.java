package com.savbill.notification.services;

import com.savbill.notification.entity.Email;
import com.savbill.notification.helper.*;
import com.savbill.notification.helper.*;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface EmailService {
    List<EmailDataDTO> findEmailBySourceName(Long eventId, String status, String emailAddress, Long mvnoId);

    Email findEmailById(Long id, Long mvnoId, Boolean isUpdateOrDelete);

    PageableResponse<EmailDataDTO> findAllEmails(Long eventId, String status, String emailAddress, Long mvnoId, PaginationDTO paginationDTO, List<Long> buIdlist);

    void deleteEmailById(Long id, Long mvnoId);

    Email saveEmail(EmailDto email, Long mvnoId);

    Email updateEmail(UpdateEmailDto updateEmailDto, Long mvnoId, Long aLong, HttpServletRequest request);

    void reSendEmail(Long id);

    void sendEmailNotification(String queueName, String message, Map<String, Object> data, String sourceName, String emailTemplate, String smsTemplate, String appendUrl, boolean isEmailConfigured, boolean isSmsConfigured);

    void sendEmailwithAttachments(Long id, Long mvnoId);

    void sendEmailNotificationWithAttachment(String queueName, String message, Map<String, Object> data, String sourceName, String emailTemplate, String smsTemplate, String appendUrl, boolean isEmailConfigured, boolean isSmsConfigured);

    void sendEmailNotificationWithAttachmentForAllEvents(String queueName, String message, Map<String, Object> data, String sourceName, String emailTemplate, String smsTemplate, String appendUrl, boolean isEmailConfigured, boolean isSmsConfigured);

    public Page<EmailDto> searchEmailAudit(PaginationRequestDTO dto, Long mvnoId, String servicetype);

    boolean validation(PaginationRequestDTO dto);
}
