package com.savbill.notification.services;

import com.savbill.notification.entity.EmailConfig;
import com.savbill.notification.helper.EmailConfigDto;
import com.savbill.notification.helper.PaginationRequestDTO;
import com.savbill.notification.helper.PasswordDto;
import com.savbill.notification.helper.UpdateEmailConfigDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface EmailConfigService {
    EmailConfig addEmailConfig(EmailConfigDto emailConfigDto, Long mvnoId, Long buId);

    EmailConfig updateEmailConfig(UpdateEmailConfigDto emailConfigDto, Long mvnoId, Long buId);

    List<EmailConfig> findAllEmailConfig(Long mvnoId, Long buId, String serviceType);

    void changePassword(PasswordDto passwordDto);

    EmailConfig findEmailConfigById(Long emailConfigId, Long mvnoId);

    void validateEmailConfigData(EmailConfigDto emailConfigDto, Long mvnoId);

    void validateEmailConfigDataOnUpdate(UpdateEmailConfigDto emailConfigDto, Long mvnoId);

    void removeEmailConfigById(Long id);

    Page<EmailConfigDto> filterEmailConfigByName(Map<String, Object> criteriaMap);

    /**
     * Get Event Template Binding With Pagination
     *
     * @param page
     * @param size
     * @return
     */
    Page<EmailConfigDto> getEmailConfigWithPagination(Integer page, Integer size, Long mvnoId, Long buIds, String serviceType);

    Page<EmailConfigDto> searchEmailConfig(PaginationRequestDTO requestDTO, Long mvnoId, String serviceType);

    boolean validation(PaginationRequestDTO requestDTO);

    boolean isSmtpAuthenticated(boolean isSmtpAuthenticated, String authenticationType, String hostServer, String port, String userName, String password);

    void validateSMTPAuthentication(boolean smtpAuth, String authType, String hostServer, String port, String userName, String password);
}
